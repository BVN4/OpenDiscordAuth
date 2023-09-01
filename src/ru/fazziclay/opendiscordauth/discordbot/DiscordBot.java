package ru.fazziclay.opendiscordauth.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.fazziclay.opendiscordauth.*;
import ru.fazziclay.opendiscordauth.runcommand.RunCommandController;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;
    public static WebhookClient webhook;
    public static String serverIp;

    protected RunCommandController rc;

    public DiscordBot() {
        this.rc = new RunCommandController();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Utils.debug("[DiscordBot] onMessageReceived(): message="+event.getMessage().getContentRaw());

        String content           = event.getMessage().getContentRaw();
        User author              = event.getMessage().getAuthor();
        MessageChannel channel   = event.getMessage().getChannel();

        // Проверка на авторство сообщений. Отбрасование сообщений автор которых этот бот или же его вебхук
        if (
                author.getId().equals(DiscordBot.webhook.getWebhook().getId()) ||
                author.getId().equals(DiscordBot.bot.getSelfUser().getId())
        ) {
            return;
        }

        //Проверка на отправку сообщения в канале для ретронсляции; Ретрансляция сообщения из чата Discord в чат Minecraft
        if (channel.getType().isGuild()) {
            if (channel.getId().equals(Config.discordChatIdForTranslation)) {

                // Получение преписки к нику если сообщение было отправлено с другого Minecraft сервера с этим же плагином
                String prefix = "discord";
                if (event.getMessage().isWebhookMessage()) {
                    prefix = author.getName()
                        .split(" ")[0]
                        .replace("[", "")
                        .replace("]", "");
                }

                Bukkit.broadcastMessage(Utils.getMessageForBroadcast(event, prefix));
            }
            return;
        }

        TempCode tempCode = TempCode.getByValue(0, content);
        Utils.debug("[DiscordBot] onMessageReceived(): (check tempCode) tempCode="+tempCode);

        if (tempCode != null) {
            Account account = Account.getByValue(0, tempCode.ownerNickname);

            if (account != null) {
                Utils.debug("[DiscordBot] onMessageReceived(): (account != null) == true");
                if (account.ownerDiscord.equals(author.getId())) {
                    LoginManager.login(tempCode.ownerUUID);

                } else {
                    Utils.sendMessage(channel, Config.messageNotYoursCode);
                }

            } else {
                Utils.debug("[DiscordBot] onMessageReceived(): (account != null) == false");
                Account.create(author, tempCode.ownerNickname);
            }

            tempCode.delete();

        } else {
            Utils.sendMessage(channel, Config.messageCodeNotFound);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        switch (event.getName()) {
            case ("rc") -> rc.eventHandle(event);
            case ("get-ip") -> {
                // TODO: need refactor for discordbot.Controller
                event.deferReply().queue();
                String ip = Utils.getGlobalIp();
                String dnsIp = ElasticwebAPI.getDnsIp();
                int port = Bukkit.getServer().getPort();
                if (!ip.equals(Utils.NULL_IP)) {
                    Bukkit.getLogger().info(ip);
                    event.getHook().editOriginal(
                            Utils.getCrossed(
                                    String.format(
                                            "URL для подключения: `%s.%s:%d`\nDNS IP сервера `%s:%d`\n",
                                            Config.domainProviderServerDomainSubName,
                                            Config.domainProviderDomainName,
                                            port,
                                            dnsIp,
                                            port
                                    ),
                                    !ip.equals(dnsIp)
                            )
                                    + String.format("Актуальное IP сервера `%s:%d`\n", ip, port)
                    ).queue();
                } else {
                    event.getHook().editOriginal("Неудалось получить IP").queue();
                }
                if (!dnsIp.equals(ip) && !dnsIp.equals(Utils.NULL_IP)) {
                    DiscordBot.checkIpUpdate();
                }
            }
            case("check-updates") -> {
                event.deferReply().queue();
                event.getHook().editOriginal("⌛ Поиск обновлений...\n⬛ Загрузка обновления").queue();
                if (!UpdateChecker.checkUpdates()) {
                    event.getHook().editOriginal("✅ Обновления не найдены\n⛔ Загрузка отменена").queue();
                    return;
                }

                BigDecimal fileSize = new BigDecimal(UpdateChecker.lastVersionSize / 1048576);
                String substring = String.format(
                        "✅ Обновление найдено `%s -> %s (%sMB)`\n",
                        UpdateChecker.thisVersion,
                        UpdateChecker.lastVersion,
                        fileSize.setScale(2, BigDecimal.ROUND_HALF_UP)
                );

                event.getHook().editOriginal(substring + "⌛ Загрузка обновления...").queue();
                boolean status = Utils.downloadFile("./plugins/OpenDiscordAuth.jar", UpdateChecker.lastVersionDownloadURL);
                String downloadStatusString = status ? "✅ Обновление загружено\nℹ Перезапустите сервер для применения обновлений" : "⛔ Не удалось загрузить обновление";

                event.getHook().editOriginal(substring + downloadStatusString).queue();
            }
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Bukkit.getLogger().info(DiscordBot.bot.getSelfUser().getAsTag());
        DiscordBot.updateOnlineStatus();
        DiscordBot.updateApplicationCommands();
        DiscordBot.checkIpUpdate();
        TextChannel channel = (TextChannel) DiscordBot.bot.getGuildChannelById(Config.discordChatIdForTranslation);

        Webhook w = null;

        String subname = String.format(
            "%s.%s/chatTranslator",
            Config.domainProviderServerDomainSubName,
            Config.domainProviderDomainName
        );

        for (Webhook webhook: Objects.requireNonNull(channel).retrieveWebhooks().complete()) {
            if (webhook.getName().equals(subname)) {
                w = webhook;
                break;
            }
        }

        if (w == null) {
            w = channel.createWebhook(subname).complete();
        }
        DiscordBot.webhook = new WebhookClient(w);
    }

    public static void updateOnlineStatus() {
        DiscordBot.bot.getPresence().setActivity(
            Activity.playing("Онлайн: " + Bukkit.getServer().getOnlinePlayers().size())
        );
    }

    public static void updateOnlineStatus(Player player, Boolean isLeave) {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

        int size = players.size();

        // В списоке игроков может не быть игрока, с которым пришёл ивент,
        // тогда добавим его к кол-ву, для упрощения расчётов
        if (!players.contains(player)) {
            size++;
        }

        if (isLeave) {
            size--;
        }

        DiscordBot.bot.getPresence().setActivity(
            Activity.playing("Онлайн: " + size)
        );
    }

    public static Member getMember(String id) {
        return DiscordBot.bot.getGuildChannelById(Config.discordChatIdForTranslation).getGuild().getMemberById(id);
    }

    private static void updateApplicationCommands() {
        SlashCommandData rc = Commands.slash("rc", "Run command")
            .addOption(OptionType.STRING, "command", "Minecraft command", true);

        SlashCommandData get_ip = Commands.slash("get-ip", "Возвращает актуальный IP адресс сервера Minecraft");
        SlashCommandData check_updates = Commands.slash("check-updates", "Проверяет наличие обновлений плагина");

        DiscordBot.bot.updateCommands()
            .addCommands(rc)
            .addCommands(get_ip)
            .addCommands(check_updates)
            .queue();
    }

    private static String checkIpUpdate() {
        String ip = Utils.getGlobalIp();
        String dnsIp = ElasticwebAPI.getDnsIp();

        if (dnsIp.equals(Utils.NULL_IP)) return null;

        if (DiscordBot.serverIp == null) {
            DiscordBot.serverIp = dnsIp;
        }

        if (!ip.equals(DiscordBot.serverIp)) {
            Bukkit.getLogger().info(
                String.format(
                    "IP inconsistent detected (%s != %s). Syncing...", ip, DiscordBot.serverIp
                )
            );
            boolean status = ElasticwebAPI.updateDnsIp(ip);
            long unixTime = System.currentTimeMillis() / 1000L;
            String replay = String.format("Запрос на смену DNS отправлен <t:%d:R>", unixTime);
            if (!status) {
                replay = "Запрос на смену DNS не удался";
                Bukkit.getLogger().info("IP inconsistent synchronization failed");
            } else {
                Bukkit.getLogger().info("IP inconsistent synchronization complete");
            }

            TextChannel channel = (TextChannel) DiscordBot.bot.getGuildChannelById(Config.discordChatIdForTranslation);
            int port = Bukkit.getServer().getPort();
            channel.sendMessage(
                String.format("IP сервера сменилось!\n`%s:%s` -> `%s:%s`\n", dnsIp, port, ip, port
                ) + replay
            ).queue();
        }
        DiscordBot.serverIp = ip;
        return DiscordBot.serverIp;
    }
}

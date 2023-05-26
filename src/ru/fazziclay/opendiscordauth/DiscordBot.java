package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;
    public static WebhookClient webhook;
    public static String serverIp;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Utils.debug("[DiscordBot] onMessageReceived(): message="+event.getMessage().getContentRaw()+"");

        String content           = event.getMessage().getContentRaw();
        User author              = event.getMessage().getAuthor();
        MessageChannel channel   = event.getMessage().getChannel();

        if (author.isBot()) {
            return;
        }

        //Проверка на отправку сообщения в канале для ретронсляции; Ретрансляция сообщения из чата Discord в чат Minecraft
        if (channel.getType().isGuild()) {
            if (channel.getId().equals(Config.discordChatIdForTranslation)) {
                Bukkit.broadcastMessage(Utils.getMessageForBroadcast(event));
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
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        if (event.getName().equals("rc")) {
            MyCommandSender sender = new MyCommandSender(event);
            String command = Objects.requireNonNull(event.getOption("command")).getAsString();
            Bukkit.getLogger().info("Command used by " + event.getUser().getAsTag() + ": /" + command);
            if (!Config.opUserIdList.contains(event.getUser().getId())) {
                event.reply(Config.messageCommandMissingPermissions).setEphemeral(true).queue();
                return;
            }
            event.deferReply().queue();

            // Обработка команды и добавление её задачи в планировщик основного потока
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                try {
                    boolean status = Bukkit.dispatchCommand(sender, command);
                    event.getHook().editOriginal(status ? Config.messageCommandSuccess : Config.messageCommandError).queue();
                } catch (CommandException e) {
                    event.getHook().editOriginal(
                        e.getMessage()
                            + "\n" + e.getCause()
                            + "\n" + Arrays.toString(e.getSuppressed())
                            + "\n" + e.getClass()
                            + "\n" + Arrays.toString(e.getStackTrace())
                    ).queue();
                }
            });
        } else if (event.getName().equals("get-ip")) {
            event.deferReply().queue();
            String ip = Utils.getGlobalIp();
            int port = Bukkit.getServer().getPort();

            if (!DiscordBot.serverIp.equals(ip)) DiscordBot.checkIpUpdate();

            if (!ip.equals(Utils.NULL_IP)) {
                Bukkit.getLogger().info(ip);
                event.getHook().editOriginal(
                    Utils.getCrossed(
                        String.format(
                            "URL для подключения: `%s.%s:%d`\nDNS IP сервера `%s:%d`",
                            Config.domainProviderServerDomainSubName,
                            Config.domainProviderDomainName,
                            port,
                            DiscordBot.serverIp,
                            port
                        ),
                        !DiscordBot.serverIp.equals(ip)
                    )
                    + String.format("Актуальное IP сервера `%s:%d`\n", ip, port)
                ).queue();
            } else {
                event.getHook().editOriginal("Неудалось получить IP").queue();
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

        for (Webhook webhook: Objects.requireNonNull(channel).retrieveWebhooks().complete()) {
            if (webhook.getName().equals("minecraftTranslator")) {
                w = webhook;
            }
        }

        if (w == null) {
            w = channel.createWebhook("minecraftTranslator").complete();
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
        CommandData rc = new CommandData("rc", "Run command")
            .addOption(OptionType.STRING, "command", "Minecraft command", true);

        CommandData get_ip = new CommandData("get-ip", "Возвращает актуальный IP адресс сервера Minecraft");

        DiscordBot.bot.updateCommands()
            .addCommands(rc)
            .addCommands(get_ip)
            .queue();
    }

    private static String checkIpUpdate() {
        String ip = Utils.getGlobalIp();
        if (DiscordBot.serverIp.isEmpty()) {
            DiscordBot.serverIp = ElasticwebAPI.getDnsIp();
        }

        if (!DiscordBot.serverIp.equals(ip)) {
            Boolean status = ElasticwebAPI.updateDnsIp(ip);
            String replay = "Запрос на смену DNS отправлен";
            if (!status) replay = "Запрос на смену DNS не удался";

            TextChannel channel = (TextChannel) DiscordBot.bot.getGuildChannelById(Config.discordChatIdForTranslation);
            int port = Bukkit.getServer().getPort();
            channel.sendMessage(
                String.format("<@%s> IP сервера сменилось на `%s:%s`\n", Config.opUserIdList.get(0), ip, port) + replay
            ).queue();
        }
        DiscordBot.serverIp = ip;
        return DiscordBot.serverIp;
    }
}

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Utils.debug("[DiscordBot] onMessageReceived(): message="+event.getMessage().getContentRaw()+"");

        String content           = event.getMessage().getContentRaw();
        User author              = event.getMessage().getAuthor();
        MessageChannel channel   = event.getMessage().getChannel();

        if (channel.getType().isGuild() || author.getId().equals(bot.getSelfUser().getId())) {
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
        MyCommandSender sender = new MyCommandSender(event);
        String command = Objects.requireNonNull(event.getOption("command")).getAsString();
        Bukkit.getLogger().info("Command used by " + event.getUser().getAsTag() + ": /" + command);
        if (event.getName().equals("rc")) {
            if(!event.getUser().getId().equals("256114365894230018")) {
                event.reply("У вас недостаточно прав").queue();
            }
            event.deferReply().queue();
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                try {
                    boolean status = Bukkit.dispatchCommand(sender, command);
                    if(status) {
                        event.getHook().editOriginal("Команда исполнена").queue();
                    } else {
                        event.getHook().editOriginal("Ошибка").queue();
                    };
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
        }
    }
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        DiscordBot.bot.getPresence().setActivity(
                Activity.playing("Онлайн: " + Bukkit.getServer().getOnlinePlayers().size())
        );
    }

}

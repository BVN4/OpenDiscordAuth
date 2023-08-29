package ru.fazziclay.opendiscordauth.runcommand;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import ru.fazziclay.opendiscordauth.Config;
import ru.fazziclay.opendiscordauth.Main;
import ru.fazziclay.opendiscordauth.discordbot.Controller;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class RunCommandController extends Controller {

    protected BukkitScheduler scheduler;

    public RunCommandController() {
        super();

        this.scheduler = Bukkit.getScheduler();
    }

    @Override
    public void eventHandle(@NotNull SlashCommandInteractionEvent event) {
        RunCommandSender sender = new RunCommandSender();
        String command = Objects.requireNonNull(event.getOption("command")).getAsString();
        User user = event.getUser();

        logger.info("Command used by " + user.getAsTag() + ": /" + command);
        if (!Config.opUserIdList.contains(user.getId())) {
            event.reply(Config.messageCommandMissingPermissions)
                .setEphemeral(true)
                .queue();
            return;
        }

        event.deferReply().queue();
        logger.info("Debug");

        // Обработка команды и добавление её задачи в планировщик основного потока
        scheduler.runTask(Main.getPlugin(Main.class), () -> {
            try {
                boolean status = Bukkit.dispatchCommand(sender, command);

                event.getHook()
                    .editOriginal(status ? Config.messageCommandSuccess : Config.messageCommandError)
                    .queue();

                sender.setCallback(() -> {
                    String msg = formatMessage(sender.getMessage());

                    FileUpload file = FileUpload.fromData(
                        new ByteArrayInputStream(msg.getBytes()),
                        command + ".ansi"
                    );

                    event.getHook()
                        .editOriginalAttachments(file)
                        .queue();
                });

                sender.runCallback();
            } catch (CommandException e) {
                event.getHook()
                    .editOriginal(Config.messageCommandError + "```" + e + "```")
                    .queue();
            }
        });
    }

    protected String formatMessage(String msg) {
        return msg
            .replace("§0", "\u001B[0;30m")
            .replace("§1", "\u001B[0;34m")
            .replace("§2", "\u001B[0;32m")
            .replace("§3", "\u001B[0;36m")
            .replace("§4", "\u001B[0;31m")
            .replace("§5", "\u001B[0;35m")
            .replace("§6", "\u001B[0;33m")
            .replace("§7", "\u001B[0;37m")
            .replace("§8", "\u001B[0;90m")
            .replace("§9", "\u001B[0;94m")
            .replace("§a", "\u001B[0;92m")
            .replace("§b", "\u001B[0;96m")
            .replace("§c", "\u001B[0;91m")
            .replace("§d", "\u001B[0;95m")
            .replace("§e", "\u001B[0;93m")
            .replace("§f", "\u001B[0;97m");
    }
}

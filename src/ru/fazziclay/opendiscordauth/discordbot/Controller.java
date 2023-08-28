package ru.fazziclay.opendiscordauth.discordbot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public abstract class Controller {

    protected Logger logger;

    public Controller() {
        this.logger = Bukkit.getLogger();
    }

    protected abstract void eventHandle(@NotNull SlashCommandInteractionEvent event);

}

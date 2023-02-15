package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class MyCommandSender implements RemoteConsoleCommandSender {

    SlashCommandEvent event;

    Boolean isOriginalEdited = false;

    public MyCommandSender(SlashCommandEvent event){
        this.event = event;
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin arg0) {
        return new PermissionAttachment(arg0, this);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return new PermissionAttachment(arg0, this);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return new PermissionAttachment(arg0, this);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return new PermissionAttachment(arg0, this);
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean hasPermission(String arg0) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        return true;
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return true;
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public void removeAttachment(PermissionAttachment arg0) {
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean arg0) {
    }

    @Override
    public @NotNull String getName() {
        return "CustomSender";
    }

    @Override
    public @NotNull Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.event.getHook().retrieveOriginal().complete().reply(this.formatMessage(message)).queue();
    }

    @Override
    public void sendMessage(String[] messages) {
        String callback = "";
        for (String message : messages) {
            callback += ("\n" + message);
        }
        this.sendMessage(callback);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {

    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {

    }

    @Override
    public @NotNull Spigot spigot() {
        return null;
    }

    private String formatMessage(String msg) {
        return "```ansi\n" + msg
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
            .replace("§f", "\u001B[0;97m")
            + "```";
    }
}

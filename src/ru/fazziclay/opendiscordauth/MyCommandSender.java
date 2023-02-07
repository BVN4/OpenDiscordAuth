package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class MyCommandSender implements CommandSender {

    SlashCommandEvent event;

    public  MyCommandSender(SlashCommandEvent event){
        this.event = event;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return null;
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
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
    public String getName() {
        return "CustomSender";
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public void sendMessage(String message) {
        this.event.getHook().editOriginal(message).queue();
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {

    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {

    }

    @Override
    public Spigot spigot() {
        return null;
    }
}

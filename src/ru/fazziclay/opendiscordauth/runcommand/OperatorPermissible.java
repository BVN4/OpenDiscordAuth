package ru.fazziclay.opendiscordauth.runcommand;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public interface OperatorPermissible extends Permissible {

    @Override
    default boolean isPermissionSet(@NotNull String s) {
        return true;
    }

    @Override
    default boolean isPermissionSet(@NotNull Permission permission) {
        return true;
    }

    @Override
    default boolean hasPermission(@NotNull String s) {
        return true;
    }

    @Override
    default boolean hasPermission(@NotNull Permission permission) {
        return true;
    }

    @Override
    default @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    default @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    default @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    default @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    default void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
    }

    @Override
    default void recalculatePermissions() {
    }

    @Override
    default @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<>(256);
    }

    @Override
    default boolean isOp() {
        return true;
    }

    @Override
    default void setOp(boolean b) {
    }

}

package ru.fazziclay.opendiscordauth.runcommand;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class ConsoleCommandSenderBase implements ConsoleCommandSender {

    protected final ConsoleCommandSender consoleSender;

    public ConsoleCommandSenderBase() {
        consoleSender = Bukkit.getConsoleSender();
    }

    @Override
    public @NotNull Server getServer() {
        return consoleSender.getServer();
    }

    @Override
    public abstract @NotNull String getName();

    @Override
    public @NotNull Spigot spigot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Component name() {
        return Component.text(getName());
    }

    @Override
    public boolean isConversing() {
        return consoleSender.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
        consoleSender.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return consoleSender.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        consoleSender.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        consoleSender.abandonConversation(conversation, details);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        consoleSender.sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        consoleSender.sendMessage(messages);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        consoleSender.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        consoleSender.sendMessage(uuid, strings);
    }

    @Override
    public void sendRawMessage(@Nullable UUID uuid, @NotNull String s) {
        consoleSender.sendRawMessage(uuid, s);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        consoleSender.sendRawMessage(message);
    }

}

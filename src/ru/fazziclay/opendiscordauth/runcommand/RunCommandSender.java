package ru.fazziclay.opendiscordauth.runcommand;

import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunCommandSender extends ConsoleCommandSenderBase implements ConsoleCommandSender, OperatorPermissible {

    protected @Nullable Runnable callback;

    protected final StringBuilder msg = new StringBuilder();

    public String getMessage() {
        return msg.toString();
    }

    public void setCallback(@NotNull Runnable callback) {
        this.callback = callback;
    }

    public void runCallback() {
        if (callback != null) {
            callback.run();
        }
    }

    protected void append(@NotNull String message) {
        msg.append(message).append('\n');
    }

    @Override
    public @NotNull String getName() {
        return "RunCommandSender";
    }

    @Override
    public void sendMessage(@NotNull String message) {
        append(message);
        runCallback();
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        for (String message : messages) {
            append(message);
        }
        runCallback();
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        append(message);
        runCallback();
    }
}

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.Timer;



public class TempAccount {
    MessageChannel messageChannel;
    User           user;
    Timer          timer;

    public TempAccount(MessageChannel messageChannel, User user, Timer timer) {
        this.messageChannel = messageChannel;
        this.user = user;
        this.timer = timer;
    }
}

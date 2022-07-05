package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
}

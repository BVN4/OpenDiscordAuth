package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Webhook;
import org.bukkit.Bukkit;
import org.json.JSONObject;
import ru.fazziclay.opendiscordauth.discordbot.DiscordBot;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;
import java.lang.InterruptedException;

public class WebhookClient {

    private Webhook webhook = null;
    public WebhookClient(Webhook webhook) {
        this.webhook = webhook;
    }

    public Webhook getWebhook() {
        return webhook;
    }

    public void sendMessage(String message, String name, String avatar_url) throws InterruptedException {
        String body = this.getRequestBody(message, name, avatar_url);
        HttpClient httpclient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://discord.com/api/v9/webhooks/" + webhook.getId() + "/" + webhook.getToken()))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .header("content-type", "application/json")
            .header("Authorization", Config.discordBotToken)
            .build();
        HttpResponse<String> response = null;
        try {
            response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            Bukkit.getLogger().info(e.getMessage());
        }
    }

    public void sendMessage(String message) throws InterruptedException {
        this.sendMessage(message, DiscordBot.bot.getSelfUser().getEffectiveName(), DiscordBot.bot.getSelfUser().getAvatarUrl());
    }

    private String getRequestBody(String message, String name, String avatar_url) {
        JSONObject payload = new JSONObject();

        payload.put("content", message);
        if (!name.equals("")) payload.put("username", name);
        if (!avatar_url.equals("")) payload.put("avatar_url", avatar_url);

        return payload.toString();
    }
}

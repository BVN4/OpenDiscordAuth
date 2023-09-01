package ru.fazziclay.opendiscordauth.getip;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.fazziclay.opendiscordauth.Config;
import ru.fazziclay.opendiscordauth.ElasticwebAPI;
import ru.fazziclay.opendiscordauth.Utils;
import ru.fazziclay.opendiscordauth.discordbot.Controller;
import ru.fazziclay.opendiscordauth.discordbot.DiscordBot;

public class GetIpController extends Controller {

    public GetIpController() {
        super();
    }

    @Override
    public void eventHandle(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String ip = Utils.getGlobalIp();
        String dnsIp = ElasticwebAPI.getDnsIp();
        int port = Bukkit.getServer().getPort();
        if (!ip.equals(Utils.NULL_IP)) {
            Bukkit.getLogger().info(ip);
            event.getHook().editOriginal(
                    Utils.getCrossed(
                            String.format(
                                    "URL для подключения: `%s.%s:%d`\nDNS IP сервера `%s:%d`\n",
                                    Config.domainProviderServerDomainSubName,
                                    Config.domainProviderDomainName,
                                    port,
                                    dnsIp,
                                    port
                            ),
                            !ip.equals(dnsIp)
                    )
                            + String.format("Актуальное IP сервера `%s:%d`\n", ip, port)
            ).queue();
        } else {
            event.getHook().editOriginal("Неудалось получить IP").queue();
        }
        if (!dnsIp.equals(ip) && !dnsIp.equals(Utils.NULL_IP)) {
            DiscordBot.checkIpUpdate();
        }
    }
}

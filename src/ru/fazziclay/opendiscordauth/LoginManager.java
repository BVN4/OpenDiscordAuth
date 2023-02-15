package ru.fazziclay.opendiscordauth;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoginManager {
    public static List<String> notAuthorizedPlayers = new ArrayList<>();

    public static void login(String uuid) {
        LoginManager.notAuthorizedPlayers.remove(uuid);

        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        Account account = Account.getByValue(0, player.getName());
        assert account != null;

        account.update();

        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.setDisplayName(ChatColor.COLOR_CHAR + account.guildColor + account.effectiveNick);
        });

        Utils.sendMessage(player, Config.messageSuccessfulAuthorization);
    }

    public static void kickAllNotAuthorizedPlayers() {
        int i = 0;
        while (i < notAuthorizedPlayers.size()) {
            Player player = Bukkit.getPlayer(UUID.fromString(notAuthorizedPlayers.get(i)));
            Utils.kickPlayer(player, Config.messagePlayerKickPluginDisabled);
            i++;
        }
    }

    public static boolean isAuthorized(String uuid) {
        return (!notAuthorizedPlayers.contains(uuid));
    }

    public static void giveCode(String uuid, String nickname, Player player) {
        Utils.debug("[LoginManager] giveCode(...)");

        String codeContent = TempCode.create(uuid, nickname);
        if (codeContent == null) {
            Utils.kickPlayer(player, Config.messageNotFreeTempCodeError);
            return;
        }

        // -------------
        String b = Config.messageGiveTempCode.replace("$code", codeContent).replace("&", "§");
        TextComponent text = new TextComponent(b);

        if (Config.giveTempCodeEventsIsHoverEvent) {
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Config.giveTempCodeEventsHoverText.replace("&", "§").replace("$code", codeContent)).create()));
        }

        if (Config.giveTempCodeEventsIsClickEvent) {
            if (Config.giveTempCodeEventsClickMode == 0) {
                text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, codeContent));

            } else if (Config.giveTempCodeEventsClickMode == 1) {
                text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, codeContent));
            }
        }

        player.spigot().sendMessage(text);
    }
}

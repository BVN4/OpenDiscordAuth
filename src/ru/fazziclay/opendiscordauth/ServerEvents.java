package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;

public class ServerEvents implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Utils.debug("[ServerEvents] onLogin(-)");

        String uuid = event.getPlayer().getUniqueId().toString();

        if (!LoginManager.notAuthorizedPlayers.contains(uuid)) {
            LoginManager.notAuthorizedPlayers.add(uuid);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Utils.debug("[ServerEvents] onPlayerJoin(-)");

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String nickname = player.getName();
        String ip = Utils.getPlayerIp(player);

        if (Session.isActive(nickname, ip)) {
            LoginManager.login(uuid);

            Session session = Session.getByValue(0, nickname);
            assert session != null;
            session.delete();

            return;
        }

        if (Account.getByValue(0, nickname) == null) {
            Utils.sendMessage(player, Config.messageRegistrationInstructions);

        } else {
            Utils.sendMessage(player, Config.messageLoginInstructions);
        }

        player.setGameMode(GameMode.SPECTATOR);
        LoginManager.giveCode(uuid, nickname, player);
        DiscordBot.updateOnlineStatus();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Utils.debug("[ServerEvents] onPlayerQuit(-)");

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String nickname = player.getName();
        String ip = Utils.getPlayerIp(player);

        TempCode tempCode = TempCode.getByValue(2, nickname);
        if (tempCode != null) tempCode.delete(); // Если для этого игрока есть сгенерированный код то удалить его.

        Account account = Account.getByValue(0, nickname);
        if (account != null && account.temp) account.delete(); // Удалить аккаунт если он временный

        if (LoginManager.isAuthorized(uuid)) {
            Session.update(nickname, ip);
        }
        LoginManager.notAuthorizedPlayers.remove(uuid);
        DiscordBot.updateOnlineStatus();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Utils.debug("[ServerEvents] onPlayerChat(...)");

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String nickname = player.getName();
        String content = event.getMessage();

        Utils.debug("[ServerEvents] onPlayerChat(...): nickname="+nickname+"; content="+content+"");

        if (!LoginManager.isAuthorized(uuid)) {
            Utils.debug("[ServerEvents] onPlayerChat(...): not authorized!");

            Account account = Account.getByValue(0, nickname);
            if (account != null && account.temp) {
                Utils.debug("[ServerEvents] onPlayerChat(...): Detected temp account");

                if (content.equals(Config.commandConfirm)) {
                    account.makePermanent();
                    LoginManager.login(uuid);

                } else if (content.equals(Config.commandCancel)) {
                    account.delete();
                    LoginManager.giveCode(uuid, nickname, player);
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) { // Отмена движения игрока
        String uuid = event.getPlayer().getUniqueId().toString();

        if (!LoginManager.isAuthorized(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            boolean isAuthorized = LoginManager.isAuthorized(player.getUniqueId().toString());
            if (!isAuthorized) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            boolean isAuthorized = LoginManager.isAuthorized(event.getEntity().getUniqueId().toString());
            if (!isAuthorized) event.setCancelled(true);
        }
    }
}

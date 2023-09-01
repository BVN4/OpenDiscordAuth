package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Utils {

    public static String GLOBAL_IP_API_URL = "https://api.ipify.org";
    public static String NULL_IP = "0.0.0.0";

    public static String getPlayerIp(Player player) {
        return Objects.requireNonNull(player.getAddress()).getHostName();
    }

    public static Player getPlayerByUUID(String uuid) {
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }


    public static void debug(String message) {
        if (Config.isDebugEnable) {
            Utils.print("§e§l[DEBUG]§r§e " + message);
        }
    }

    public static void print(String message) {
        Bukkit.getLogger().info(message);
    }

    public static int getRandom(int minimum, int maximum) { // Получение случайного числа в диапозоне
        Utils.debug("[Utils] getRandom(minimum="+minimum+", maximum="+maximum+")");
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static void kickPlayer(Player player, String reason) { // Кик игрока
        Utils.debug("[Utils] kickPlayer(player="+player+", reason="+reason+")");
        if (reason == null) {
            reason = "[OpenDiscordAuth] kicked no reason.§n§b https://github.com/fazziclay/opendiscordauth/";
        }

        if (player == null || !player.isOnline()) {
            return;
        }

        String finalReason = reason;
        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> player.kickPlayer(finalReason));
    }

    public static void sendMessage(Player player, String message) {
        Utils.debug("[Utils] sendMessage(player="+player+", message="+message+")");
        if (player == null || message == null || message.equals("-1")) {
            return;
        }

        player.sendMessage(message.replace("&", "§"));
    }

    public static void sendMessage(MessageChannel channel, String message) { // Отправка сообщения в Discord
        Utils.debug("[Utils] sendMessage(channel="+channel+", message="+message+")");
        if (channel == null || message == null || message.equals("-1")) {
            Utils.debug("[Utils] sendMessage(channel="+channel+", message="+message+") stopped.");
            return;
        }

        String[] replacements = {"&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0", "&a", "&e", "&d", "&f", "&r", "&l", "&k", "&c", "&b", "&n", "&m"};

        int i = 0;
        while (i < replacements.length) {
            message = message.replace(replacements[i], "");
            i++;
        }

        channel.sendMessage(message).queue();
    }


    // File Utils
    public static void createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            createDirIfNotExists(dirPath);
            File folder = new File(dirPath);
            folder.mkdirs();
        }

        File file = new File(path);

        try {
            if (!file.exists())
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readFile(String path) {
        Utils.createNewFile(path);

        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(path);

            char[] buff = new char[1024];
            int length;

            while ((length = fileReader.read(buff)) > 0) {
                stringBuilder.append(new String(buff, 0, length));
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }

    public static void writeFile(String path, String content) {
        Utils.createNewFile(path);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(path, false);
            fileWriter.write(content);
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String truncate(String str, int length) {
        if (str.length() <= length) {
            return str;
        } else {
            return str.substring(0, length-1) + "…";
        }
    }

    public static String truncate(String str, int length, String postfix) {
        int lengthTarget = length - postfix.length();
        if (lengthTarget < 0) {
            throw new IllegalArgumentException("Postfix length cannot exceed the maximum length");
        }
        return Utils.truncate(str, lengthTarget) + postfix;
    }

    public static String getMemberHexColor(Member member) {
        Color color = Objects.requireNonNull(member).getColor();
        if (color == null) {
            color = Color.WHITE;
        }

        return "#"+Integer.toHexString(color.getRGB()).substring(2);
    }

    public static String getMessageForBroadcast(MessageReceivedEvent event, String prefix) {
        boolean hasFiles = event.getMessage().getAttachments().size() == 0;
        String filesMessage = (!hasFiles ? " <file>" : "");
        String message = event.getMessage().getContentDisplay();

        String output = Config.globalMessageFormat
            .replace("&", "§")
            .replace("%prefix", prefix)
            .replace("%color", Utils.getMemberHexColor(event.getMember()))
            .replace("%displayname", event.getMember().getEffectiveName())
            .replace("%message", message);

        return Utils.truncate(output, 256, filesMessage);
    }

    public static String getGlobalIp() {
        try (
            java.util.Scanner s = new java.util.Scanner(
                new java.net.URL(Utils.GLOBAL_IP_API_URL).openStream(),
                StandardCharsets.UTF_8
            ).useDelimiter("\\A")
        ) {
            return s.next();
        } catch (java.io.IOException e) {
            return Utils.NULL_IP;
        }
    }

    public static String getCrossed(String target, Boolean cross) {
        if (cross) target = "~~" + target + "~~";
        return target;
    }

    public static boolean downloadFile(String path, String url) {
        try {
            java.net.URL website = new java.net.URL(url);
            java.nio.channels.ReadableByteChannel rbc = java.nio.channels.Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(path);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            rbc.close();
            fos.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean downloadFile(String path, String url, pbDrawerFunc progressBarDrawer) {
        try {
            java.net.URL website = new java.net.URL(url);
            java.net.URLConnection conn = website.openConnection();
            long bytesFull = conn.getContentLength();
            java.io.InputStream ins = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(path);

            while (ins.available() > 0) {
                fos.write(ins.read());
                progressBarDrawer.draw(ins.available() - bytesFull, bytesFull);
            }

            ins.close();
            fos.close();

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.isFile();
    }
}

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

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

	public static String getMemberHexColor(Member member){
		return "#"+Integer.toHexString(
			Objects.requireNonNull(Objects.requireNonNull(member).getColor()).getRGB()
		).substring(2);
	};

	public static boolean isFileExist(String path) {
		File file = new File(path);
		return file.isFile();
	}
}

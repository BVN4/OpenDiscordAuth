package ru.fazziclay.opendiscordauth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.Runtime.Version;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    // THIS
    public static Version thisVersion;

    // LAST
    public static Version lastVersion;
    public static String lastVersionDownloadURL = "null";
    public static String lastVersionDownloadPageURL = "null";
    public static boolean isLastVersionRelease = false;
    public static JSONArray allJson;

    public static void loadUpdateChecker(String pluginVersion) {
        Utils.debug("[UpdateChecker] loadUpdateChecker()");

        thisVersion = Version.parse(pluginVersion);

        if (checkUpdates()) {
            Utils.debug("[UpdateChecker] loadUpdateChecker(): update detected!");
            Utils.print("### UPDATE CHECKER ###");
            Utils.print("## OpenDiscordAuth new version!");
            Utils.print("## ");
            Utils.print("## Current: " + thisVersion.toString());
            Utils.print("## Last: " + lastVersion.toString());
            Utils.print("## Download URL: " + lastVersionDownloadPageURL);

            if (Config.enablePluginAutoUpdate && isLastVersionRelease) {
                Utils.print("## Downloading update...");
                boolean state = Utils.downloadFile("./plugins/OpenDiscordAuth.jar", lastVersionDownloadURL);
                Utils.print("## Downloading "
                    + (state ? "complete! Restart server (/restart) to apply changes" : "failed!")
                );
            }

            Utils.print("## ");
        }
    }

    public static boolean checkUpdates() {
        try {
            InputStream inputStream = new URL("https://api.github.com/repos/BVN4/OpenDiscordAuth/releases").openStream();
            Scanner scanner = new Scanner(inputStream);

            allJson = new JSONArray(scanner.nextLine());
            Utils.debug("[UpdateChecker] loadUpdateChecker(): page loaded!");

            JSONObject release = allJson.getJSONObject(0);

            lastVersion = Version.parse(release.getString("name"));
            isLastVersionRelease = !release.getBoolean("prerelease");
            lastVersionDownloadPageURL = release.getString("html_url");
            lastVersionDownloadURL = release.getJSONArray("assets")
                    .getJSONObject(0)
                    .getString("browser_download_url");

            return thisVersion.compareTo(lastVersion) < 0;

        } catch (Exception e) {
            Utils.debug("[UpdateChecker] checkUpdates(): Error. e.toString()=" + e);
            return false;
        }
    }
}

package ru.fazziclay.opendiscordauth;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    // THIS
    public static int thisVersionTag = 16;
    public static String thisVersionName = "0.8.6";
    public static boolean isThisVersionRelease = false;

    // LAST
    public static int lastVersionTag = -1;
    public static String  lastVersionName = "null";
    public static String  lastVersionDownloadURL = "null";
    public static String  lastVersionDownloadPageURL = "null";
    public static boolean isLastVersionRelease = false;
    public static JSONArray allJson;

    public static void loadUpdateChecker() {
        Utils.debug("[UpdateChecker] loadUpdateChecker()");

        try {
            InputStream inputStream = new URL("https://api.github.com/repos/BVN4/OpenDiscordAuth/releases").openStream();
            Scanner scanner = new Scanner(inputStream);

            allJson = new JSONArray(scanner.nextLine());
            Utils.debug("[UpdateChecker] loadUpdateChecker(): page loaded!");


            lastVersionTag = Integer.parseInt(allJson.getJSONObject(0).getString("tag_name"));
            lastVersionName = allJson.getJSONObject(0).getString("name");
            isLastVersionRelease = !allJson.getJSONObject(0).getBoolean("prerelease");
            lastVersionDownloadPageURL = allJson.getJSONObject(0).getString("html_url");
            lastVersionDownloadURL = allJson
                    .getJSONObject(0).getJSONArray("assets")
                    .getJSONObject(0).getString("browser_download_url");

            Utils.debug("[UpdateChecker] loadUpdateChecker(): last version: lastVersionTag="+lastVersionTag+"; lastVersionName"+lastVersionName+"; isLastVersionRelease"+isLastVersionRelease
                +"; lastVersionDownloadURL="+lastVersionDownloadPageURL);

            if (lastVersionTag > thisVersionTag) {
                Utils.debug("[UpdateChecker] loadUpdateChecker(): update detected!");
                Utils.print("### UPDATE CHECKER ###");
                Utils.print("## OpenDiscordAuth new version!");
                Utils.print("## ");
                Utils.print("## Current: (" + thisVersionName + ") (#" + thisVersionTag + ")");
                Utils.print("## Last: (" + lastVersionName + ") (#" + lastVersionTag + ")");
                Utils.print("## Download URL: " + lastVersionDownloadPageURL);

                if (Config.enablePluginAutoUpdate && isLastVersionRelease) {
                    Utils.print("## Downloading update...");
                    boolean state = Utils.downloadFile("./plugins/OpenDiscordAuth.jar", lastVersionDownloadURL);
                    Utils.print("## Downloading "
                            + (state ? "complete! Reload plugins (/reload) to apply changes" : "failed!")
                    );
                }

                Utils.print("## ");
            }

        } catch (Exception e) {
            Utils.debug("[UpdateChecker] loadUpdateChecker(): Error. e.toString()="+ e);
        }
    }
}

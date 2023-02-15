package ru.fazziclay.opendiscordauth;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;


public class Account {

    // Static
    public static List<Account> accounts = new ArrayList<>();
    public static JSONArray     accountsJson = new JSONArray();


    public static void makePermanent(Account account) {
        Utils.debug("[Account] makePermanent()");

        if (account.temp) {
            account.temp = false;

            // add
            Account.accountsJson.put(new JSONObject(
                "{'discord': '"+account.ownerDiscord+"',"
                + " 'nickname': '"+account.ownerNickname+"',"
                + " 'effectiveNick': '"+account.effectiveNick+"',"
                + " 'effectiveAvatarUrl': '"+account.effectiveAvatarUrl+"',"
                + " 'guildColor': '"+account.guildColor+"'"
                + "}"
            ));
            Utils.writeFile(Config.accountsFilePath, accountsJson.toString(4));
            // add
        }
    }

    public static void delete(Account account) {
        Utils.debug("[Account] delete()");

        // remove
        Account.accounts.remove(account);
        int i=0;
        while (i < accountsJson.length()) {
            JSONObject a = Account.accountsJson.getJSONObject(i);
            if ( (a.getString("discord").equals(account.ownerDiscord)) && (a.getString("nickname").equals(account.ownerNickname)) ) {
                Account.accountsJson.remove(i);
                break;
            }

            i++;
        }
        // remove
    }

    public static void create(Member ownerDiscord, String ownerNickname) {
        Utils.debug("[Account] create()");

        String discordNickname = ownerDiscord.getUser().getAsTag();
        Utils.sendMessage(Bukkit.getPlayer(ownerNickname), Config.messageAccountCreatingConfirming.replace("$discord", discordNickname).replace("$nickname", ownerNickname));

        Account account = new Account(
                ownerDiscord.getId(),
                ownerNickname,
                true,
                ownerDiscord.getEffectiveName(),
                ownerDiscord.getEffectiveAvatarUrl(),
                Utils.getMemberHexColor(ownerDiscord));

        Account.accounts.add(account);
    }

    public static Account getByValue(int type, Object value) {
        Utils.debug("[Account] getByValue("+type+", "+value+")");

        int i = 0;
        while (i < accounts.size()) {
            Account currentAccount = accounts.get(i);

            if (type == 0 && currentAccount.ownerNickname.equals(value)) {
                Utils.debug("[Account] getByValue("+type+", "+value+"): returned '"+currentAccount+"'");
                return currentAccount;
            }

            if (type == 2 && currentAccount.ownerDiscord.equals(value)) {
                Utils.debug("[Account] getByValue("+type+", "+value+"): returned '"+currentAccount+"'");
                return currentAccount;
            }

            if (type == 3 && currentAccount.temp == (boolean) value) {
                Utils.debug("[Account] getByValue("+type+", "+value+"): returned '"+currentAccount+"'");
                return currentAccount;
            }


            i++;
        }

        Utils.debug("[Account] getByValue("+type+", "+value+"): returned 'null'");
        return null;
    }


    //Not-static
    public String ownerDiscord;
    public String ownerNickname;
    public boolean temp;
    public String effectiveNick;
    public String effectiveAvatarUrl;
    public String guildColor;

    public void makePermanent() {
        Utils.debug("[Account] [object] makePermanent()");
        Account.makePermanent(this);
    }

    public void delete() {
        Utils.debug("[Account] [object] delete()");
        Account.delete(this);
    }


    // Constructor
    public Account(String ownerDiscord, String ownerNickname, boolean temp, String guildNick, String effectiveAvatarUrl, String nickColor) {
        Utils.debug("[Account] -> created new object: (ownerDiscord="+ownerDiscord+"; ownerNickname="+ownerNickname+"; temp="+temp+")");

        this.ownerDiscord = ownerDiscord;
        this.ownerNickname = ownerNickname;
        this.temp = temp;
        this.effectiveNick = guildNick;
        this.effectiveAvatarUrl = effectiveAvatarUrl;
        this.guildColor = nickColor;
    }
}

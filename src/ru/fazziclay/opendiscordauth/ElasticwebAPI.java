package ru.fazziclay.opendiscordauth;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Form;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class ElasticwebAPI {

    private static final String API_ROOT = "https://elasticweb.org";
    private static final String DNS_LIST_ENDPOINT = "/api/dns/list/";
    private static final String DNS_ENTRY_ENDPOINT = "/api/dns/entry/";

    public static boolean enabled = true;

    public static boolean updateDnsIp(String ip) {

        if (!enabled) return false;

        Bukkit.getLogger().info("UpdateDnsIp triggered");

        // Получаем список dns записей
        Bukkit.getLogger().info("Getting server dns entry");
        JSONObject entry = ElasticwebAPI.getDnsEntryFromList();
        Bukkit.getLogger().info("Getting server dns entry response: " + entry.toString());

        // Если нашли удаляем устаревшую запись
        if (entry != null) {
            Bukkit.getLogger().info("Deleting server dns entry");
            JSONObject r = ElasticwebAPI.deleteDnsEntry(Config.domainProviderToken, (String) entry.get("id"));
            Bukkit.getLogger().info("Deleting server dns entry response: " + r);
        }

        // Создаём новую запись
        Bukkit.getLogger().info("Posting server dns entry");
        JSONObject resp = ElasticwebAPI.postDnsEntry(
            Config.domainProviderToken,
            Config.domainProviderDomainName,
            Config.domainProviderServerDomainSubName,
            ip
        );
        Bukkit.getLogger().info("Posting server dns entry response: " + resp);

        return resp != null;
    }

    public static String getDnsIp() {

        if (!enabled) return Utils.NULL_IP;

        Bukkit.getLogger().info("GetDnsIp triggered");

        Bukkit.getLogger().info("Getting server dns entry");
        JSONObject entry = ElasticwebAPI.getDnsEntryFromList();

        if (entry == null) {
            return Utils.NULL_IP;
        }

        Bukkit.getLogger().info("Getting server dns entry response: " + entry.toString());

        return (String) entry.get("value");
    }

    public static JSONObject getDnsEntryFromList() {

        if (!enabled) return null;

        JSONArray response = ElasticwebAPI.getDnsList(Config.domainProviderToken, Config.domainProviderDomainName);
        if (response == null) return null;


        JSONObject entry = null;

        for (Object i: response) {

            JSONObject obj = (JSONObject) i;

            if (
                obj.get("name").equals(
                    String.format("%s.%s.", Config.domainProviderServerDomainSubName, Config.domainProviderDomainName)
                )
                    && obj.get("type").equals("A")
            ) {
                entry = obj;
                break;
            }
        }

        return entry;
    }

    public static JSONArray getDnsList(String token, String domainName) {
        if (!enabled) return null;

        try {
            Content resp = Request.get(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_LIST_ENDPOINT + domainName)
                .setHeader("X-API-KEY", token)
                .execute().returnContent();
            return (JSONArray) (new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public static JSONObject getDnsEntry(String token, String dnsId) {
        if (!enabled) return null;

        try {
            Content resp = Request.get(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId)
                .setHeader("X-API-KEY", token)
                .execute().returnContent();

            return (JSONObject) (new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public static JSONObject postDnsEntry(String token, String domainName, String subDomainName, String ip) {
        if (!enabled) return null;

        try {
            Content resp = Request.post(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + domainName)
                .setHeader("X-API-KEY", token)
                .bodyForm(
                    Form.form()
                        .add("type", "A")
                        .add("name", String.format("%s.%s.", domainName, subDomainName))
                        .add("value", ip)
                        .build()
                )
                .execute().returnContent();

            return (JSONObject) (new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            Bukkit.getLogger().info(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + domainName);
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public static JSONObject deleteDnsEntry(String token, String dnsId) {
        if (!enabled) return null;

        try {
            Content resp = Request.delete(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId)
                .setHeader("X-API-KEY", token)
                .execute().returnContent();

            return (JSONObject) (new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            Bukkit.getLogger().info(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId);
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }
}

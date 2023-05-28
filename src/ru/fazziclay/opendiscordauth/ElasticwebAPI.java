package ru.fazziclay.opendiscordauth;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Form;


import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class ElasticwebAPI {

    private static final String API_ROOT = "elasticweb.org";
    private static final String DNS_LIST_ENDPOINT = "/api/dns/list/";
    private static final String DNS_ENTRY_ENDPOINT = "/api/dns/entry/";

    public static boolean updateDnsIp(String ip) {

        if (
            Config.domainProviderToken.startsWith("ENTER") ||
            Config.domainProviderDomainName.startsWith("ENTER") ||
            Config.domainProviderServerDomainSubName.startsWith("ENTER")
        ) {
            return false;
        }

        // Получаем список dns записей
        JSONObject entry = ElasticwebAPI.getDnsEntryFromList();

        // Если нашли удаляем устаревшую запись
        if (entry != null) {
            ElasticwebAPI.deleteDnsEntry(Config.domainProviderToken, entry.getString("id"));
        }

        // Создаём новую запись
        JSONObject resp = ElasticwebAPI.postDnsEntry(
            Config.domainProviderToken,
            Config.domainProviderDomainName,
            Config.domainProviderServerDomainSubName,
            ip
        );

        return resp != null;
    }

    public static String getDnsIp() {
        if (
            Config.domainProviderToken.startsWith("ENTER") ||
            Config.domainProviderDomainName.startsWith("ENTER") ||
            Config.domainProviderServerDomainSubName.startsWith("ENTER")
        ) {
            return Utils.NULL_IP;
        }

        JSONObject entry = ElasticwebAPI.getDnsEntryFromList();

        return entry.getString("value");
    }

    public static JSONObject getDnsEntryFromList() {

        JSONObject response = ElasticwebAPI.getDnsList(Config.domainProviderToken, Config.domainProviderDomainName);
        if (response == null) return null;

        JSONObject entry = null;

        for (Object i: response.getJSONArray("data")) {

            JSONObject obj = (JSONObject) i;

            if (
                obj.getString("name").equals(
                    String.format("%s.%s.", Config.domainProviderServerDomainSubName, Config.domainProviderDomainName)
                )
                    && obj.getString("type").equals("A")
            ) {
                entry = obj;
                break;
            }
        }

        return entry;
    }

    public static JSONObject getDnsList(String token, String domainName) {
        try {
            Content resp = Request.get(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_LIST_ENDPOINT + domainName)
                .setHeader("X-API-KEY", token)
                .execute().returnContent();

            return new JSONObject(new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static JSONObject getDnsEntry(String token, String dnsId) {
        try {
            Content resp = Request.get(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId)
                .setHeader("X-API-KEY", token)
                .execute().returnContent();

            return new JSONObject(new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static JSONObject postDnsEntry(String token, String domainName, String subDomainName, String ip) {
        try {
            Content resp = Request.post(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + domainName)
                .setHeader("X-API-KEY", token)
                .bodyForm(Form.form()
                    .add("type", "A")
                    .add("name", String.format("%s.%s.", domainName, subDomainName))
                    .add("value", ip)
                    .build()
                )
                .execute().returnContent();

            return new JSONObject(new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static JSONObject deleteDnsEntry(String token, String dnsId) {
        try {
            Content resp = Request.delete(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId)
                .setHeader("X-API-KEY", token)
                .execute().returnContent();

            return new JSONObject(new JSONParser().parse(resp.asString()));

        } catch (IOException | ParseException e) {
            return null;
        }
    }
}

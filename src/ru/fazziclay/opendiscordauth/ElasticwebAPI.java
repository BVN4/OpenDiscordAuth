package ru.fazziclay.opendiscordauth;

import org.bukkit.craftbukkit.libs.org.apache.http.HttpEntity;
import org.bukkit.craftbukkit.libs.org.apache.http.HttpResponse;
import org.bukkit.craftbukkit.libs.org.apache.http.NameValuePair;
import org.bukkit.craftbukkit.libs.org.apache.http.client.HttpClient;
import org.bukkit.craftbukkit.libs.org.apache.http.client.entity.UrlEncodedFormEntity;
import org.bukkit.craftbukkit.libs.org.apache.http.client.methods.HttpDelete;
import org.bukkit.craftbukkit.libs.org.apache.http.client.methods.HttpGet;
import org.bukkit.craftbukkit.libs.org.apache.http.client.methods.HttpPost;
import org.bukkit.craftbukkit.libs.org.apache.http.impl.client.HttpClients;
import org.bukkit.craftbukkit.libs.org.apache.http.message.BasicNameValuePair;
import org.bukkit.craftbukkit.libs.org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ElasticwebAPI {

    private static final String API_ROOT = "elasticweb.org";
    private static final String DNS_LIST_ENDPOINT = "/api/dns/list/";
    private static final String DNS_ENTRY_ENDPOINT = "/api/dns/entry/";

    public static boolean updateDnsIp(String ip) {

        // Получаем список dns записей
        JSONObject entry = ElasticwebAPI.getDnsEntryFromList();

        // Если нашли удаляем устаревшую запись
        if (entry != null) {
            ElasticwebAPI.deleteDnsEntry(Config.domainProviderToken, entry.getString("id"));
        }

        // Создаём новую запись
        ElasticwebAPI.postDnsEntry(
            Config.domainProviderToken,
            Config.domainProviderDomainName,
            Config.domainProviderServerDomainSubName,
            ip
        );

        return true;
    }

    public static String getDnsIp() {

        JSONObject entry = ElasticwebAPI.getDnsEntryFromList();

        return entry.getString("value");
    }

    public static JSONObject getDnsEntryFromList() {

        JSONObject response = ElasticwebAPI.getDnsList(Config.domainProviderToken, Config.domainProviderDomainName);

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
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_LIST_ENDPOINT + domainName);
            httpget.setHeader("X-API-KEY", token);

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            return new JSONObject(new JSONParser().parse(json));

        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static JSONObject getDnsEntry(String token, String dnsId) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId);
            httpget.setHeader("X-API-KEY", token);

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            return new JSONObject(new JSONParser().parse(json));

        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static JSONObject postDnsEntry(String token, String domainName, String subDomainName, String ip) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + domainName);

            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("type", "A"));
            params.add(new BasicNameValuePair("name", String.format("%s.%s.", domainName, subDomainName)));
            params.add(new BasicNameValuePair("value", ip));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httppost.setHeader("X-API-KEY", token);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            return new JSONObject(new JSONParser().parse(json));

        } catch (IOException | ParseException e) {
            return null;
        }
    }

    public static JSONObject deleteDnsEntry(String token, String dnsId) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpDelete httpdelete = new HttpDelete(ElasticwebAPI.API_ROOT + ElasticwebAPI.DNS_ENTRY_ENDPOINT + dnsId);
            httpdelete.setHeader("X-API-KEY", token);

            HttpResponse response = httpclient.execute(httpdelete);
            HttpEntity entity = response.getEntity();

            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            return new JSONObject(new JSONParser().parse(json));

        } catch (IOException | ParseException e) {
            return null;
        }
    }
}

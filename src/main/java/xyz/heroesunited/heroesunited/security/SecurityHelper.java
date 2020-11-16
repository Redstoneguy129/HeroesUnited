package xyz.heroesunited.heroesunited.security;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecurityHelper {

    private JsonParser parser = new JsonParser();

    private JsonObject getPlayableJson() {
        try {
            return (JsonObject) parser.parse(startUrl("https://beycraft.ga/heroes_united/patreons.json"));
        } catch (Exception e) {
            LogManager.getLogger().error("No Internet Connection!");
            Minecraft.getInstance().shutdown();
            return null;
        }
    }

    public boolean patronSecurity(String UUID) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        for (Map.Entry<String, JsonElement> discordID : Objects.requireNonNull(getPlayableJson()).entrySet()) {
            String uuid = discordID.getValue().getAsJsonObject().get("uuid").getAsString();
            if (uuid.equals(UUID)) {
                atomicBoolean.set(discordID.getValue().getAsJsonObject().get("role").getAsString().equals("supporter"));
            }
        }
        return atomicBoolean.get();
    }

    public boolean boosterSecurity(String UUID) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        for (Map.Entry<String, JsonElement> discordID : Objects.requireNonNull(getPlayableJson()).entrySet()) {
            String uuid = discordID.getValue().getAsJsonObject().get("uuid").getAsString();
            if (uuid.equals(UUID)) {
                atomicBoolean.set(discordID.getValue().getAsJsonObject().get("role").getAsString().equals("booster"));
            }
        }
        return atomicBoolean.get();
    }

    public boolean bypassSecurity(String UUID) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (Map.Entry<String, JsonElement> discordID : Objects.requireNonNull(getPlayableJson()).entrySet()) {
            String uuid = discordID.getValue().getAsJsonObject().get("uuid").getAsString();
            if (uuid.equals(UUID)) {
                atomicBoolean.set(discordID.getValue().getAsJsonObject().get("role").getAsString().equals("bypass"));
            }
        }
        return atomicBoolean.get();
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JsonObject readJsonFromUrl(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        URL a = new URL(url);
        URLConnection b = a.openConnection();
        if(b instanceof HttpsURLConnection) {
            HttpsURLConnection c = (HttpsURLConnection) a.openConnection();
            c.setHostnameVerifier((s, sslSession) -> true);
        }
        try (InputStream is = a.openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JsonParser parser = new JsonParser();
            return (JsonObject) parser.parse(jsonText);
        }
    }

    public static String startUrl(String url) {
        JsonObject json;
        try {
            json = readJsonFromUrl(url);
            return json.toString();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException ignored) {}
        return "Error";
    }

}

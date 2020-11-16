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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecurityHelper {

    ResultSet users;

    public boolean shouldContinue(String UUID) {
        try {
            String st = "https://raw.githubusercontent.com/Heroes-United/patreonlist/main/users.csv";
            URL stockURL = new URL(st);
            BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
            String s;
            while ((s=in.readLine())!=null) {
                String[] user = s.replace("\"","").split(",");
                if(user[1].equals(UUID)){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.getLogger().error("No Internet Connection!");
            return false;
        }
    }

}

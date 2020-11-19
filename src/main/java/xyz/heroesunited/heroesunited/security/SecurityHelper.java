package xyz.heroesunited.heroesunited.security;

import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class SecurityHelper {

    public boolean shouldContinue(String UUID) {
        try {
            String st = "https://raw.githubusercontent.com/Heroes-United/patreonlist/main/users.csv";
            URL stockURL = new URL(st);
            BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
            String user;
            while ((user=in.readLine())!=null) {
                if(user.replace("\"","").equals(UUID)){
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

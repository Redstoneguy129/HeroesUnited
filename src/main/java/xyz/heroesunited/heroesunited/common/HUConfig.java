package xyz.heroesunited.heroesunited.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class HUConfig {
    public static Client CLIENT;
    public static ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<Client, ForgeConfigSpec> specClientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specClientPair.getRight();
        CLIENT = specClientPair.getLeft();
    }

    public static class Client {

        public final ForgeConfigSpec.BooleanValue renderHead;

        Client(ForgeConfigSpec.Builder builder) {

            builder.comment("Client Settings").push("client");
            renderHead = builder.comment("Changes the patreon-players head").translation("config.heroesunited.renderhead").define("changeHead", true);
            builder.pop();
        }

    }

}
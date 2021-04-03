package xyz.heroesunited.heroesunited.util;

import com.google.common.collect.Lists;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class HURichPresence {

    private static HURichPresence RPC = new HURichPresence("778269026874163230");
    private static boolean hiddenRPC = false;

    public static HURichPresence getPresence() {
        return RPC;
    }

    private final Random random = new Random();
    private List<String> list = Lists.newArrayList();

    public HURichPresence(String clientID) {
        this.list.addAll(getListFromTXT(new ResourceLocation(HeroesUnited.MODID, "splash.txt")));
        this.list.addAll(getListFromTXT(new ResourceLocation("texts/splashes.txt")));
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(user -> HeroesUnited.LOGGER.info(String.format("Logged into Discord as %s!", user.username + "#" + user.discriminator))).build();
        DiscordRPC.discordInitialize(clientID, handlers, true);
        DiscordRPC.discordClearPresence();
    }

    private List<String> getListFromTXT(ResourceLocation resourceLocation) {
        try {
            IResource iresource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            return bufferedreader.lines().map(String::trim).filter((p_215277_0_) -> p_215277_0_.hashCode() != 125780783).collect(Collectors.toList());
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
    }

    public void setDiscordRichPresence(String title, String description, MiniLogos logo, String caption) {
        DiscordRichPresence discordRichPresence = new DiscordRichPresence.Builder(getQuote(description)).setDetails(title).setBigImage("heroes_united", null).setSmallImage(logo.getLogo(), caption).setStartTimestamps(new Timestamp(System.currentTimeMillis()).getTime()).build();
        DiscordRPC.discordUpdatePresence(discordRichPresence);
    }

    private String getQuote(String notNull) {
        if (notNull != null) return notNull;
        return list.get(random.nextInt(list.size()));
    }

    public static void hideDiscordRPC() {
        hiddenRPC = true;
    }

    public static boolean isHiddenRPC() {
        return hiddenRPC;
    }

    public enum MiniLogos {
        NONE,
        BEN10("ben10"),
        GENERATOR_REX("generator_rex"),
        DANNY_PHANTOM("danny_phantom"),
        EXTERNAL("external");

        private final String logo;

        MiniLogos(String logo) {
            this.logo = logo;
        }

        MiniLogos() {
            this.logo = null;
        }

        public String getLogo() {
            return this.logo;
        }
    }
}

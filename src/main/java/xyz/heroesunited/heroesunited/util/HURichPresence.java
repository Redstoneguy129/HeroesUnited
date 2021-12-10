package xyz.heroesunited.heroesunited.util;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.HUConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class HURichPresence {

    private static final HURichPresence RPC = new HURichPresence("778269026874163230");
    private static boolean hiddenRPC = HUConfig.CLIENT.richPresence.get();

    public static HURichPresence getPresence() {
        return RPC;
    }

    private final Random random = new Random();
    private final List<String> list = new ArrayList<>();
    private final IPCClient client;

    public HURichPresence(String clientID) {
        this.list.addAll(getListFromTXT(new ResourceLocation(HeroesUnited.MODID, "splash.txt")));
        this.list.addAll(getListFromTXT(new ResourceLocation("texts/splashes.txt")));
        this.client = new IPCClient(Long.parseLong(clientID));
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                HeroesUnited.LOGGER.info("Logged into Discord");
            }
        });
        try {
            client.connect(DiscordBuild.ANY);
        } catch (Throwable e) {
            HeroesUnited.LOGGER.info("No discord founded.");
        }
    }

    private List<String> getListFromTXT(ResourceLocation resourceLocation) {
        try {
            Resource iresource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            return bufferedreader.lines().map(String::trim).filter((p_215277_0_) -> p_215277_0_.hashCode() != 125780783).collect(Collectors.toList());
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
    }

    public void setDiscordRichPresence(String title, String description, MiniLogos logo, String caption) {
        if (!HURichPresence.isHiddenRPC()) {
            RichPresence.Builder builder = new RichPresence.Builder();
            builder.setState(getQuote(description))
                    .setDetails(title)
                    .setStartTimestamp(OffsetDateTime.now())
                    .setLargeImage("heroes_united", "Heroes United " + SharedConstants.getCurrentVersion().getName());
            if (logo.getLogo() != null) {
                builder.setSmallImage(logo.getLogo(), caption);
            }
            client.sendRichPresence(builder.build());
        }
    }

    public static void close() {
        HURichPresence.getPresence().client.close();
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

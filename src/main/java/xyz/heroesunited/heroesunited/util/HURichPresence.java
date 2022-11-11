package xyz.heroesunited.heroesunited.util;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
    private static final OffsetDateTime startDataTime = OffsetDateTime.now();
    private static boolean hiddenRPC = HUConfig.CLIENT.richPresence.get();

    public static HURichPresence getPresence() {
        return RPC;
    }

    private final Random random = new Random();
    private final List<String> list = new ArrayList<>();
    public final IPCClient client;

    public HURichPresence(String clientID) {
        this.list.addAll(getListFromTXT(new ResourceLocation(HeroesUnited.MODID, "splash.txt")));
        this.list.addAll(getListFromTXT(new ResourceLocation("texts/splashes.txt")));
        this.client = new IPCClient(Long.parseLong(clientID));
        this.client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                HeroesUnited.LOGGER.info("Logged into Discord");
            }
        });
        try {
            this.client.connect(DiscordBuild.ANY);
        } catch (Throwable e) {
            HeroesUnited.LOGGER.info("No discord founded.");
        }
    }

    private List<String> getListFromTXT(ResourceLocation resourceLocation) {
        try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager()
                            .getResource(resourceLocation).getInputStream(), StandardCharsets.UTF_8));
            return bufferedreader.lines().map(String::trim).filter((s) -> s.hashCode() != 125780783).collect(Collectors.toList());
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
    }

    public void setDiscordRichPresence(String title) {
        this.setDiscordRichPresence(title, null, "", null);
    }

    public void setDiscordRichPresence(String title, String description, String logo, String caption) {
        if (!HURichPresence.isHiddenRPC()) {
            try {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setState(getQuote(description))
                        .setDetails(title).setStartTimestamp(startDataTime)
                        .setLargeImage("heroes_united", "Heroes United " + SharedConstants.getCurrentVersion().getName());
                if (!logo.isEmpty()) {
                    builder.setSmallImage(logo, caption);
                }
                client.sendRichPresence(builder.build());
            } catch (Throwable e) {
                HeroesUnited.LOGGER.info("No discord founded.");
            }
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
}

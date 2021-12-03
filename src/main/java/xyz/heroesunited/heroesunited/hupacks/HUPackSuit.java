package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.JsonSuit;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class HUPackSuit extends SimpleJsonResourceReloadListener {

    private static final Map<ResourceLocation, Function<Map.Entry<ResourceLocation, JsonObject>, Suit>> suitTypes = Maps.newHashMap();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public HUPackSuit() {
        super(GSON, "husuits");
        registerSuitType(new ResourceLocation(HeroesUnited.MODID, "default"), JsonSuit::new);
    }

    public static void registerSuitType(ResourceLocation resourceLocation, Function<Map.Entry<ResourceLocation, JsonObject>, Suit> function) {
        suitTypes.put(Objects.requireNonNull(resourceLocation), Objects.requireNonNull(function));
    }

    private static Suit parse(Map.Entry<ResourceLocation, JsonObject> map) {
        Function<Map.Entry<ResourceLocation, JsonObject>, Suit> function = suitTypes.get(new ResourceLocation(GsonHelper.getAsString(map.getValue(), "type")));
        if (function == null) {
            throw new JsonParseException("The type of a suit '" + GsonHelper.getAsString(map.getValue(), "type") + "' doesn't exist!");
        }
        return Objects.requireNonNull(function.apply(map));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> suits, ResourceManager manager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> map : suits.entrySet()) {
            try {
                if (map.getValue() instanceof JsonObject) {
                    Suit suit = parse(new AbstractMap.SimpleEntry<>(map.getKey(), (JsonObject) map.getValue()));
                    if (suit != null) {
                        Suit.registerSuit(suit);
                        HeroesUnited.LOGGER.info("Registered hupack suit {}!", map.getKey());
                    }
                }
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack suit {}", map.getKey(), throwable);
            }
        }
    }
}
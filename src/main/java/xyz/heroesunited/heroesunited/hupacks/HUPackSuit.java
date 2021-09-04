package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.JsonSuit;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class HUPackSuit {

    private static Map<ResourceLocation, Function<Map.Entry<ResourceLocation, JsonObject>, Suit>> suitTypes = Maps.newHashMap();

    public static void init() {
        registerSuitType(new ResourceLocation(HeroesUnited.MODID, "default"), JsonSuit::new);

        ResourceManager resourceManager = HUPacks.getInstance().getResourceManager();
        LinkedHashMap<ResourceLocation, JsonObject> suits = Maps.newLinkedHashMap();

        for (ResourceLocation resourcelocation : resourceManager.listResources("husuits", (name) -> name.endsWith(".json") && !name.startsWith("_"))) {
            String s = resourcelocation.getPath();
            ResourceLocation id = new ResourceLocation(resourcelocation.getNamespace(), s.substring("husuits/".length(), s.length() - ".json".length()));

            try (Resource iresource = resourceManager.getResource(resourcelocation)) {
                suits.put(id, GsonHelper.fromJson(HUPacks.GSON, new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8)), JsonObject.class));
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack suit {} from {}", id, resourcelocation, throwable);
            }
        }

        for (Map.Entry<ResourceLocation, JsonObject> map : suits.entrySet()) {
            try {
                Suit suit = parse(map);
                if (suit != null) {
                    Suit.registerSuit(suit);
                    HeroesUnited.LOGGER.info("Registered hupack suit {}!", map.getKey());
                }
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack suit {}", map.getKey(), throwable);
            }
        }
    }

    public static void registerSuitType(ResourceLocation resourceLocation, Function<Map.Entry<ResourceLocation, JsonObject>, Suit> function) {
        Objects.requireNonNull(resourceLocation);
        Objects.requireNonNull(function);
        suitTypes.put(resourceLocation, function);
    }

    public static Suit parse(Map.Entry<ResourceLocation, JsonObject> map) {
        Function<Map.Entry<ResourceLocation, JsonObject>, Suit> function = suitTypes.get(new ResourceLocation(GsonHelper.getAsString(map.getValue(), "type")));

        if (function == null) {
            throw new JsonParseException("The type of a suit '" + GsonHelper.getAsString(map.getValue(), "type") + "' doesn't exist!");
        }

        Suit suit = function.apply(map);
        return Objects.requireNonNull(suit);
    }
}
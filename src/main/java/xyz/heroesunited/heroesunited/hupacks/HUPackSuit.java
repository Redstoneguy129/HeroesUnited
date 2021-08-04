package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class HUPackSuit {

    private static Map<Identifier, Function<Map.Entry<Identifier, JsonObject>, Suit>> suitTypes = Maps.newHashMap();

    public static void init() {
        registerSuitType(new Identifier(HeroesUnited.MODID, "default"), JsonSuit::new);

        ResourceManager resourceManager = HUPacks.getInstance().getResourceManager();
        LinkedHashMap<Identifier, JsonObject> suits = Maps.newLinkedHashMap();

        for (Identifier resourcelocation : resourceManager.findResources("husuits", (name) -> name.endsWith(".json") && !name.startsWith("_"))) {
            String s = resourcelocation.getPath();
            Identifier id = new Identifier(resourcelocation.getNamespace(), s.substring("husuits/".length(), s.length() - ".json".length()));

            try (Resource iresource = resourceManager.getResource(resourcelocation)) {
                suits.put(id, JsonHelper.deserialize(HUPacks.GSON, new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8)), JsonObject.class));
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack suit {} from {}", id, resourcelocation, throwable);
            }
        }

        for (Map.Entry<Identifier, JsonObject> map : suits.entrySet()) {
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

    public static void registerSuitType(Identifier resourceLocation, Function<Map.Entry<Identifier, JsonObject>, Suit> function) {
        Objects.requireNonNull(resourceLocation);
        Objects.requireNonNull(function);
        suitTypes.put(resourceLocation, function);
    }

    public static Suit parse(Map.Entry<Identifier, JsonObject> map) {
        Function<Map.Entry<Identifier, JsonObject>, Suit> function = suitTypes.get(new Identifier(JsonHelper.getString(map.getValue(), "type")));

        if (function == null) {
            throw new JsonParseException("The type of a suit '" + JsonHelper.getString(map.getValue(), "type") + "' doesn't exist!");
        }

        Suit suit = function.apply(map);
        return Objects.requireNonNull(suit);
    }
}
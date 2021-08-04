package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.events.HURegisterSuperpower;

import java.util.Map;

public class HUPackSuperpowers extends JsonDataLoader {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HUPackSuperpowers INSTANCE;
    private Map<Identifier, Superpower> registeredSuperpowers = Maps.newHashMap();

    public HUPackSuperpowers() {
        super(GSON, "husuperpowers");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager iResourceManager, Profiler iProfiler) {
        for (Map.Entry<Identifier, JsonElement> entry : map.entrySet()) {
            Identifier resourcelocation = entry.getKey();
            try {
                Superpower superpower = new Superpower(resourcelocation, (JsonObject) entry.getValue());
                this.registeredSuperpowers.put(resourcelocation, superpower);
                MinecraftForge.EVENT_BUS.post(new HURegisterSuperpower(this.registeredSuperpowers));
            } catch (Exception e) {
                HeroesUnited.LOGGER.error("Parsing error loading superpower {}", resourcelocation, e);
            }
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerWorld world : server.getWorlds()) {
                for (PlayerEntity player : world.getPlayers()) {
                    Identifier resourceLocation = HUPackSuperpowers.getSuperpower(player);
                    if (player != null && player.isAlive() && resourceLocation != null && registeredSuperpowers.containsKey(resourceLocation)) {
                        HUPackSuperpowers.setSuperpower(player, registeredSuperpowers.get(resourceLocation));
                    }
                }
            }
        }
        HeroesUnited.LOGGER.info("Loaded {} superpowers", this.registeredSuperpowers.size());
    }

    public static Map<Identifier, Superpower> getSuperpowers() {
        Map<Identifier, Superpower> superpowers = Maps.newHashMap();
        for (Map.Entry<Identifier, Superpower> entry : getInstance().registeredSuperpowers.entrySet()) {
            if (!JsonHelper.getBoolean(entry.getValue().jsonObject, "hidden", false)) {
                superpowers.put(entry.getKey(), entry.getValue());
            }
        }
        return superpowers;
    }

    public static HUPackSuperpowers getInstance() {
        return INSTANCE;
    }

    public static Superpower getSuperpower(Identifier location) {
        return getInstance().registeredSuperpowers.get(location);
    }

    public static void setSuperpower(PlayerEntity player, Superpower superpower) {
        try {
            if (!HUPlayer.getCap(player).getSuperpowerLevels().containsKey(superpower.getRegistryName())) {
                HUPlayer.getCap(player).getSuperpowerLevels().put(superpower.getRegistryName(), new Level());
            }
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                cap.clearAbilities(ability -> ability.getAdditionalData().contains("Superpower"));
                cap.addAbilities(superpower);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSuperpower(PlayerEntity player) {
        try {
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> cap.clearAbilities(ability -> ability.getAdditionalData().contains("Superpower")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasSuperpower(PlayerEntity player, Identifier location) {
        for (Ability ability : HUAbilityCap.getCap(player).getAbilities().values()) {
            if (ability.getAdditionalData() != null && ability.getAdditionalData().getString("Superpower").equals(location.toString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSuperpower(PlayerEntity player, Superpower superpower) {
        return hasSuperpower(player, superpower.getRegistryName());
    }

    public static boolean hasSuperpowers(PlayerEntity player) {
        for (Ability ability : HUAbilityCap.getCap(player).getAbilities().values()) {
            if (ability.getAdditionalData().contains("Superpower")) {
                return true;
            }
        }
        return false;
    }

    public static Identifier getSuperpower(PlayerEntity player) {
        for (Ability ability : HUAbilityCap.getCap(player).getAbilities().values()) {
            if (ability.getAdditionalData().contains("Superpower")) {
                return new Identifier(ability.getAdditionalData().getString("Superpower"));
            }
        }
        return null;
    }
}

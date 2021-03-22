package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.events.HURegisterSuperpower;

import java.util.Map;

public class HUPackSuperpowers extends JsonReloadListener {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HUPackSuperpowers INSTANCE;
    private Map<ResourceLocation, Superpower> registeredSuperpowers = Maps.newHashMap();

    public HUPackSuperpowers() {
        super(GSON, "husuperpowers");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager iResourceManager, IProfiler iProfiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            try {
                Superpower superpower = new Superpower(resourcelocation, AbilityHelper.parseAbilityCreators((JsonObject) entry.getValue(), resourcelocation));
                this.registeredSuperpowers.put(resourcelocation, superpower);
                MinecraftForge.EVENT_BUS.post(new HURegisterSuperpower(this.registeredSuperpowers));
            } catch (Exception e) {
                HeroesUnited.LOGGER.error("Parsing error loading superpower {}", resourcelocation, e);
            }
        }
        HeroesUnited.LOGGER.info("Loaded {} superpowers", this.registeredSuperpowers.size());
    }

    public static Map<ResourceLocation, Superpower> getSuperpowers() {
        return getInstance().registeredSuperpowers;
    }

    public static HUPackSuperpowers getInstance() {
        return INSTANCE;
    }

    public static Superpower getSuperpower(ResourceLocation location) {
        return getSuperpowers().get(location);
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

    public static boolean hasSuperpower(PlayerEntity player, ResourceLocation location) {
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

    public static ResourceLocation getSuperpower(PlayerEntity player) {
        for (Ability ability : HUAbilityCap.getCap(player).getAbilities().values()) {
            if (ability.getAdditionalData().contains("Superpower")) {
                return new ResourceLocation(ability.getAdditionalData().getString("Superpower"));
            }
        }
        return null;
    }
}

package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityCreator;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.common.events.HURegisterSuperpower;

import java.util.List;
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
                Superpower superpower = parseSuperpower(resourcelocation, (JsonObject) entry.getValue());
                this.registeredSuperpowers.put(resourcelocation, superpower);
                MinecraftForge.EVENT_BUS.post(new HURegisterSuperpower(this.registeredSuperpowers));
            } catch (Exception e) {
                HeroesUnited.LOGGER.error("Parsing error loading superpower {}", resourcelocation, e);
            }
        }
        HeroesUnited.LOGGER.info("Loaded {} superpowers", this.registeredSuperpowers.size());
    }

    public Superpower parseSuperpower(ResourceLocation resourceLocation, JsonObject json) {
        List<AbilityCreator> abilityList = Lists.newArrayList();
        if (JSONUtils.hasField(json, "abilities")) {
            JsonObject abilities = JSONUtils.getJsonObject(json, "abilities");
            abilities.entrySet().forEach((e) -> {
                if (e.getValue() instanceof JsonObject) {
                    JsonObject o = (JsonObject) e.getValue();
                    AbilityType ability = AbilityType.ABILITIES.getValue(new ResourceLocation(JSONUtils.getString(o, "ability")));
                    if (ability != null) {
                        abilityList.add(new AbilityCreator(e.getKey(), ability).setAdditional(resourceLocation, o));
                    } else
                        HeroesUnited.LOGGER.error("Couldn't read ability {} in superpower {}", JSONUtils.getString(o, "ability"), resourceLocation);
                }
            });

        }
        return new Superpower(resourceLocation, abilityList);
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
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                if(!cap.getSuperpowerLevels().containsKey(superpower.getRegistryName())){
                    cap.getSuperpowerLevels().put(superpower.getRegistryName(),new Level());
                }
                cap.clearAbilities();
                cap.addAbilities(superpower);
                cap.getAbilities().forEach((key, value) -> value.setJsonObject(player, AbilityCreator.createdJsons.get(superpower.getRegistryName().toString() + "_" + key)));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeSuperpower(PlayerEntity player) {
        try {
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.clearAbilities());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasSuperpower(PlayerEntity player, ResourceLocation location) {
        for (Ability ability : HUPlayer.getCap(player).getAbilities().values()) {
            if (ability.getSuperpower() != null && ability.getSuperpower().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSuperpower(PlayerEntity player, Superpower superpower) {
        return hasSuperpower(player, superpower.getRegistryName());
    }

    public static boolean hasSuperpowers(PlayerEntity player) {
        for (Ability ability : HUPlayer.getCap(player).getAbilities().values()) {
            if (ability.getSuperpower() != null) {
                return true;
            }
        }
        return false;
    }
}

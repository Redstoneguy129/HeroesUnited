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
import xyz.heroesunited.heroesunited.common.abilities.AbilityCreator;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.events.HURegisterSuperpower;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
                        abilityList.add(new AbilityCreator(e.getKey(), ability, o));
                    } else HeroesUnited.LOGGER.error("Couldn't read ability {} in superpower {}", JSONUtils.getString(o, "ability"), resourceLocation);
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
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap  -> {
                cap.clearAbilities();
                if (superpower != null) {
                    cap.addAbilities(superpower);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasSuperpower(PlayerEntity player, ResourceLocation location) {
        AtomicBoolean b = new AtomicBoolean(false);
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.getAbilities().forEach(ability -> {
            if (ability.create().getSuperpower() != null && ability.create().getSuperpower().equals(location.toString())
                    && !player.world.isRemote) {
                b.set(true);
            }
        }));
        return b.get();
    }

    public static boolean hasSuperpower(PlayerEntity player, Superpower superpower) {
        return hasSuperpower(player, superpower.getRegistryName());
    }

    public static boolean hasSuperpowers(PlayerEntity player) {
        AtomicBoolean b = new AtomicBoolean(false);
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> cap.getAbilities().forEach(ability -> {
            if(ability.create().getSuperpower() != null && !player.world.isRemote) {
                b.set(true);
            }
        }));
        return b.get();
    }
}

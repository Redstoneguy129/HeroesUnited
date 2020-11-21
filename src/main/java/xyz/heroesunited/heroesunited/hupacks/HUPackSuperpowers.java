package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
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
                HeroesUnited.getLogger().error("Parsing error loading superpower {}", resourcelocation, e);
            }
        }
        HeroesUnited.getLogger().info("Loaded {} superpowers", this.registeredSuperpowers.size());
    }

    public Superpower parseSuperpower(ResourceLocation resourceLocation, JsonObject json) {
        List<AbilityType> types = Lists.newArrayList();
        if (JSONUtils.hasField(json, "abilities")) {
            JsonObject abilities = JSONUtils.getJsonObject(json, "abilities");
            abilities.entrySet().forEach((e) -> {
                if (e.getValue() instanceof JsonObject) {
                    JsonObject o = (JsonObject) e.getValue();
                    AbilityType type = AbilityType.ABILITIES.getValue(new ResourceLocation(JSONUtils.getString(o, "ability")));
                    try {
                        if (type != null) {
                            types.add(type);
                        }
                    } catch (Throwable throwable) {
                        HeroesUnited.getLogger().error("Couldn't read ability {} in superpower {}", type, resourceLocation);
                    }

                }
            });

        }
        return new Superpower(resourceLocation, types);
    }

    public Map<ResourceLocation, Superpower> getSuperpowers() {
        return this.registeredSuperpowers;
    }

    public static HUPackSuperpowers getInstance() {
        return INSTANCE;
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class AbilityCreator {

    //Only for Server dist, so dont use in client
    public static Map<String, JsonObject> createdJsons = Maps.newHashMap();

    private final String key;
    private String creatorKey;
    private final AbilityType abilityType;

    public AbilityCreator(String key, AbilityType abilityType) {
        this.key = key;
        this.abilityType = abilityType;
    }

    public String getKey() {
        return key;
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public AbilityCreator setAdditional(ResourceLocation key, JsonObject jsonObject) {
        this.creatorKey = key.toString() + "_" + this.getKey();
        if (createdJsons.containsKey(this.creatorKey)) createdJsons.remove(this.creatorKey, jsonObject);
        createdJsons.put(this.creatorKey, jsonObject);
        return this;
    }
}

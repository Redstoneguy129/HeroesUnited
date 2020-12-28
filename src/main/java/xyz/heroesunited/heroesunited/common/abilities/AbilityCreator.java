package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import java.util.Map;

public class AbilityCreator {

    //Only for Server dist, so dont use in client
    public static Map<String, JsonObject> createdJsons = Maps.newHashMap();

    private final String key, superpower;
    private final AbilityType abilityType;
    private JsonObject jsonObject;

    public AbilityCreator(String key, AbilityType abilityType, String superpower) {
        this.key = key;
        this.abilityType = abilityType;
        this.superpower = superpower;
    }

    public AbilityCreator(String key, AbilityType abilityType, String superpower, JsonObject jsonObject) {
        this(key, abilityType, superpower);
        this.jsonObject = jsonObject;
        if (createdJsons.containsKey(key)) {
            createdJsons.remove(key, jsonObject);
        }
        createdJsons.put(key, jsonObject);
    }

    public String getKey() {
        return key;
    }

    public AbilityType getAbilityType() {
        return abilityType;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public Ability create() {
        return this.getAbilityType().create(this.getKey()).setSuperpower(superpower);
    }
}

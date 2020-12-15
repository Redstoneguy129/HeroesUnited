package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import java.util.Map;

public class AbilityCreator {

    public static final Map<String, AbilityCreator> createdAbilities = Maps.newHashMap();

    private final String key;
    private final AbilityType abilityType;
    private JsonObject jsonObject;

    public AbilityCreator(String key, AbilityType abilityType) {
        this.key = key;
        this.abilityType = abilityType;

        if (createdAbilities.containsKey(key)) {
            createdAbilities.remove(key, this);
        }
        createdAbilities.put(key, this);
    }

    public AbilityCreator(String key, AbilityType abilityType, JsonObject jsonObject) {
        this(key, abilityType);
        this.jsonObject = jsonObject;
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
        return this.getAbilityType().create(this.getKey());
    }
}

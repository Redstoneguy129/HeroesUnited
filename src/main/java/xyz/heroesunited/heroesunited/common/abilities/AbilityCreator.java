package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;

public class AbilityCreator {

    private final String key;
    private final AbilityType abilityType;
    private final JsonObject jsonObject;

    public AbilityCreator(String key, AbilityType abilityType, JsonObject jsonObject) {
        this.key = key;
        this.abilityType = abilityType;
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
}

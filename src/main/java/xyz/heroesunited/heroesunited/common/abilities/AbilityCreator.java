package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;

public class AbilityCreator {

    private String key;
    private AbilityType abilityType;
    private JsonObject jsonObject;

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

    public AbilityCreator setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}

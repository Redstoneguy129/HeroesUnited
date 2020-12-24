package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

public class AbilityCreator {

    public static final Map<String, AbilityCreator> createdAbilities = Maps.newHashMap();

    private final String key;
    private final AbilityType abilityType;
    private ITextComponent textComponent;
    private JsonObject jsonObject;
    private String superpower;

    public AbilityCreator(String key, AbilityType abilityType, ITextComponent textComponent) {
        this.key = key;
        this.abilityType = abilityType;
        this.textComponent = textComponent;
        if (createdAbilities.containsKey(key)) {
            createdAbilities.remove(key, this);
        }
        createdAbilities.put(key, this);
    }

    public AbilityCreator(String key, AbilityType abilityType) {
        this(key, abilityType, new TranslationTextComponent(key));
    }

    public AbilityCreator(String key, AbilityType abilityType, JsonObject jsonObject) {
        this(key, abilityType, new TranslationTextComponent(key));
        this.jsonObject = jsonObject;
    }

    public String getKey() {
        return key;
    }

    public ITextComponent getTextComponent() {
        if (getJsonObject() != null && JSONUtils.hasField(getJsonObject(), "title")) {
            return ITextComponent.Serializer.getComponentFromJson(JSONUtils.getJsonObject(getJsonObject(), "title"));
        } else {
            return textComponent;
        }
    }

    public AbilityCreator setSuperpower(String superpower) {
        this.superpower = superpower;
        this.create().setSuperpower(superpower);
        return this;
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

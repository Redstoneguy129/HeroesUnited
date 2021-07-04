package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class Superpower implements IAbilityProvider {

    private final ResourceLocation name;
    public JsonObject jsonObject;
    private List<AbilityCreator> containedAbilities;

    public Superpower(ResourceLocation name) {
        this.name = name;
    }

    public Superpower(ResourceLocation name, List<AbilityCreator> containedAbilities) {
        this.name = name;
        this.containedAbilities = containedAbilities;
    }

    public Superpower(ResourceLocation name, JsonObject jsonObject) {
        this(name, AbilityHelper.parseAbilityCreators(jsonObject, name));
        this.jsonObject = jsonObject;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeDescriptionId("superpowers", name));
    }

    @Nullable
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public Map<String, Ability> getAbilities(PlayerEntity player) {
        Map<String, Ability> map = Maps.newHashMap();
        getContainedAbilities().forEach(a -> {
            Ability ability = a.getAbilityType().create(a.getKey());
            ability.getAdditionalData().putString("Superpower", this.getRegistryName().toString());
            if (a.getJsonObject() != null) {
                ability.setJsonObject(player, a.getJsonObject());
            }
            map.put(ability.name, ability);
        });
        return map;
    }

    public List<AbilityCreator> getContainedAbilities() {
        return containedAbilities;
    }
}
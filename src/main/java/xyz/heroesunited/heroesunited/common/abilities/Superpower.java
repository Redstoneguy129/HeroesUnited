package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.Map;

public class Superpower {

    private final ResourceLocation name;
    private List<AbilityCreator> containedAbilities;

    public Superpower(ResourceLocation name) {
        this.name = name;
    }

    public Superpower(ResourceLocation name, List<AbilityCreator> containedAbilities) {
        this.name = name;
        this.containedAbilities = containedAbilities;
    }

    public List<AbilityCreator> getContainedAbilities(PlayerEntity player) {
        return containedAbilities;
    }

    public Map<String, Ability> getAbilities(PlayerEntity player) {
        Map<String, Ability> map = Maps.newHashMap();
        this.getContainedAbilities(player).forEach(a -> {
            Ability ability = a.create();
            ability.setSuperpower(this.getRegistryName().toString());
            map.put(ability.name, ability);
        });
        return map;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("superpowers", name));
    }

    public ResourceLocation getRegistryName() {
        return name;
    }
}
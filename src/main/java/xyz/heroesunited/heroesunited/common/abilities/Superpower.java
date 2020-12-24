package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

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

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("superpowers", name));
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    public List<AbilityCreator> getAbilities(PlayerEntity player) {
        List<AbilityCreator> list = Lists.newArrayList();
        this.getContainedAbilities(player).forEach(a -> {
            list.add(a.setSuperpower(this.getRegistryName().toString()));
        });
        return list;
    }
}
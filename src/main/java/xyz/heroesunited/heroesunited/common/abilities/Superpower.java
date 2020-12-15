package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import javax.annotation.Nonnull;
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

    @Nonnull
    public static Superpower getSuperpower(PlayerEntity player) {
        return HUPlayer.getCap(player).getSuperpower();
    }

    @Nonnull
    public static Map<String, Ability> getTypesFromSuperpower(PlayerEntity player) {
        Map<String, Ability> map = Maps.newHashMap();
        Superpower power = Superpower.getSuperpower(player);
        if (power != null) {
            power.getContainedAbilities(player).forEach(a -> map.put(a.getKey(), a.create()));
        }
        return map;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("superpowers", name));
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    public CompoundNBT serializeNBT(PlayerEntity player) {
        CompoundNBT nbt = new CompoundNBT();
        //CompoundNBT abilities = new CompoundNBT();
        getContainedAbilities(player).forEach(a -> nbt.put(a.getKey(), a.create().serializeNBT()));
        //nbt.put("contain", abilities);
        nbt.putString("name", name.toString());
        return nbt;
    }

    public static Superpower deserializeNBT(CompoundNBT nbt) {
        Superpower superpower = HUPackSuperpowers.getInstance().getSuperpowers().get(new ResourceLocation(nbt.getString("name")));
        if (superpower != null) {
            superpower.containedAbilities.clear();
            for (String id : nbt.keySet()) {
                CompoundNBT tag = nbt.getCompound(id);
                AbilityType abilityType = AbilityType.ABILITIES.getValue(new ResourceLocation(tag.getString("AbilityType")));
                if (abilityType != null) {
                    Ability ability = abilityType.create(id);
                    ability.deserializeNBT(tag);
                    superpower.containedAbilities.add(AbilityCreator.createdAbilities.get(id));
                    ability.name = id;
                }
            }
        }
        return superpower;
    }
}

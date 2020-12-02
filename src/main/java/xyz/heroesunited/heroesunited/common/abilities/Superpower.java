package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import javax.annotation.Nonnull;
import java.util.List;

public class Superpower {

    private final ResourceLocation name;
    private List<AbilityType> containedAbilities;

    public Superpower(ResourceLocation name) {
        this.name = name;
    }

    public Superpower(ResourceLocation name, List<AbilityType> containedAbilities) {
        this.name = name;
        this.containedAbilities = containedAbilities;
    }

    public List<AbilityType> getContainedAbilities(PlayerEntity player) {
        return containedAbilities;
    }

    @Nonnull
    public static Superpower getSuperpower(PlayerEntity player) {
        return HUPlayer.getCap(player).getSuperpower();
    }

    @Nonnull
    public static List<AbilityType> getTypesFromSuperpower(PlayerEntity player) {
        List<AbilityType> list = Lists.newArrayList();
        for (AbilityType type : AbilityType.ABILITIES) {
            Superpower power = Superpower.getSuperpower(player);
            if (power != null && power.getContainedAbilities(player).contains(type)) {
                list.add(type);
            }
        }
        return list;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("superpowers", name));
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    public CompoundNBT serializeNBT(PlayerEntity player) {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT listNBT = new ListNBT();
        nbt.putString("name", name.toString());
        List<AbilityType> types = getContainedAbilities(player);
        for (AbilityType type : types) {
            listNBT.add(StringNBT.valueOf(type.getRegistryName().toString()));
        }
        nbt.put("contain", listNBT);
        return nbt;
    }

    public static Superpower deserializeNBT(CompoundNBT nbt) {
        Superpower superpower = HUPackSuperpowers.getInstance().getSuperpowers().get(new ResourceLocation(nbt.getString("name")));
        if (superpower != null) {
            superpower.containedAbilities = Lists.newArrayList();
            ListNBT listNBT = nbt.getList("contain", Constants.NBT.TAG_STRING);

            for (int i = 0; i < listNBT.size(); i++) {
                AbilityType type = AbilityType.ABILITIES.getValue(new ResourceLocation(listNBT.getString(i)));
                if (type != null) superpower.containedAbilities.add(type);
            }
        }
        return superpower;
    }
}

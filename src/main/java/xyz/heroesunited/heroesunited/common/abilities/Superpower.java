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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Superpower extends ForgeRegistryEntry<Superpower> {

    public static IForgeRegistry<Superpower> SUPERPOWERS;
    private List<AbilityType> containedAbilities;

    public Superpower() {
    }

    public Superpower(List<AbilityType> containerAbilities) {
        this.containedAbilities = containerAbilities;
    }

    public List<AbilityType> getContainedAbilities(PlayerEntity player) {
        return containedAbilities;
    }

    @Nonnull
    public static Superpower getSuperpower(PlayerEntity player) {
        return HUPlayer.getCap(player).getSuperpower();
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("superpowers", this.getRegistryName()));
    }

    public CompoundNBT serializeNBT(PlayerEntity player) {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT listNBT = new ListNBT();
        nbt.putString("name", this.getRegistryName().toString());
        List<AbilityType> types = getContainedAbilities(player);
        for (AbilityType type : types) {
            listNBT.add(StringNBT.valueOf(type.getRegistryName().toString()));
        }
        nbt.put("contain", listNBT);
        return nbt;
    }

    public static Superpower deserializeNBT(CompoundNBT nbt) {
        Superpower superpower = Superpower.SUPERPOWERS.getValue(new ResourceLocation(nbt.getString("name")));
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        SUPERPOWERS = new RegistryBuilder<Superpower>().setName(new ResourceLocation(HeroesUnited.MODID, "superpowers")).setType(Superpower.class).setIDRange(0, 2048).create();
    }
}

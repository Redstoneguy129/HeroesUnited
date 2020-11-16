package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityType extends ForgeRegistryEntry<AbilityType> {

    public static IForgeRegistry<AbilityType> ABILITIES;

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("ability", this.getRegistryName()));
    }

    private Supplier<Ability> supplier;

    public AbilityType(Supplier<Ability> supplier) {
        this.supplier = supplier;
    }

    public AbilityType(Supplier<Ability> supplier, String modid, String name) {
        this.supplier = supplier;
        this.setRegistryName(modid, name);
    }

    public Ability create() {
        return this.supplier.get();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        ABILITIES = new RegistryBuilder<AbilityType>().setName(new ResourceLocation(HeroesUnited.MODID, "ability_types")).setType(AbilityType.class).setIDRange(0, 2048).create();
    }
}
package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
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
    private Supplier<Ability> supplier;

    public AbilityType(Supplier<Ability> supplier) {
        this.supplier = supplier;
    }

    public AbilityType(Supplier<Ability> supplier, String modid, String name) {
        this.supplier = supplier;
        this.setRegistryName(modid, name);
    }

    public Ability create(String id) {
        Ability a = this.supplier.get();
        a.name = id;
        return a;
    }

    public Ability create(String id, JsonObject jsonObject) {
        Ability a = this.supplier.get();
        a.name = id;
        a.setJsonObject(jsonObject);
        return a;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeTranslationKey("ability", this.getRegistryName()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        ABILITIES = new RegistryBuilder<AbilityType>().setName(new ResourceLocation(HeroesUnited.MODID, "ability_types")).setType(AbilityType.class).setIDRange(0, 2048).create();
    }

    public static final AbilityType ATTRIBUTE_MODIFIER = new AbilityType(AttributeModifierAbility::new, HeroesUnited.MODID, "attribute_modifier");
    public static final AbilityType FLIGHT = new AbilityType(FlightAbility::new, HeroesUnited.MODID, "flight");

    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<AbilityType> e) {
        e.getRegistry().register(ATTRIBUTE_MODIFIER);
        e.getRegistry().register(FLIGHT);
    }
}
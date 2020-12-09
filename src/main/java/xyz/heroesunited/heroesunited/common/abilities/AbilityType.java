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
    private boolean hidden, active;
    private JsonObject jsonObject;
    private ITextComponent displayName;

    public AbilityType(Supplier<Ability> supplier) {
        this.supplier = supplier;
    }

    public AbilityType(Supplier<Ability> supplier, String modid, String name) {
        this.supplier = supplier;
        this.setRegistryName(modid, name);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean alwaysActive() {
        return active;
    }

    public void setAlwaysActive(boolean active) {
        this.active = active;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public Ability create() {
        return this.supplier.get();
    }

    public void setDisplayName(String key) {
        this.displayName = new TranslationTextComponent(Util.makeTranslationKey("ability", new ResourceLocation(key)));
    }

    public ITextComponent getDisplayName() {
        return displayName == null ? new TranslationTextComponent(Util.makeTranslationKey("ability", this.getRegistryName())) : displayName;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        ABILITIES = new RegistryBuilder<AbilityType>().setName(new ResourceLocation(HeroesUnited.MODID, "ability_types")).setType(AbilityType.class).setIDRange(0, 2048).create();
    }

    public static final AbilityType ATTRIBUTE_MODIFIER = new AbilityType(AttributeModifierAbility::new, HeroesUnited.MODID, "attribute_modifier");

    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<AbilityType> e) {
        e.getRegistry().register(ATTRIBUTE_MODIFIER);
    }
}
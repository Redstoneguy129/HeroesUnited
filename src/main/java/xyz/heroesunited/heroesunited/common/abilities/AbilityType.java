package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
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

    public static final AbilityType ATTRIBUTE_MODIFIER = new AbilityType(AttributeModifierAbility::new, HeroesUnited.MODID, "attribute_modifier");
    public static final AbilityType FLIGHT = new AbilityType(FlightAbility::new, HeroesUnited.MODID, "flight");
    public static final AbilityType SLOW_MO = new AbilityType(SlowMoAbility::new, HeroesUnited.MODID, "slow_mo");
    public static final AbilityType GECKO = new AbilityType(GeckoAbility::new, HeroesUnited.MODID, "gecko");
    public static final AbilityType HIDE_BODY_PARTS = new AbilityType(HideBodyPartsAbility::new, HeroesUnited.MODID, "hide_body_parts");
    public static final AbilityType ROTATE_PARTS = new AbilityType(RotatePartsAbility::new, HeroesUnited.MODID, "rotate_parts");
    public static final AbilityType SIZE_CHANGE = new AbilityType(SizeChangeAbility::new, HeroesUnited.MODID, "size_change");
    public static final AbilityType COMMAND = new AbilityType(CommandAbility::new, HeroesUnited.MODID, "command");
    public static final AbilityType DAMAGE_IMMUNITY = new AbilityType(DamageImmunityAbility::new, HeroesUnited.MODID, "damage_immunity");
    public static final AbilityType POTION_EFFECT = new AbilityType(PotionEffectAbility::new, HeroesUnited.MODID, "potion_effect");
    public static final AbilityType ENERGY_LASER = new AbilityType(EnergyLaserAbility::new, HeroesUnited.MODID, "energy_laser");
    public static final AbilityType HEAT_VISION = new AbilityType(HeatVisionAbility::new, HeroesUnited.MODID, "heat_vision");
    public static final AbilityType OXYGEN = new AbilityType(OxygenAbility::new, HeroesUnited.MODID, "oxygen");
    public static final AbilityType PROJECTILE = new AbilityType(ProjectileAbility::new, HeroesUnited.MODID, "projectile");
    public static final AbilityType HIDE_LAYER = new AbilityType(HideLayerAbility::new, HeroesUnited.MODID, "hide_layer");

    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<AbilityType> e) {
        e.getRegistry().register(ATTRIBUTE_MODIFIER);
        e.getRegistry().register(FLIGHT);
        e.getRegistry().register(SLOW_MO);
        e.getRegistry().register(GECKO);
        e.getRegistry().register(HIDE_BODY_PARTS);
        e.getRegistry().register(SIZE_CHANGE);
        e.getRegistry().register(COMMAND);
        e.getRegistry().register(DAMAGE_IMMUNITY);
        e.getRegistry().register(ROTATE_PARTS);
        e.getRegistry().register(POTION_EFFECT);
        e.getRegistry().register(ENERGY_LASER);
        e.getRegistry().register(HEAT_VISION);
        e.getRegistry().register(OXYGEN);
        e.getRegistry().register(PROJECTILE);
        e.getRegistry().register(HIDE_LAYER);
    }
}
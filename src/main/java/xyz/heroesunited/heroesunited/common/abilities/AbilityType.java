package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.events.AbilityEvent;
import xyz.heroesunited.heroesunited.util.hudata.HUData;

import java.util.Map;

public class AbilityType extends ForgeRegistryEntry<AbilityType> {

    public static final DeferredRegister<AbilityType> ABILITY_TYPES = DeferredRegister.create(AbilityType.class, HeroesUnited.MODID);
    public static final Lazy<IForgeRegistry<AbilityType>> ABILITIES = Lazy.of(ABILITY_TYPES.makeRegistry("ability_types", () -> new RegistryBuilder<AbilityType>().setType(AbilityType.class).setIDRange(0, 2048)));

    private final AbilitySupplier supplier;

    public AbilityType(AbilitySupplier supplier) {
        this.supplier = supplier;
    }
    
    public AbilityType(AbilitySupplier supplier, String modid, String name) {
        this(supplier);
        this.setRegistryName(modid, name);
    }

    public static Ability fromNBT(Player player, String id, CompoundTag tag) {
        AbilityType type = AbilityType.ABILITIES.get().getValue(new ResourceLocation(tag.getString("AbilityType")));
        if (type != null) {
            Ability ability = type.create(player, id, GsonHelper.parse(tag.getString("JsonObject")));
            ability.deserializeNBT(tag);
            return ability;
        }
        return null;
    }

    public Ability create(Player player, String id, JsonObject jsonObject) {
        Ability a = this.supplier.create(this, player, jsonObject);
        a.name = id;
        a.registerData();
        MinecraftForge.EVENT_BUS.post(new AbilityEvent.RegisterData(player, a));
        for (Map.Entry<String, HUData<?>> entry : a.dataManager.getHUDataMap().entrySet()) {
            if (entry.getValue().isJson()) {
                a.dataManager.set(entry.getKey(), entry.getValue().getFromJson(jsonObject, entry.getKey(), entry.getValue().getDefaultValue()));
            }
        }
        return a;
    }

    public static final AbilityType ATTRIBUTE_MODIFIER = register("attribute_modifier", AttributeModifierAbility::new);
    public static final AbilityType FLIGHT = register("flight", FlightAbility::new);
    public static final AbilityType SLOW_MO = register("slow_mo", SlowMoAbility::new);
    public static final AbilityType GECKO = register("gecko", GeckoAbility::new);
    public static final AbilityType HIDE_PARTS = register("hide_parts", HidePartsAbility::new);
    public static final AbilityType ROTATE_PARTS = register("rotate_parts", RotatePartsAbility::new);
    public static final AbilityType SIZE_CHANGE = register("size_change", SizeChangeAbility::new);
    public static final AbilityType COMMAND = register("command", CommandAbility::new);
    public static final AbilityType DAMAGE_IMMUNITY = register("damage_immunity", DamageImmunityAbility::new);
    public static final AbilityType POTION_EFFECT = register("potion_effect", PotionEffectAbility::new);
    public static final AbilityType ENERGY_LASER = register("energy_laser", EnergyLaserAbility::new);
    public static final AbilityType HEAT_VISION = register("heat_vision", HeatVisionAbility::new);
    public static final AbilityType OXYGEN = register("oxygen", OxygenAbility::new);
    public static final AbilityType PROJECTILE = register("projectile", ProjectileAbility::new);
    public static final AbilityType HIDE_LAYER = register("hide_layer", HideLayerAbility::new);
    public static final AbilityType PARACHUTE = register("parachute", ParachuteAbility::new);
    public static final AbilityType CANCEL_SPRINT = register("cancel_sprint", CancelSprintAbility::new);
    public static final AbilityType GLIDING = register("gliding", GlidingAbility::new);

    private static AbilityType register(String name, AbilitySupplier ability) {
        AbilityType type = new AbilityType(ability);
        ABILITY_TYPES.register(name, () -> type);
        return type;
    }

    @FunctionalInterface
    public interface AbilitySupplier {
        Ability create(AbilityType type, Player player, JsonObject jsonObject);
    }
}
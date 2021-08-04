package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Condition extends ForgeRegistryEntry<Condition> {

    public static IForgeRegistry<Condition> CONDITIONS;
    private BiFunction<PlayerEntity, JsonObject, Boolean> biFunction;

    public Condition(BiFunction<PlayerEntity, JsonObject, Boolean> biFunction) {
        this.biFunction = biFunction;
    }

    public Condition(BiFunction<PlayerEntity, JsonObject, Boolean> biFunction, String modid, String name) {
        this.biFunction = biFunction;
        this.setRegistryName(modid, name);
    }

    public void whenSetEnabled(JSONAbility ability, Map.Entry<JsonObject, UUID> e, PlayerEntity player, boolean enabled) {
    }

    public BiFunction<PlayerEntity, JsonObject, Boolean> getBiFunction() {
        return biFunction;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        CONDITIONS = new RegistryBuilder<Condition>().setName(new Identifier(HeroesUnited.MODID, "conditions")).setType(Condition.class).setIDRange(0, 2048).create();
    }

    public static final Condition HAS_SUPERPOWERS = new Condition((player, e) -> HUPackSuperpowers.hasSuperpowers(player), HeroesUnited.MODID, "has_superpowers");
    public static final Condition HAS_SUPERPOWER = new Condition((player, e) -> HUPackSuperpowers.hasSuperpower(player, new Identifier(JsonHelper.getString(e, "superpower"))), HeroesUnited.MODID, "has_superpower");
    public static final Condition ACTIVATED_ABILITY = new Condition((player, e) -> AbilityHelper.getEnabled(JsonHelper.getString(e, "ability"), player), HeroesUnited.MODID, "activated_ability");
    public static final Condition HAS_LEVEL = new Condition((player, e) -> {
        IHUPlayer hu = HUPlayer.getCap(player);
        if (hu != null) {
            Level level = hu.getSuperpowerLevels().get(new Identifier(JsonHelper.getString(e, "superpower")));
            return level.getLevel() == JsonHelper.getInt(e, "level");
        }
        return false;
    }, HeroesUnited.MODID, "has_level");
    public static final Condition HAS_ITEM = new Condition((player, e) -> {
        Item item = Registry.ITEM.get(new Identifier(JsonHelper.getString(e, "item")));
        boolean b = false;
        if (e.has("slots")) {
            JsonArray array = JsonHelper.getArray(e, "slots");
            for (int i = 0; i < array.size(); i++) {
                if (player.getEquippedStack(EquipmentSlot.byName(array.get(i).getAsString().toLowerCase())).getItem() == item) {
                    b = true;
                    break;
                }
            }
        } else {
            if (player.getEquippedStack(EquipmentSlot.byName(JsonHelper.getString(e, "slot", EquipmentSlot.MAINHAND.getName()).toLowerCase())).getItem() == item) {
                b = true;
            }
        }
        return b;
    }, HeroesUnited.MODID, "has_item");


    public static final Condition ABILITY_ENABLED = new Condition((player, jsonObject) -> {
        Ability ability = AbilityHelper.getActiveAbilityMap(player).getOrDefault(JsonHelper.getString(jsonObject, "ability"), null);
        if (ability instanceof JSONAbility) {
            return ((JSONAbility) ability).getEnabled();
        }
        return false;
    }, HeroesUnited.MODID, "ability_enabled");

    @SubscribeEvent
    public static void registerConditions(RegistryEvent.Register<Condition> e) {
        e.getRegistry().register(HAS_SUPERPOWERS);
        e.getRegistry().register(HAS_SUPERPOWER);
        e.getRegistry().register(ACTIVATED_ABILITY);
        e.getRegistry().register(HAS_LEVEL);
        e.getRegistry().register(HAS_ITEM);
        e.getRegistry().register(ABILITY_ENABLED);
    }
}

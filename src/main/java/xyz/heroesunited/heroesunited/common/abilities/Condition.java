package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.util.internal.StringUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.util.function.BiFunction;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Condition extends ForgeRegistryEntry<Condition> {

    public static final DeferredRegister<Condition> CONDITIONS = DeferredRegister.create(Condition.class, HeroesUnited.MODID);
    public static final Lazy<IForgeRegistry<Condition>> REGISTRY = Lazy.of(CONDITIONS.makeRegistry("conditions", () -> new RegistryBuilder<Condition>().setType(Condition.class).setIDRange(0, 2048)));

    private final BiFunction<Player, JsonObject, Boolean> biFunction;

    public Condition(BiFunction<Player, JsonObject, Boolean> biFunction) {
        this.biFunction = biFunction;
    }

    public Condition(BiFunction<Player, JsonObject, Boolean> biFunction, String modid, String name) {
        this.biFunction = biFunction;
        this.setRegistryName(modid, name);
    }

    public BiFunction<Player, JsonObject, Boolean> getBiFunction() {
        return biFunction;
    }

    public static final Condition HAS_SUPERPOWERS = register("has_superpowers", new Condition((player, e) -> HUPackSuperpowers.hasSuperpowers(player)));
    public static final Condition HAS_SUPERPOWER = register("has_superpower", new Condition((player, e) -> HUPackSuperpowers.hasSuperpower(player, new ResourceLocation(GsonHelper.getAsString(e, "superpower")))));
    public static final Condition ACTIVATED_ABILITY = register("activated_ability", new Condition((player, e) -> AbilityHelper.getEnabled(GsonHelper.getAsString(e, "ability"), player)));
    public static final Condition HAS_LEVEL = register("has_level", new Condition((player, e) -> {
        IHUPlayer hu = HUPlayer.getCap(player);
        if (hu != null) {
            Level level = hu.getSuperpowerLevels().get(new ResourceLocation(GsonHelper.getAsString(e, "superpower")));
            return level.getLevel() >= GsonHelper.getAsInt(e, "level");
        }
        return false;
    }));

    public static final Condition HAS_ITEM = register("has_item", new Condition((player, e) -> {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(e, "item")));
        boolean b = false;
        if (e.has("slots")) {
            JsonArray array = GsonHelper.getAsJsonArray(e, "slots");
            for (int i = 0; i < array.size(); i++) {
                if (player.getItemBySlot(EquipmentSlot.byName(array.get(i).getAsString().toLowerCase())).getItem() == item) {
                    b = true;
                    break;
                }
            }
        } else {
            if (player.getItemBySlot(EquipmentSlot.byName(GsonHelper.getAsString(e, "slot", EquipmentSlot.MAINHAND.getName()).toLowerCase())).getItem() == item) {
                b = true;
            }
        }
        return b;
    }));

    public static final Condition ABILITY_ENABLED = register("ability_enabled", new Condition((player, e) -> {
        Ability ability = AbilityHelper.getActiveAbilityMap(player).getOrDefault(GsonHelper.getAsString(e, "ability"), null);
        if (ability != null) {
            return ability.getEnabled();
        }
        return false;
    }));

    public static final Condition HAS_SUIT = register("has_suit", new Condition((player, e) -> {
        String suitName = GsonHelper.getAsString(e, "suit", "");
        if (!StringUtil.isNullOrEmpty(suitName)) {
            return Suit.getSuit(player).getRegistryName().toString().equals(suitName);
        }
        return Suit.getSuit(player) != null;
    }));

    public static final Condition IS_IN_FLUID = register("is_in_fluid", new Condition((player, e) -> {
        for(Tag<Fluid> tag : FluidTags.getAllTags().getAllTags().values()) {
            if (tag instanceof Tag.Named && ((Tag.Named<Fluid>) tag).getName().getPath().equals(GsonHelper.getAsString(e, "fluid"))) {
                return player.isEyeInFluid(tag);
            }
        }
        return false;
    }));

    public static final Condition IS_SPRINTING = register("is_sprinting", new Condition((player, e) -> player.isSprinting()));

    private static Condition register(String name, Condition condition) {
        CONDITIONS.register(name, () -> condition);
        return condition;
    }
}

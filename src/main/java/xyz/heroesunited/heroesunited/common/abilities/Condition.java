package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.util.internal.StringUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.*;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.util.function.Function;

public class Condition extends ForgeRegistryEntry<Condition> {

    public static final DeferredRegister<Condition> CONDITIONS = DeferredRegister.create(Condition.class, HeroesUnited.MODID);
    public static final Lazy<IForgeRegistry<Condition>> REGISTRY = Lazy.of(CONDITIONS.makeRegistry("conditions", () -> new RegistryBuilder<Condition>().setType(Condition.class).setIDRange(0, 2048)));

    private final Function<ConditionVariables, Boolean> function;

    public Condition(Function<ConditionVariables, Boolean> function) {
        this.function = function;
    }

    public Condition(Function<ConditionVariables, Boolean> function, String modid, String name) {
        this.function = function;
        this.setRegistryName(modid, name);
    }

    public boolean apply(PlayerEntity player, JsonObject jsonObject, Ability ability) {
        return function.apply(new ConditionVariables(player, jsonObject, ability));
    }

    public static final Condition HAS_SUPERPOWERS = register("has_superpowers", new Condition((c) -> HUPackSuperpowers.hasSuperpowers(c.player())));
    public static final Condition HAS_SUPERPOWER = register("has_superpower", new Condition((c) -> HUPackSuperpowers.hasSuperpower(c.player(), new ResourceLocation(JSONUtils.getAsString(c.jsonObject(), "superpower")))));
    public static final Condition ACTIVATED_ABILITY = register("activated_ability", new Condition((c) -> AbilityHelper.isActivated(JSONUtils.getAsString(c.jsonObject(), "ability", c.ability().name), c.player())));
    public static final Condition HAS_LEVEL = register("has_level", new Condition((c) -> {
        IHUPlayer hu = HUPlayer.getCap(c.player());
        if (hu != null) {
            Level level = hu.getSuperpowerLevels().get(c.jsonObject().has("superpower") ? new ResourceLocation(JSONUtils.getAsString(c.jsonObject(), "superpower")) : HUPackSuperpowers.getSuperpower(c.player()));
            return level.getLevel() >= JSONUtils.getAsInt(c.jsonObject(), "level");
        }
        return false;
    }));

    public static final Condition HAS_ITEM = register("has_item", new Condition((c) -> {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getAsString(c.jsonObject(), "item")));
        boolean b = false;
        if (c.jsonObject().has("slots")) {
            JsonArray array = JSONUtils.getAsJsonArray(c.jsonObject(), "slots");
            for (int i = 0; i < array.size(); i++) {
                if (c.player().getItemBySlot(EquipmentSlotType.byName(array.get(i).getAsString().toLowerCase())).getItem() == item) {
                    b = true;
                    break;
                }
            }
        } else {
            if (c.player().getItemBySlot(EquipmentSlotType.byName(JSONUtils.getAsString(c.jsonObject(), "slot", EquipmentSlotType.MAINHAND.getName()).toLowerCase())).getItem() == item) {
                b = true;
            }
        }
        return b;
    }));

    public static final Condition ABILITY_ENABLED = register("ability_enabled", new Condition((c) -> {
        Ability ability = AbilityHelper.getActiveAbilityMap(c.player()).getOrDefault(JSONUtils.getAsString(c.jsonObject(), "ability"), c.ability());
        if (ability != null) {
            return ability.getEnabled();
        }
        return false;
    }));

    public static final Condition HAS_SUIT = register("has_suit", new Condition((c) -> {
        Suit suit = Suit.getSuit(c.player());
        String suitName = JSONUtils.getAsString(c.jsonObject(), "suit", "");
        if (!StringUtil.isNullOrEmpty(suitName) && suit != null) {
            return suit.getRegistryName().toString().equals(suitName);
        }
        return suit != null;
    }));

    public static final Condition IS_IN_FLUID = register("is_in_fluid", new Condition((c) -> {
        for (ITag.INamedTag<Fluid> tag : FluidTags.getWrappers()) {
            if (tag.getName().getPath().equals(JSONUtils.getAsString(c.jsonObject(), "fluid"))) {
                return c.player().isEyeInFluid(tag);
            }
        }
        return false;
    }));

    public static final Condition IS_SPRINTING = register("is_sprinting", new Condition((c) -> c.player().isSprinting()));
    public static final Condition IS_SNEAKING = register("is_sneaking", new Condition((c) -> c.player().isCrouching()));

    private static Condition register(String name, Condition condition) {
        CONDITIONS.register(name, () -> condition);
        return condition;
    }


    public static class ConditionVariables {
        private final PlayerEntity player;
        private final JsonObject jsonObject;
        private final Ability ability;

        private ConditionVariables(PlayerEntity player, JsonObject jsonObject, Ability ability) {
            this.player = player;
            this.jsonObject = jsonObject;
            this.ability = ability;
        }

        public PlayerEntity player() {
            return player;
        }

        public JsonObject jsonObject() {
            return jsonObject;
        }

        public Ability ability() {
            return ability;
        }
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.util.internal.StringUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Condition extends ForgeRegistryEntry<Condition> {

    public static final ResourceLocation REGISTRY_KEY = new ResourceLocation(HeroesUnited.MODID, "conditions");
    public static final DeferredRegister<Condition> CONDITIONS = DeferredRegister.create(REGISTRY_KEY, HeroesUnited.MODID);
    public static Supplier<IForgeRegistry<Condition>> REGISTRY = () -> null;

    private final Predicate<ConditionVariables> function;
    private Consumer<ConditionVariables> earlyFunction = (c) -> {};

    public Condition(Predicate<ConditionVariables> function) {
        this.function = function;
    }

    public Condition(Predicate<ConditionVariables> function, Consumer<ConditionVariables> earlyFunction) {
        this(function);
        this.earlyFunction = earlyFunction;
    }

    public Condition(Predicate<ConditionVariables> function, String modid, String name) {
        this(function);
        this.setRegistryName(modid, name);
    }

    public boolean apply(Player player, JsonObject jsonObject, Ability ability) {
        ConditionVariables variables = new ConditionVariables(player, jsonObject, ability);
        boolean invert = GsonHelper.getAsBoolean(jsonObject, "invert", false);
        this.earlyFunction.accept(variables);
        if (jsonObject.has("creative") && player.isCreative()) {
            return true;
        }
        return invert != function.test(variables);
    }

    public static final Condition HAS_SUPERPOWER = register("has_superpower", new Condition((c) -> {
        if (c.jsonObject().has("superpower")) {
            return HUPackSuperpowers.hasSuperpower(c.player(), new ResourceLocation(GsonHelper.getAsString(c.jsonObject(), "superpower")));
        }
        return HUPackSuperpowers.hasSuperpowers(c.player());
    }));
    public static final Condition ACTIVATED_ABILITY = register("activated_ability", new Condition((c) -> {
        if (c.jsonObject().has("abilities")) {
            List<String> list = HUJsonUtils.getStringsFromArray(c.jsonObject().getAsJsonArray("abilities"));
            if (c.jsonObject().has("filter")) {
                if (list.contains(c.ability().name)) {
                    for (String id : list) {
                        if (!c.ability().name.equals(id) && AbilityHelper.isActivated(id, c.player())) {
                            return false;
                        }
                    }
                }
            } else {
                for (String id : list) {
                    if (!c.ability().name.equals(id) && AbilityHelper.isActivated(id, c.player())) {
                        return false;
                    }
                }
            }
            return true;
        }
        return AbilityHelper.isActivated(GsonHelper.getAsString(c.jsonObject(), "ability", c.ability().name), c.player());
    }));
    public static final Condition HAS_LEVEL = register("has_level", new Condition((c) -> {
        IHUPlayer hu = HUPlayer.getCap(c.player());
        if (hu != null) {
            Level level = hu.getSuperpowerLevels().get(c.jsonObject().has("superpower") ? new ResourceLocation(GsonHelper.getAsString(c.jsonObject(), "superpower")) : HUPackSuperpowers.getSuperpower(c.player()));
            return level.getLevel() >= GsonHelper.getAsInt(c.jsonObject(), "level");
        }
        return false;
    }));

    public static final Condition HAS_ITEM = register("has_item", new Condition((c) -> {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(c.jsonObject(), "item")));
        boolean b = false;
        if (c.jsonObject().has("slots")) {
            JsonArray array = GsonHelper.getAsJsonArray(c.jsonObject(), "slots");
            for (int i = 0; i < array.size(); i++) {
                if (c.player().getItemBySlot(EquipmentSlot.byName(array.get(i).getAsString().toLowerCase())).getItem() == item) {
                    b = true;
                    break;
                }
            }
        } else {
            if (c.player().getItemBySlot(EquipmentSlot.byName(GsonHelper.getAsString(c.jsonObject(), "slot", EquipmentSlot.MAINHAND.getName()).toLowerCase())).getItem() == item) {
                b = true;
            }
        }
        return b;
    }));

    public static final Condition ABILITY_ENABLED = register("ability_enabled", new Condition((c) -> {
        if (c.jsonObject().has("abilities")) {
            List<String> list = HUJsonUtils.getStringsFromArray(c.jsonObject().getAsJsonArray("abilities"));

            if (c.jsonObject().has("filter")) {
                if (list.contains(c.ability().name)) {
                    for (String id : list) {
                        Ability a = AbilityHelper.getActiveAbilityMap(c.player()).get(id);
                        if (a != null && !c.ability().name.equals(a.name) && a.getEnabled()) {
                            return false;
                        }
                    }
                }
            } else {
                for (String id : list) {
                    Ability a = AbilityHelper.getActiveAbilityMap(c.player()).get(id);
                    if (a != null && !c.ability().name.equals(a.name) && a.getEnabled()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return AbilityHelper.getActiveAbilityMap(c.player()).getOrDefault(GsonHelper.getAsString(c.jsonObject(), "ability"), c.ability()).getEnabled();
    }));

    public static final Condition HAS_SUIT = register("has_suit", new Condition((c) -> {
        Suit suit = Suit.getSuit(c.player());
        String suitName = GsonHelper.getAsString(c.jsonObject(), "suit", "");
        if (!StringUtil.isNullOrEmpty(suitName) && suit != null) {
            return suit.getRegistryName().toString().equals(suitName);
        }
        return suit != null;
    }));

    public static final Condition IS_IN_FLUID = register("is_in_fluid", new Condition((c) -> {
        for (FluidState state : Fluid.FLUID_STATE_REGISTRY) {
            for (TagKey<Fluid> tag : state.getTags().collect(Collectors.toList())) {
                if (tag.location().getPath().equals(GsonHelper.getAsString(c.jsonObject(), "fluid"))) {
                    return c.player().isEyeInFluid(tag);
                }
            }
        }
        return false;
    }));

    public static final Condition OR = register("or", new Condition((c) -> {
        JsonArray array = c.jsonObject().getAsJsonArray("values");
        for (JsonElement jsonElement : array) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Condition condition = ConditionManager.getFromJson(jsonObject);
            if (condition != null && condition.apply(c.player(), jsonObject, c.ability())) {
                return true;
            }
        }
        return false;
    }));

    public static final Condition AND = register("and", new Condition((c) -> {
        JsonArray array = c.jsonObject().getAsJsonArray("values");
        for (JsonElement jsonElement : array) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Condition condition = ConditionManager.getFromJson(jsonObject);
            if (condition != null && !condition.apply(c.player(), jsonObject, c.ability())) {
                return false;
            }
        }
        return true;
    }));

    public static final Condition ABILITY_NAME = register("ability_name", new Condition((c) -> c.ability().name.equals(c.jsonObject().get("ability").getAsString())));
    public static final Condition IS_SPRINTING = register("is_sprinting", new Condition((c) -> c.player().isSprinting()));
    public static final Condition IS_SNEAKING = register("is_sneaking", new Condition((c) -> c.player().isCrouching()));

    private static Condition register(String name, Condition condition) {
        CONDITIONS.register(name, () -> condition);
        return condition;
    }


    public record ConditionVariables(Player player, JsonObject jsonObject, Ability ability) {
    }
}

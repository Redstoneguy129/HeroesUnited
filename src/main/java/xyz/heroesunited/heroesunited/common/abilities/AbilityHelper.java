package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AbilityHelper {

    public static boolean getEnabled(String name, PlayerEntity player) {
        return HUAbilityCap.getCap(player).getActiveAbilities().containsKey(name);
    }

    public static <T extends Ability> T getAnotherAbilityFromMap(PlayerEntity player, T ability) {
        for (Ability newAbility : getAbilityMap(player).values()) {
            if (newAbility.type.equals(ability.type) && newAbility.name.equals(ability.name)) {
                return (T) newAbility;
            }
        }
        return ability;
    }

    public static void disable(PlayerEntity player) {
        player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> ImmutableMap.copyOf(a.getActiveAbilities()).forEach((id, ability) -> {
            a.disable(id);
            ability.onDeactivated(player);
        }));
    }

    public static boolean canActiveAbility(Ability ability, PlayerEntity player) {
        boolean suit = Suit.getSuit(player) == null || Suit.getSuit(player).canCombineWithAbility(ability, player);
        return ability.canActivate(player) && suit;
    }

    public static List<Ability> getAbilities(Entity entity) {
        List<Ability> list = new ArrayList<>();
        entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((f) -> list.addAll(f.getActiveAbilities().values()));
        return list;
    }

    public static Map<String, Ability> getActiveAbilityMap(Entity entity) {
        Map<String, Ability> map = Maps.newHashMap();
        entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((f) -> map.putAll(f.getActiveAbilities()));
        return map;
    }

    public static Map<String, Ability> getAbilityMap(Entity entity) {
        Map<String, Ability> map = Maps.newHashMap();
        entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((f) -> map.putAll(f.getAbilities()));
        return map;
    }

    public static void addTheme(Identifier theme) {
        if (!AbilitiesScreen.themes.contains(theme)) {
            AbilitiesScreen.themes.add(theme);
        }
    }

    public static void setAttribute(LivingEntity entity, EntityAttribute attribute, UUID uuid, double amount, EntityAttributeModifier.Operation operation) {
        setAttribute(entity, "hudefault", attribute, uuid, amount, operation);
    }

    //For remove modifier set amount to 0
    public static void setAttribute(LivingEntity entity, String name, EntityAttribute attribute, UUID uuid, double amount, EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);

        if (instance == null || entity.world.isClient) {
            return;
        }

        EntityAttributeModifier modifier = instance.getModifier(uuid);

        if (amount == 0 || modifier != null && (modifier.getValue() != amount || modifier.getOperation() != operation)) {
            instance.removeModifier(uuid);
            return;
        }

        modifier = instance.getModifier(uuid);

        if (modifier == null) {
            modifier = new EntityAttributeModifier(uuid, name, amount, operation);
            instance.addTemporaryModifier(modifier);
        }
    }

    public static List<AbilityCreator> parseAbilityCreators(JsonObject json, Identifier resourceLocation) {
        List<AbilityCreator> abilityList = Lists.newArrayList();
        if (json.has("abilities")) {
            JsonObject abilities = JsonHelper.getObject(json, "abilities");
            abilities.entrySet().forEach((e) -> {
                if (e.getValue() instanceof JsonObject) {
                    JsonObject o = (JsonObject) e.getValue();
                    AbilityType ability = AbilityType.ABILITIES.getValue(new Identifier(JsonHelper.getString(o, "ability")));
                    if (ability != null) {
                        abilityList.add(new AbilityCreator(e.getKey(), ability).setJsonObject(o));
                    } else
                        HeroesUnited.LOGGER.error("Couldn't read ability {} in {}", JsonHelper.getString(o, "ability"), resourceLocation);
                }
            });
        }
        return abilityList;
    }
}
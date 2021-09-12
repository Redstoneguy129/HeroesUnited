package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbilityHelper {

    public static boolean getEnabled(String name, PlayerEntity player) {
        return HUAbilityCap.getCap(player).getActiveAbilities().containsKey(name);
    }

    public static <T extends Ability> T getAnotherAbilityFromMap(List<Ability> abilities, T ability) {
        for (Ability newAbility : abilities) {
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

    public static void addTheme(ResourceLocation theme) {
        if (!AbilitiesScreen.themes.contains(theme)) {
            AbilitiesScreen.themes.add(theme);
        }
    }

    public static void setAttribute(LivingEntity entity, Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation) {
        setAttribute(entity, "hudefault", attribute, uuid, amount, operation);
    }

    //For remove modifier set amount to 0
    public static void setAttribute(LivingEntity entity, String name, Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation) {
        ModifiableAttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null || entity.level.isClientSide) {
            return;
        }

        AttributeModifier modifier = instance.getModifier(uuid);

        if (amount == 0 || modifier != null && (modifier.getAmount() != amount || modifier.getOperation() != operation)) {
            instance.removeModifier(uuid);
            return;
        }

        modifier = instance.getModifier(uuid);

        if (modifier == null) {
            modifier = new AttributeModifier(uuid, name, amount, operation);
            instance.addTransientModifier(modifier);
        }
    }

    public static List<AbilityCreator> parseAbilityCreators(JsonObject json, ResourceLocation resourceLocation) {
        List<AbilityCreator> abilityList = Lists.newArrayList();
        if (json.has("abilities")) {
            JsonObject abilities = JSONUtils.getAsJsonObject(json, "abilities");
            abilities.entrySet().forEach((e) -> {
                if (e.getValue() instanceof JsonObject) {
                    JsonObject o = (JsonObject) e.getValue();
                    AbilityType ability = AbilityType.ABILITIES.get().getValue(new ResourceLocation(JSONUtils.getAsString(o, "ability")));
                    if (ability != null) {
                        abilityList.add(new AbilityCreator(e.getKey(), ability).setJsonObject(o));
                    } else
                        HeroesUnited.LOGGER.error("Couldn't read ability {} in {}", JSONUtils.getAsString(o, "ability"), resourceLocation);
                }
            });
        }
        return abilityList;
    }
}
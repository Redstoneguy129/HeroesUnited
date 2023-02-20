package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.hupacks.HUPackPowers;

import java.util.*;
import java.util.stream.Collectors;

public class AbilityHelper {

    public static boolean isActivated(String name, Entity entity) {
        return getActiveAbilityMap(entity).containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Ability> T getSameAbilityFrom(Collection<Ability> abilities, T ability) {
        for (Ability newAbility : abilities) {
            if (newAbility.type.equals(ability.type) && newAbility.name.equals(ability.name)) {
                return (T) newAbility;
            }
        }
        return ability;
    }

    public static <T> List<T> getListOfType(Class<T> type, Collection<?> list) {
        return list.stream().filter(x -> type.isAssignableFrom(x.getClass())).map(type::cast).collect(Collectors.toList());
    }

    public static List<Ability> getAbilities(Entity entity) {
        return new ArrayList<>(getActiveAbilityMap(entity).values());
    }

    public static Map<String, Ability> getActiveAbilityMap(Entity entity) {
        Map<String, Ability> map = Maps.newLinkedHashMap();
        entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((f) -> map.putAll(f.getActiveAbilities()));
        return map;
    }

    public static Map<String, Ability> getAbilityMap(Entity entity) {
        Map<String, Ability> map = Maps.newLinkedHashMap();
        entity.getCapability(HUAbilityCap.CAPABILITY).ifPresent((f) -> {
            map.putAll(f.getAbilities());
            map.putAll(f.getActiveAbilities());
        });
        return map;
    }

    //For remove modifier set amount to 0
    public static void setAttribute(LivingEntity entity, String name, Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);

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

    public static List<AbilityCreator> parseAbilityCreators(JsonObject jsonObject, ResourceLocation resourceLocation) {
        List<AbilityCreator> abilityList = new ArrayList<>();
        if (jsonObject.has("abilities")) {
            abilityList.addAll(parsePowers(GsonHelper.getAsJsonObject(jsonObject, "abilities"), resourceLocation));
        }
        if (jsonObject.has("powers")) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "powers");
            for (int i = 0; i < jsonArray.size(); i++) {
                abilityList.addAll(HUPackPowers.getPower(new ResourceLocation(jsonArray.get(i).getAsString())));
            }
        }
        if (jsonObject.has("power")) {
            abilityList.addAll(HUPackPowers.getPower(new ResourceLocation(GsonHelper.getAsString(jsonObject, "power"))));
        }

        if (jsonObject.has("common")) {
            for (AbilityCreator creator : abilityList) {
                creator.mergeCommonStuff(jsonObject.getAsJsonObject("common"));
            }
        }
        return abilityList;
    }

    public static List<AbilityCreator> parsePowers(JsonObject abilities, ResourceLocation resourceLocation) {
        List<AbilityCreator> abilityList = new ArrayList<>();
        abilities.entrySet().forEach((e) -> {
            if (e.getValue() instanceof JsonObject jsonObject) {
                AbilityType ability = AbilityType.REGISTRY.get().getValue(new ResourceLocation(GsonHelper.getAsString(jsonObject, "ability")));
                if (ability != null) {
                    abilityList.add(new AbilityCreator(e.getKey(), ability, jsonObject));
                } else {
                    HeroesUnited.LOGGER.error("Couldn't read ability with id {} in {}", GsonHelper.getAsString(jsonObject, "ability"), resourceLocation);
                }
            }
        });
        return abilityList;
    }
}
package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbilityHelper {

    public static boolean getEnabled(String name, PlayerEntity player) {
        return HUPlayer.getCap(player).getActiveAbilities().containsKey(name);
    }

    public static void disable(PlayerEntity player) {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            ImmutableMap.copyOf(a.getActiveAbilities()).forEach((id, ability) -> {
                a.disable(id);
                ability.onDeactivated(player);
            });
        });
    }

    public static boolean canActiveAbility(Ability ability, PlayerEntity player) {
        boolean suit = Suit.getSuit(player) == null || Suit.getSuit(player) != null && Suit.getSuit(player).canCombineWithAbility(ability, player);
        return ability.canActivate(player) && suit;
    }

    public static List<Ability> getAbilities(Entity entity) {
        List<Ability> list = new ArrayList<>();
        entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((f) -> list.addAll(f.getActiveAbilities().values()));
        return list;
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

        if (instance == null || entity.world.isRemote) {
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
            instance.applyNonPersistentModifier(modifier);
        }
    }

    public static List<AbilityCreator> parseAbilityCreators(JsonObject json, ResourceLocation resourceLocation) {
        List<AbilityCreator> abilityList = Lists.newArrayList();
        if (JSONUtils.hasField(json, "abilities")) {
            JsonObject abilities = JSONUtils.getJsonObject(json, "abilities");
            abilities.entrySet().forEach((e) -> {
                if (e.getValue() instanceof JsonObject) {
                    JsonObject o = (JsonObject) e.getValue();
                    AbilityType ability = AbilityType.ABILITIES.getValue(new ResourceLocation(JSONUtils.getString(o, "ability")));
                    if (ability != null) {
                        abilityList.add(new AbilityCreator(e.getKey(), ability).setJsonObject(o));
                    } else
                        HeroesUnited.LOGGER.error("Couldn't read ability {} in {}", JSONUtils.getString(o, "ability"), resourceLocation);
                }
            });
        }
        return abilityList;
    }
}
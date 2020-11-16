package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.client.gui.AbilitiesScreen;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbilityHelper {

    public static boolean getEnabled(AbilityType type, PlayerEntity player) {
        return getAbilities(player).contains(type);
    }

    public static void disable(PlayerEntity player) {
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
            for (AbilityType type : ImmutableList.copyOf(a.getActiveAbilities())) {
                a.disable(type);
            }
        });
    }

    public static boolean canActiveAbility(AbilityType type, PlayerEntity player) {
        boolean a = type.create().canActivate(player);
        boolean b = Suit.getSuit(player) == null || Suit.getSuit(player) != null && Suit.getSuit(player).canCombineWithAbility(type, player);
        return a && b;
    }

    public static List<AbilityType> getAbilities(PlayerEntity player) {
        List<AbilityType> list = new ArrayList<>();
        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((f) -> list.addAll(f.getActiveAbilities()));
        return list;
    }

    public static void addTheme(ResourceLocation theme) {
        if (!AbilitiesScreen.themes.contains(theme)) {
            AbilitiesScreen.themes.add(theme);
        }
    }

    public static void removeTheme(ResourceLocation theme) {
        if (AbilitiesScreen.themes.contains(theme)) {
            AbilitiesScreen.themes.remove(theme);
        }
    }

    public static void setAttribute(LivingEntity entity, Attribute attribute, double value, AttributeModifier.Operation operation, UUID uuid) {
        setAttribute(entity, "hudefault", attribute, uuid, value, operation);
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
        }

        modifier = instance.getModifier(uuid);

        if (modifier == null) {
            modifier = new AttributeModifier(uuid, name, amount, operation);
            instance.applyNonPersistentModifier(modifier);
        }
    }
}
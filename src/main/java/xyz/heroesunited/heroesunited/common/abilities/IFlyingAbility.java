package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;

public interface IFlyingAbility {

    boolean isFlying(PlayerEntity player);

    default boolean renderFlying(PlayerEntity player) {
        return true;
    }

    default boolean rotateArms(PlayerEntity player) {
        return false;
    }

    default boolean setDefaultRotationAngles(HUSetRotationAnglesEvent event) {
        return true;
    }

    default float getDegreesForWalk(PlayerEntity player) {
        return 22.5F;
    }

    default float getDegreesForSprint(PlayerEntity player) {
        return 90F + player.xRot;
    }

    default SoundEvent getSoundEvent() {
        return HUSounds.FLYING;
    }

    static IFlyingAbility getFlyingAbility(PlayerEntity player) {
        return AbilityHelper.getListOfType(AbilityHelper.getAbilities(player), IFlyingAbility.class).stream().findFirst().orElse(null);
    }
}

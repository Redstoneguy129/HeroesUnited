package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;

public interface IFlyingAbility {

    boolean isFlying(PlayerEntity player);

    default boolean renderFlying(PlayerEntity player) {
        return true;
    }

    default boolean rotateArms(PlayerEntity player) {
        return false;
    }

    default boolean setDefaultRotationAngles(PlayerEntity player) {
        return true;
    }

    default SoundEvent getSoundEvent() {
        return HUSounds.FLYING;
    }

    static IFlyingAbility getFlyingAbility(PlayerEntity player) {
        for (Ability ability : AbilityHelper.getAbilities(player)) {
            if (ability instanceof IFlyingAbility) {
                return (IFlyingAbility) ability;
            }
        }
        return null;
    }
}

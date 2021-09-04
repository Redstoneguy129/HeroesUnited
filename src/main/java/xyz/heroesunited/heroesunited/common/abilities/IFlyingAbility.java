package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;

public interface IFlyingAbility {

    boolean isFlying(Player player);

    default boolean renderFlying(Player player) {
        return true;
    }

    default boolean rotateArms(Player player) {
        return false;
    }

    default boolean setDefaultRotationAngles(Player player) {
        return true;
    }

    default SoundEvent getSoundEvent() {
        return HUSounds.FLYING;
    }

    static IFlyingAbility getFlyingAbility(Player player) {
        for (Ability ability : AbilityHelper.getAbilities(player)) {
            if (ability instanceof IFlyingAbility) {
                return (IFlyingAbility) ability;
            }
        }
        return null;
    }
}

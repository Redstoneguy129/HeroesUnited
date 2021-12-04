package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;

public interface IFlyingAbility {

    boolean isFlying(Player player);

    default boolean renderFlying(Player player) {
        return true;
    }

    default boolean rotateArms(Player player) {
        return false;
    }

    default boolean setDefaultRotationAngles(SetupAnimEvent event) {
        return true;
    }

    default float getDegreesForWalk(Player player) {
        return 22.5F;
    }

    default float getDegreesForSprint(Player player) {
        return 90F + player.getXRot();
    }

    default SoundEvent getSoundEvent() {
        return HUSounds.FLYING;
    }

    static IFlyingAbility getFlyingAbility(Player player) {
        return AbilityHelper.getListOfType(AbilityHelper.getAbilities(player), IFlyingAbility.class).stream().findFirst().orElse(null);
    }
}

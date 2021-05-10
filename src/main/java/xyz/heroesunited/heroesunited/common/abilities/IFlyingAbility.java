package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public interface IFlyingAbility {

    boolean isFlying(PlayerEntity player);

    boolean renderFlying(PlayerEntity player);

    boolean rotateArms(PlayerEntity player);

    boolean setDefaultRotationAngles(PlayerEntity player);

    SoundEvent getSoundEvent();

    @Nullable
    static IFlyingAbility getFlyingAbility(PlayerEntity player) {
        for (Ability type : AbilityHelper.getAbilities(player)) {
            if (type != null && type instanceof IFlyingAbility) {
                return (IFlyingAbility) type;
            }
        }
        return null;
    }
}

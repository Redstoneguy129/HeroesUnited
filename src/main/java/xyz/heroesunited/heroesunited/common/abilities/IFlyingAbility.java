package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public interface IFlyingAbility {

    boolean renderFlying(PlayerEntity player);

    boolean rotateArms();

    SoundEvent getSoundEvent();

    @Nullable
    static IFlyingAbility getFlyingAbility(PlayerEntity player) {
        for (AbilityType type : AbilityHelper.getAbilities(player)) {
            if (type != null && type.create() instanceof IFlyingAbility) {
                return ((IFlyingAbility)type.create());
            }
        }
        return null;
    }
}

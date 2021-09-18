package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;

public class FlightAbility extends JSONAbility implements IFlyingAbility {

    public FlightAbility(AbilityType type) {
        super(type);
    }

    @Override
    public boolean isFlying(PlayerEntity player) {
        return getEnabled();
    }

    @Override
    public boolean renderFlying(PlayerEntity player) {
        return getJsonObject() != null && JSONUtils.getAsBoolean(this.getJsonObject(), "render", true);
    }

    @Override
    public boolean rotateArms(PlayerEntity player) {
        return getJsonObject() != null && JSONUtils.getAsBoolean(this.getJsonObject(), "rotateArms", false);
    }

    @Override
    public boolean setDefaultRotationAngles(PlayerEntity player) {
        return getJsonObject() == null || JSONUtils.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true);
    }
}

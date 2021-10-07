package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;

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
    public boolean setDefaultRotationAngles(HUSetRotationAnglesEvent event) {
        return getJsonObject() == null || JSONUtils.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true);
    }

    @Override
    public float getDegreesForSprint(PlayerEntity player) {
        return getJsonObject() != null ? JSONUtils.getAsFloat(this.getJsonObject(), "degrees_for_sprint", IFlyingAbility.super.getDegreesForSprint(player)) : IFlyingAbility.super.getDegreesForSprint(player);
    }

    @Override
    public float getDegreesForWalk(PlayerEntity player) {
        return getJsonObject() != null ? JSONUtils.getAsFloat(this.getJsonObject(), "degrees_for_walk", IFlyingAbility.super.getDegreesForWalk(player)) : IFlyingAbility.super.getDegreesForWalk(player);
    }
}

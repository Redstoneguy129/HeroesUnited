package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;

public class FlightAbility extends JSONAbility implements IFlyingAbility {

    public FlightAbility(AbilityType type) {
        super(type);
    }

    @Override
    public boolean isFlying(Player player) {
        return getEnabled();
    }

    @Override
    public boolean renderFlying(Player player) {
        return getJsonObject() != null && GsonHelper.getAsBoolean(this.getJsonObject(), "render", true);
    }

    @Override
    public boolean rotateArms(Player player) {
        return getJsonObject() != null && GsonHelper.getAsBoolean(this.getJsonObject(), "rotateArms", false);
    }

    @Override
    public boolean setDefaultRotationAngles(SetupAnimEvent event) {
        return getJsonObject() == null || GsonHelper.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true);
    }

    @Override
    public float getDegreesForSprint(Player player) {
        return getJsonObject() != null ? GsonHelper.getAsFloat(this.getJsonObject(), "degrees_for_sprint", IFlyingAbility.super.getDegreesForSprint(player)) : IFlyingAbility.super.getDegreesForSprint(player);
    }

    @Override
    public float getDegreesForWalk(Player player) {
        return getJsonObject() != null ? GsonHelper.getAsFloat(this.getJsonObject(), "degrees_for_walk", IFlyingAbility.super.getDegreesForWalk(player)) : IFlyingAbility.super.getDegreesForWalk(player);
    }
}

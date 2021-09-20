package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;

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
    public boolean setDefaultRotationAngles(Player player) {
        return getJsonObject() == null || GsonHelper.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true);
    }
}

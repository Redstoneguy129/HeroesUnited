package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;

public class FlightAbility extends JSONAbility implements IFlyingAbility {

    public FlightAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public boolean isFlying(Player player) {
        return this.getEnabled();
    }

    @Override
    public boolean renderFlying(Player player) {
        return GsonHelper.getAsBoolean(this.getJsonObject(), "render", true);
    }

    @Override
    public boolean rotateArms(Player player) {
        return GsonHelper.getAsBoolean(this.getJsonObject(), "rotateArms", false);
    }

    @Override
    public boolean setDefaultRotationAngles(SetupAnimEvent event) {
        return GsonHelper.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true);
    }

    @Override
    public float getDegreesForSprint(Player player) {
        return GsonHelper.getAsFloat(this.getJsonObject(), "degrees_for_sprint", IFlyingAbility.super.getDegreesForSprint(player));
    }

    @Override
    public float getDegreesForWalk(Player player) {
        return GsonHelper.getAsFloat(this.getJsonObject(), "degrees_for_walk", IFlyingAbility.super.getDegreesForWalk(player));
    }
}

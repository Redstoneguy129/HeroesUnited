package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SoundEvent;
import xyz.heroesunited.heroesunited.util.hudata.HUData;

public class FlightAbility extends JSONAbility implements IFlyingAbility {
    public static final HUData<Boolean> FLIGHT = new HUData("flight");

    public FlightAbility() {
        super(AbilityType.FLIGHT);
    }

    @Override
    public void action(PlayerEntity player) {
        this.dataManager.set(player, FLIGHT, enabled);
    }

    @Override
    public void registerData() {
        this.dataManager.register(FLIGHT, false);
    }

    @Override
    public boolean isFlying(PlayerEntity player) {
        return this.dataManager.getEntry(FLIGHT).getValue();
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
        return getJsonObject() != null ? JSONUtils.getAsBoolean(this.getJsonObject(), "setDefaultRotationAngles", true) : true;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return null;
    }
}

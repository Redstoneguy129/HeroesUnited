package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SoundEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;

public class FlightAbility extends Ability implements IFlyingAbility {
    public FlightAbility() {
        super(AbilityType.FLIGHT);
    }

    @Override
    public void onUpdate(PlayerEntity player) {
        if (!JSONUtils.hasField(this.getJsonObject(), "key")) {
            HUPlayer.getCap(player).setFlying(true);
        }
    }

    @Override
    public void toggle(PlayerEntity player, int id, int action) {
        if (JSONUtils.hasField(this.getJsonObject(), "key") && id == JSONUtils.getInt(this.getJsonObject(), "key")) {
            HUPlayer.getCap(player).setFlying(!HUPlayer.getCap(player).isFlying());
        }
    }

    @Override
    public void onDeactivated(PlayerEntity player) {
        HUPlayer.getCap(player).setFlying(false);
    }

    @Override
    public boolean renderFlying(PlayerEntity player) {
        return JSONUtils.getBoolean(this.getJsonObject(), "render", true);
    }

    @Override
    public boolean rotateArms() {
        return JSONUtils.getBoolean(this.getJsonObject(), "rotateArms", false);
    }

    @Override
    public SoundEvent getSoundEvent() {
        return null;
    }
}

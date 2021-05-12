package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class ParachuteAbility extends JSONAbility {
    public ParachuteAbility() {
        super(AbilityType.PARACHUTE);
    }

    @Override
    public void action(PlayerEntity player) {
        if(player.isOnGround()){
            enabled = false;
        }
        if (usingParachute(player)) {
            Vector3d vec = player.getDeltaMovement();
            vec = vec.multiply(0.99F, 0.85F, 0.99F);
            player.setDeltaMovement(vec.x, vec.y, vec.z);
        }
    }

    public boolean usingParachute(PlayerEntity player) {
        return player.getDeltaMovement().y < 0F && !player.abilities.flying && !player.isOnGround() && !player.isInWater() && !player.isFallFlying() && enabled;
    }
}

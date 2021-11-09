package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class GlidingAbility extends JSONAbility {

    public GlidingAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void onUpdate(PlayerEntity playerEntity) {
        super.onUpdate(playerEntity);
        if (canGliding(playerEntity)) {
            Vector3d vec = playerEntity.getDeltaMovement();
            Vector3d vec1 = playerEntity.getLookAngle();
            float f = playerEntity.xRot * ((float) Math.PI / 180F);
            double d1 = Math.sqrt(vec1.x * vec1.x + vec1.z * vec1.z);
            double d3 = Math.sqrt(Entity.getHorizontalDistanceSqr(vec));
            float f1 = MathHelper.cos(f);
            f1 = (float) ((double) f1 * (double) f1 * Math.min(1.0D, vec1.length() / 0.4D));
            vec = playerEntity.getDeltaMovement().add(0.0D, -0.07 * (-1.0D + (double) f1 * 0.75D), 0.0D);
            if (vec.y < 0.0D && d1 > 0.0D) {
                double d5 = vec.y * -0.1D * (double) f1;
                vec = vec.add(vec1.x * d5 / d1, d5, vec1.z * d5 / d1);
            }

            if (f < 0.0F && d1 > 0.0D) {
                double d9 = d3 * (double) (-MathHelper.sin(f)) * 0.04D;
                vec = vec.add(-vec1.x * d9 / d1, d9 * 3.2D, -vec1.z * d9 / d1);
            }

            if (d1 > 0.0D) {
                vec = vec.add((vec1.x / d1 * d3 - vec.x) * 0.1D, 0.0D, (vec1.z / d1 * d3 - vec.z) * 0.1D);
            }

            vec = vec.multiply(0.99F, 0.85F, 0.99F);
            playerEntity.setDeltaMovement(vec.x, vec.y, vec.z);
        }
    }

    public boolean canGliding(PlayerEntity playerEntity) {
        return playerEntity.getDeltaMovement().y < 0F && !playerEntity.abilities.flying
                && !playerEntity.isOnGround() && !playerEntity.isInWater() && !playerEntity.isFallFlying() && getEnabled();
    }

    public static GlidingAbility getInstance(PlayerEntity playerEntity) {
        for (Ability ability : AbilityHelper.getAbilities(playerEntity)) {
            if (ability instanceof GlidingAbility) {
                return (GlidingAbility) ability;
            }
        }
        return null;
    }
}


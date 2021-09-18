package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class ProjectileAbility extends JSONAbility {

    public ProjectileAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void action(PlayerEntity player) {
        if (!player.level.isClientSide && getEnabled()) {
            CompoundNBT compound = new CompoundNBT();
            ServerWorld world = (ServerWorld) player.level;
            try {
                JsonObject jsonObject = JSONUtils.getAsJsonObject(getJsonObject(), "nbt", null);
                if (jsonObject != null) {
                    compound = JsonToNBT.parseTag(jsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            compound.putString("id", JSONUtils.getAsString(getJsonObject(), "entity", "minecraft:snowball"));
            EntityType.loadEntityRecursive(compound, world, (entity) -> {
                entity.moveTo(player.getX(), (player.getY() + player.getEyeHeight()) - 0.25D, player.getZ(), entity.yRot, entity.xRot);
                float velocity = JSONUtils.getAsFloat(getJsonObject(), "velocity", 1.5F);
                float inaccuracy = JSONUtils.getAsFloat(getJsonObject(), "inaccuracy", 0F);
                shoot(entity, player, velocity, inaccuracy);

                if (entity instanceof ProjectileEntity) {
                    ((ProjectileEntity) entity).setOwner(player);
                }
                return !world.addWithUUID(entity) ? null : entity;
            });
        }
    }

    /**
     * Code from {@link ProjectileEntity#shootFromRotation}
     */
    private void shoot(Entity e, Entity player, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(player.yRot * ((float)Math.PI / 180F)) * MathHelper.cos(player.xRot * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((player.xRot) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(player.yRot * ((float)Math.PI / 180F)) * MathHelper.cos(player.xRot * ((float)Math.PI / 180F));
        Vector3d vec3d = (new Vector3d(f, f1, f2)).normalize().add(e.level.getRandom().nextGaussian() * (double)0.0075F * (double)inaccuracy, e.level.getRandom().nextGaussian() * (double)0.0075F * (double)inaccuracy, e.level.getRandom().nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);
        
        e.setDeltaMovement(vec3d);
        e.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        e.xRot = (float)(MathHelper.atan2(vec3d.y, MathHelper.sqrt(Entity.getHorizontalDistanceSqr(vec3d))) * (double)(180F / (float)Math.PI));
        e.yRotO = e.yRot;
        e.xRotO = e.xRot;

        Vector3d vector3d = player.getDeltaMovement();
        e.setDeltaMovement(e.getDeltaMovement().add(vector3d.x, player.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.server.ServerWorld;

public class ProjectileAbility extends JSONAbility {

    public ProjectileAbility() {
        super(AbilityType.PROJECTILE);
    }

    @Override
    public void action(PlayerEntity player) {
        if (!player.level.isClientSide && enabled) {
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
                if (entity instanceof ProjectileEntity) {
                    ProjectileEntity projectile = (ProjectileEntity) entity;
                    projectile.moveTo(player.getX(), (player.getY() + player.getEyeHeight()) - 0.25D, player.getZ(), projectile.yRot, projectile.xRot);
                    float velocity = JSONUtils.getAsFloat(getJsonObject(), "velocity", 1.5F);
                    float inaccuracy = JSONUtils.getAsFloat(getJsonObject(), "inaccuracy", 0F);
                    projectile.shootFromRotation(player, player.xRot, player.yRot, 0, velocity, inaccuracy);
                    projectile.setOwner(player);
                    return !world.addWithUUID(projectile) ? null : projectile;
                }
                return null;
            });
        }
    }
}

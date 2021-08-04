package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;

public class ProjectileAbility extends JSONAbility {

    public ProjectileAbility() {
        super(AbilityType.PROJECTILE);
    }

    @Override
    public void action(PlayerEntity player) {
        if (!player.world.isClient && getEnabled()) {
            NbtCompound compound = new NbtCompound();
            ServerWorld world = (ServerWorld) player.world;
            try {
                JsonObject jsonObject = JsonHelper.getObject(getJsonObject(), "nbt", null);
                if (jsonObject != null) {
                    compound = StringNbtReader.parse(jsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            compound.putString("id", JsonHelper.getString(getJsonObject(), "entity", "minecraft:snowball"));
            EntityType.loadEntityWithPassengers(compound, world, (entity) -> {
                if (entity instanceof ProjectileEntity) {
                    ProjectileEntity projectile = (ProjectileEntity) entity;
                    projectile.refreshPositionAndAngles(player.getX(), (player.getY() + player.getStandingEyeHeight()) - 0.25D, player.getZ(), projectile.yaw, projectile.pitch);
                    float velocity = JsonHelper.getFloat(getJsonObject(), "velocity", 1.5F);
                    float inaccuracy = JsonHelper.getFloat(getJsonObject(), "inaccuracy", 0F);
                    projectile.setProperties(player, player.pitch, player.yaw, 0, velocity, inaccuracy);
                    projectile.setOwner(player);
                    return !world.tryLoadEntity(projectile) ? null : projectile;
                }
                return null;
            });
        }
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class ProjectileAbility extends JSONAbility {

    public ProjectileAbility() {
        super(AbilityType.PROJECTILE);
    }

    @Override
    public void action(Player player) {
        if (!player.level.isClientSide && getEnabled()) {
            CompoundTag compound = new CompoundTag();
            ServerLevel world = (ServerLevel) player.level;
            try {
                JsonObject jsonObject = GsonHelper.getAsJsonObject(getJsonObject(), "nbt", null);
                if (jsonObject != null) {
                    compound = TagParser.parseTag(jsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            compound.putString("id", GsonHelper.getAsString(getJsonObject(), "entity", "minecraft:snowball"));
            EntityType.loadEntityRecursive(compound, world, (entity) -> {
                entity.moveTo(player.getX(), (player.getY() + player.getEyeHeight()) - 0.25D, player.getZ(), entity.getYRot(), entity.getXRot());
                float velocity = GsonHelper.getAsFloat(getJsonObject(), "velocity", 1.5F);
                float inaccuracy = GsonHelper.getAsFloat(getJsonObject(), "inaccuracy", 0F);
                shoot(entity, player, velocity, inaccuracy);

                if (entity instanceof Projectile) {
                    ((Projectile) entity).setOwner(player);
                }
                return !world.addWithUUID(entity) ? null : entity;
            });
        }
    }

    /**
     * Code from {@link Projectile#shootFromRotation}
     */
    private void shoot(Entity e, Entity player, float velocity, float inaccuracy) {
        float f = -Mth.sin(player.getYRot() * (Mth.PI / 180F)) * Mth.cos(player.getXRot() * (Mth.PI / 180F));
        float f1 = -Mth.sin((player.getXRot()) * (Mth.PI / 180F));
        float f2 = Mth.cos(player.getYRot() * (Mth.PI / 180F)) * Mth.cos(player.getXRot() * (Mth.PI / 180F));
        Vec3 vec3d = (new Vec3(f, f1, f2)).normalize().add(e.level.getRandom().nextGaussian() * (double)0.0075F * (double)inaccuracy, e.level.getRandom().nextGaussian() * (double)0.0075F * (double)inaccuracy, e.level.getRandom().nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);
        
        e.setDeltaMovement(vec3d);
        e.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * (double)(180F / Mth.PI)));
        e.setXRot((float)(Mth.atan2(vec3d.y, vec3d.horizontalDistance()) * (double)(180F / Mth.PI)));
        e.yRotO = e.getYRot();
        e.xRotO = e.getXRot();

        Vec3 vector3d = player.getDeltaMovement();
        e.setDeltaMovement(e.getDeltaMovement().add(vector3d.x, player.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }
}

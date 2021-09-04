package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

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
                if (entity instanceof Projectile) {
                    Projectile projectile = (Projectile) entity;
                    projectile.moveTo(player.getX(), (player.getY() + player.getEyeHeight()) - 0.25D, player.getZ(), projectile.getYRot(), projectile.getXRot());
                    float velocity = GsonHelper.getAsFloat(getJsonObject(), "velocity", 1.5F);
                    float inaccuracy = GsonHelper.getAsFloat(getJsonObject(), "inaccuracy", 0F);
                    projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, velocity, inaccuracy);
                    projectile.setOwner(player);
                    return !world.addWithUUID(projectile) ? null : projectile;
                }
                return null;
            });
        }
    }
}

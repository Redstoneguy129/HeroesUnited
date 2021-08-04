package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import xyz.heroesunited.heroesunited.HeroesUnited;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spaceship extends Entity {

    public Spaceship(EntityType<?> type, World p_i48580_2_) {
        super(type, p_i48580_2_);
    }

    @Override
    public ActionResult interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
        if (p_184230_1_.shouldCancelInteraction()) {
            return ActionResult.PASS;
        } else if (!this.world.isClient) {
            return p_184230_1_.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            return ActionResult.SUCCESS;
        }
    }

    public boolean isRocket() {
        return getType() == HUEntities.SPACESHIP;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerWorld world, net.minecraftforge.common.util.ITeleporter teleporter) {
        ArrayList<Entity> entities = new ArrayList<>();
        entities.addAll(getPassengerList());
        Spaceship entity = (Spaceship) super.moveToWorld(world, teleporter);
        for (Entity entity2 : entities) {
            entity2.moveToWorld(world, teleporter).startRiding(entity);
        }
        return entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getPrimaryPassenger() != null && this.getPrimaryPassenger() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) this.getPrimaryPassenger();
            if (isRocket()) {
                if (world.getRegistryKey() == HeroesUnited.SPACE) {
                    this.setRotation(playerEntity.getYaw(), playerEntity.getPitch());
                    Vec3d vector3d = getRotationVector().multiply(playerEntity.forwardSpeed, playerEntity.forwardSpeed, playerEntity.forwardSpeed);
                    setVelocity(vector3d.x, vector3d.y, vector3d.z);
                } else {
                    if (playerEntity.forwardSpeed > 0) {
                        setVelocity(0, getVelocity().z + playerEntity.forwardSpeed * 10, 0);
                    } else {
                        if (getVelocity().y > -0.8) {
                            setVelocity(getVelocity().x, getVelocity().y - 0.1, getVelocity().z);
                        } else {
                            setVelocity(getVelocity().x, getVelocity().y, getVelocity().z);
                        }
                    }
                }
            } else {
                this.setRotation(playerEntity.getYaw(), playerEntity.getPitch());
                Vec3d vector3d = getRotationVector().multiply(playerEntity.forwardSpeed * 10, playerEntity.forwardSpeed * 10, playerEntity.forwardSpeed * 10);
                setVelocity(vector3d.x, vector3d.y, vector3d.z);
            }
            this.move(MovementType.SELF, this.getVelocity());
        }
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        List<Entity> list = this.getPassengerList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean collidesWith(Entity p_241849_1_) {
        return canVehicleCollide(this, p_241849_1_);
    }

    public static boolean canVehicleCollide(Entity p_242378_0_, Entity p_242378_1_) {
        return (p_242378_1_.isCollidable() || p_242378_1_.isPushable()) && !p_242378_0_.isConnectedThroughVehicle(p_242378_1_);
    }

    @Override
    public double getMountedHeightOffset() {
        return getHeight() / getType().getHeight() * 8;
    }

    @Override
    public void pushAwayFrom(Entity p_70108_1_) {
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound p_70037_1_) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound p_213281_1_) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
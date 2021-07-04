package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.heroesunited.heroesunited.HeroesUnited;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spaceship extends Entity {

    public Spaceship(EntityType<?> type, World p_i48580_2_) {
        super(type, p_i48580_2_);
    }

    @Override
    public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
        if (p_184230_1_.isSecondaryUseActive()) {
            return ActionResultType.PASS;
        } else if (!this.level.isClientSide) {
            return p_184230_1_.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    public boolean isRocket() {
        return getType() == HUEntities.SPACESHIP;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerWorld world, net.minecraftforge.common.util.ITeleporter teleporter) {
        ArrayList<Entity> entities = new ArrayList<>();
        entities.addAll(getPassengers());
        Spaceship entity = (Spaceship) super.changeDimension(world, teleporter);
        for (Entity entity2 : entities) {
            entity2.changeDimension(world, teleporter).startRiding(entity);
        }
        return entity;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) this.getControllingPassenger();
            if (isRocket()) {
                if (level.dimension() == HeroesUnited.SPACE) {
                    this.setRot(playerEntity.yRot, playerEntity.xRot);
                    Vector3d vector3d = getLookAngle().multiply(playerEntity.zza, playerEntity.zza, playerEntity.zza);
                    setDeltaMovement(vector3d.x, vector3d.y, vector3d.z);
                } else {
                    if (playerEntity.zza > 0) {
                        setDeltaMovement(0, getDeltaMovement().z + playerEntity.zza * 10, 0);
                    } else {
                        if (getDeltaMovement().y > -0.8) {
                            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y - 0.1, getDeltaMovement().z);
                        } else {
                            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z);
                        }
                    }
                }
            } else {
                this.setRot(playerEntity.yRot, playerEntity.xRot);
                Vector3d vector3d = getLookAngle().multiply(playerEntity.zza * 10, playerEntity.zza * 10, playerEntity.zza * 10);
                setDeltaMovement(vector3d.x, vector3d.y, vector3d.z);
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean canCollideWith(Entity p_241849_1_) {
        return canVehicleCollide(this, p_241849_1_);
    }

    public static boolean canVehicleCollide(Entity p_242378_0_, Entity p_242378_1_) {
        return (p_242378_1_.canBeCollidedWith() || p_242378_1_.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(p_242378_1_);
    }

    @Override
    public double getPassengersRidingOffset() {
        return getBbHeight() / getType().getHeight() * 8;
    }

    @Override
    public void push(Entity p_70108_1_) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
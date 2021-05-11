package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class Spaceship extends Entity {

    public Spaceship(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
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

    @Override
    public void tick() {
        super.tick();
        if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            setDeltaMovement(((PlayerEntity)this.getControllingPassenger()).xxa*10, ((PlayerEntity)this.getControllingPassenger()).yya != 0.0F? 5 : 0, ((PlayerEntity)this.getControllingPassenger()).zza*10);
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
        return 8D;
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
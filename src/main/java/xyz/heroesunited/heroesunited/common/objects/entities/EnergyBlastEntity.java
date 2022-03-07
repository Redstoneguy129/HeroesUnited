package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;

public class EnergyBlastEntity extends ThrowableProjectile {

    private float damage;
    private Color color;
    private float gravity;
    private int lifetime;

    public EnergyBlastEntity(EntityType<EnergyBlastEntity> type, Level worldIn) {
        super(type, worldIn);
        this.damage = 4;
        this.color = Color.RED;
        this.gravity = 0;
        this.lifetime = 60;
    }

    public EnergyBlastEntity(Level world, LivingEntity entity, float damage, Color color, int lifetime) {
        super(HUEntities.ENERGY_BLAST.get(), entity, world);
        this.damage = damage;
        this.color = color;
        this.lifetime = lifetime;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && tickCount > lifetime) {
            this.removeAfterChangingDimensions();
        }
        super.tick();
    }

    @Override
    protected void onHit(HitResult result) {
        if (!isAlive())
            return;

        if (result.getType() == HitResult.Type.ENTITY) {
            if (((EntityHitResult) result).getEntity() == getOwner()) return;
            ((EntityHitResult) result).getEntity().hurt(DamageSource.thrown(this, getOwner()), damage);
        }

        if (!this.level.isClientSide) this.removeAfterChangingDimensions();
    }

    @Override
    protected float getGravity() {
        return gravity;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", damage);
        compound.putFloat("Gravity", gravity);
        compound.putInt("Lifetime", lifetime);
        ListTag listNBT = new ListTag();
        listNBT.add(IntTag.valueOf(this.color.getRed()));
        listNBT.add(IntTag.valueOf(this.color.getGreen()));
        listNBT.add(IntTag.valueOf(this.color.getBlue()));
        compound.put("Color", listNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Damage")) {
            this.damage = compound.getFloat("Damage");
        }
        if (compound.contains("Gravity")) {
            this.gravity = compound.getFloat("Gravity");
        }
        if (compound.contains("Lifetime")) {
            this.lifetime = compound.getInt("Lifetime");
        }
        if (compound.contains("Color")) {
            ListTag listNBT = compound.getList("Color", Tag.TAG_INT);
            this.color = new Color(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2));
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
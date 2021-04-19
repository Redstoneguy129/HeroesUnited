package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;

public class EnergyBlastEntity extends ThrowableEntity {

    private float damage;
    private Color color;
    private float gravity;
    private int lifetime;

    public EnergyBlastEntity(EntityType<EnergyBlastEntity> type, World worldIn) {
        super(type, worldIn);
        this.damage = 4;
        this.color = Color.RED;
        this.gravity = 0;
        this.lifetime = 60;
    }

    public EnergyBlastEntity(World world, LivingEntity entity, float damage, Color color, int lifetime) {
        super(HUEntities.ENERGY_BLAST, entity, world);
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
    protected void onHit(RayTraceResult result) {
        if (result == null || !isAlive())
            return;

        if (result.getType() == RayTraceResult.Type.ENTITY) {
            if (((EntityRayTraceResult) result).getEntity() == getOwner()) return;
            ((EntityRayTraceResult) result).getEntity().hurt(DamageSource.thrown(this, getOwner()), damage);
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
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", damage);
        compound.putFloat("Gravity", gravity);
        compound.putInt("Lifetime", lifetime);
        ListNBT listNBT = new ListNBT();
        listNBT.add(IntNBT.valueOf(this.color.getRed()));
        listNBT.add(IntNBT.valueOf(this.color.getGreen()));
        listNBT.add(IntNBT.valueOf(this.color.getBlue()));
        compound.put("Color", listNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
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
            ListNBT listNBT = compound.getList("Color", Constants.NBT.TAG_INT);
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
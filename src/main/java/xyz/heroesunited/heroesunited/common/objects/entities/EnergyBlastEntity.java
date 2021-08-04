package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.awt.*;

public class EnergyBlastEntity extends ThrownEntity {

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
        if (!this.world.isClient && age > lifetime) {
            this.removeFromDimension();
        }
        super.tick();
    }

    @Override
    protected void onCollision(HitResult result) {
        if (result == null || !isAlive())
            return;

        if (result.getType() == HitResult.Type.ENTITY) {
            if (((EntityHitResult) result).getEntity() == getOwner()) return;
            ((EntityHitResult) result).getEntity().damage(DamageSource.thrownProjectile(this, getOwner()), damage);
        }

        if (!this.world.isClient) this.removeFromDimension();
    }

    @Override
    protected float getGravity() {
        return gravity;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.putFloat("Damage", damage);
        compound.putFloat("Gravity", gravity);
        compound.putInt("Lifetime", lifetime);
        NbtList listNBT = new NbtList();
        listNBT.add(NbtInt.of(this.color.getRed()));
        listNBT.add(NbtInt.of(this.color.getGreen()));
        listNBT.add(NbtInt.of(this.color.getBlue()));
        compound.put("Color", listNBT);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
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
            NbtList listNBT = compound.getList("Color", Constants.NBT.TAG_INT);
            this.color = new Color(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2));
        }
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public boolean isTouchingWater() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Nonnull
    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
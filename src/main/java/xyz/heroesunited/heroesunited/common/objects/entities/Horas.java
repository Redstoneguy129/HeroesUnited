package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.gui.HorasScreen;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class Horas extends CreatureEntity implements IFlyingAnimal {
    private float flap, flapSpeed, flapping = 1.0F;
    private PlayerEntity playerEntity;
    private final PathNavigator navigator;
    private int timeToRecalcPath;

    public Horas(EntityType<? extends Horas> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlyingMovementController(this, 10, false);
        this.navigator = this.getNavigation();
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        if (reason == SpawnReason.MOB_SUMMONED || reason == SpawnReason.SPAWN_EGG) this.playerEntity = worldIn.getNearestPlayer(this, 10D);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResultType interactAt(PlayerEntity player, Vector3d vec, Hand hand) {
        if (!player.level.isClientSide) return ActionResultType.PASS;
        Minecraft.getInstance().forceSetScreen(new HorasScreen(this));
        return ActionResultType.SUCCESS;
    }

    public static AttributeModifierMap.MutableAttribute createMobAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 8D).add(Attributes.MOVEMENT_SPEED, .23F).add(Attributes.FLYING_SPEED, .23F);
    }

    @Override
    public void baseTick() {
        if (this.playerEntity != null && !this.playerEntity.isSpectator()) {
            List<PlayerEntity> playerEntities = this.getCommandSenderWorld().getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(10D));
            if (!playerEntities.contains(this.playerEntity)) {
                if (this.playerEntity != null && !this.isLeashed()) {
                    this.lookControl.setLookAt(this.playerEntity, 10.0F, (float) this.getHeadRotSpeed());
                    if (--this.timeToRecalcPath <= 0) {
                        this.timeToRecalcPath = 10;
                        double d0 = this.getX() - this.playerEntity.getX();
                        double d1 = this.getY() - this.playerEntity.getY();
                        double d2 = this.getZ() - this.playerEntity.getZ();
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        if (!(d3 <= (2D * 2D))) {
                            this.navigator.moveTo(this.playerEntity, 1.23D);
                        } else {
                            this.navigator.stop();
                            LookController lookcontroller = this.lookControl;
                            if (d3 <= 5D || lookcontroller.getWantedX() == this.getX() && lookcontroller.getWantedY() == this.getY() && lookcontroller.getWantedZ() == this.getZ()) {
                                double d4 = this.playerEntity.getX() - this.getX();
                                double d5 = this.playerEntity.getZ() - this.getZ();
                                this.navigator.moveTo(this.getX() - d4, this.getY(), this.getZ() - d5, 1.23D);
                            }

                        }
                    }
                }
            }
        }
        super.baseTick();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
        this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }
        this.flapping = (float) ((double) this.flapping * 0.9D);
        Vector3d vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapping * 2.0F;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("owner")) {
            UUID uuid = compound.getUUID("owner");
            this.playerEntity = this.level.getPlayerByUUID(uuid);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (this.playerEntity != null)
            compound.putUUID("owner", this.playerEntity.getUUID());
    }
}
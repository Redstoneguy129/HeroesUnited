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
public class Horas extends CreatureEntity implements IFlyingAnimal, HUEntity {
    private float flap, flapSpeed;
    private float flapping = 1.0F;
    private PlayerEntity playerEntity;
    private final PathNavigator navigator;
    private int timeToRecalcPath;

    public Horas(EntityType<? extends Horas> entityType, World world) {
        super(entityType, world);
        this.moveController = new FlyingMovementController(this, 10, false);
        this.navigator = this.getNavigator();
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        if (reason == SpawnReason.MOB_SUMMONED || reason == SpawnReason.SPAWN_EGG) this.playerEntity = worldIn.getClosestPlayer(this, 10D);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        if (!player.world.isRemote) return ActionResultType.PASS;
        Minecraft.getInstance().displayGuiScreen(new HorasScreen(this));
        return ActionResultType.SUCCESS;
    }

    public static AttributeModifierMap.MutableAttribute func_234225_eI_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 8D).createMutableAttribute(Attributes.MOVEMENT_SPEED, .23F).createMutableAttribute(Attributes.FLYING_SPEED, .23F);
    }

    @Override
    public void livingTick() {
        if (this.playerEntity != null && !this.playerEntity.isSpectator()) {
            List<PlayerEntity> playerEntities = this.getEntityWorld().getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().grow(10D));
            if (!playerEntities.contains(this.playerEntity)) {
                if (this.playerEntity != null && !this.getLeashed()) {
                    this.getLookController().setLookPositionWithEntity(this.playerEntity, 10.0F, (float) this.getVerticalFaceSpeed());
                    if (--this.timeToRecalcPath <= 0) {
                        this.timeToRecalcPath = 10;
                        double d0 = this.getPosX() - this.playerEntity.getPosX();
                        double d1 = this.getPosY() - this.playerEntity.getPosY();
                        double d2 = this.getPosZ() - this.playerEntity.getPosZ();
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        if (!(d3 <= (2D * 2D))) {
                            this.navigator.tryMoveToEntityLiving(this.playerEntity, 1.23D);
                        } else {
                            this.navigator.clearPath();
                            LookController lookcontroller = this.getLookController();
                            if (d3 <= 5D || lookcontroller.getLookPosX() == this.getPosX() && lookcontroller.getLookPosY() == this.getPosY() && lookcontroller.getLookPosZ() == this.getPosZ()) {
                                double d4 = this.playerEntity.getPosX() - this.getPosX();
                                double d5 = this.playerEntity.getPosZ() - this.getPosZ();
                                this.navigator.tryMoveToXYZ(this.getPosX() - d4, this.getPosY(), this.getPosZ() - d5, 1.23D);
                            }

                        }
                    }
                }
            }
        }
        super.livingTick();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
        this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }
        this.flapping = (float) ((double) this.flapping * 0.9D);
        Vector3d vector3d = this.getMotion();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setMotion(vector3d.mul(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapping * 2.0F;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("owner")) {
            UUID uuid = compound.getUniqueId("owner");
            this.playerEntity = this.world.getPlayerByUuid(uuid);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.playerEntity != null)
            compound.putUniqueId("owner", this.playerEntity.getUniqueID());
    }
}
package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.common.abilities.SizeChangeAbility;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    public MixinPlayerEntity(EntityType<? extends PlayerEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "updatePose()V", at = @At(value = "HEAD"), cancellable = true)
    protected void updatePose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object)this;
        if(player.getForcedPose() != null) {
            this.setPose(player.getForcedPose());
            return;
        }
        if (this.isPoseClear(Pose.SWIMMING)) {
            Pose pose;
            if (this.isElytraFlying()) {
                pose = Pose.FALL_FLYING;
            } else if (this.isSleeping()) {
                pose = Pose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = Pose.SWIMMING;
            } else if (this.isSpinAttacking()) {
                pose = Pose.SPIN_ATTACK;
            } else if (this.isSneaking() && !player.abilities.isFlying) {
                pose = Pose.CROUCHING;
            } else {
                pose = Pose.STANDING;
            }

            Pose pose1;
            if (!this.isSpectator() && !this.isPassenger() && !this.isPoseClear(pose)) {
                if (this.isPoseClear(Pose.CROUCHING)) {
                    pose1 = Pose.CROUCHING;
                } else {
                    if (!SizeChangeAbility.isSmall(player)) {
                        pose1 = Pose.SWIMMING;
                    } else {
                        pose1 = Pose.CROUCHING;
                    }
                }
            } else {
                pose1 = pose;
            }

            this.setPose(pose1);
            ci.cancel();
        }
    }
}

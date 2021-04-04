package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.SizeChangeAbility;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    public MixinPlayerEntity(EntityType<? extends PlayerEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    private void onGetDimensions(Pose pose, CallbackInfoReturnable<EntitySize> info) {
        for (Ability ability : AbilityHelper.getAbilities(this)) {
            if (ability instanceof SizeChangeAbility) {
                float size = ((SizeChangeAbility) ability).getSize();
                info.setReturnValue(info.getReturnValue().scale(size, size));
            }
        }
    }

    @Inject(method = "updatePlayerPose()V", at = @At(value = "HEAD"), cancellable = true)
    protected void updatePlayerPose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object)this;
        if(player.getForcedPose() != null) {
            this.setPose(player.getForcedPose());
            return;
        }
        if (this.canEnterPose(Pose.SWIMMING)) {
            Pose pose;
            if (this.isFallFlying()) {
                pose = Pose.FALL_FLYING;
            } else if (this.isSleeping()) {
                pose = Pose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = Pose.SWIMMING;
            } else if (this.isAutoSpinAttack()) {
                pose = Pose.SPIN_ATTACK;
            } else if (this.isShiftKeyDown() && !player.abilities.flying) {
                pose = Pose.CROUCHING;
            } else {
                pose = Pose.STANDING;
            }

            Pose pose1;
            if (!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(pose)) {
                if (this.canEnterPose(Pose.CROUCHING)) {
                    pose1 = Pose.CROUCHING;
                } else {
                    if (!SizeChangeAbility.isSmall(player)) {
                        pose1 = Pose.SWIMMING;
                    } else {
                        pose1 = pose;
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

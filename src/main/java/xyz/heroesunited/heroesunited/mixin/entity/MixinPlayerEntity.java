package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.HUBoundingBoxEvent;
import xyz.heroesunited.heroesunited.common.abilities.SizeChangeAbility;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.FALL_RESISTANCE;
import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    public MixinPlayerEntity(EntityType<? extends PlayerEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(at = @At("RETURN"), method = "createPlayerAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", cancellable = true)
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue().add(FALL_RESISTANCE, 0).add(JUMP_BOOST, 0));
    }

    @Inject(at = @At("RETURN"), method = "getDimensions", cancellable = true)
    private void onGetDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> info) {
        HUBoundingBoxEvent event = new HUBoundingBoxEvent((PlayerEntity) (Object) this, info.getReturnValue());
        MinecraftForge.EVENT_BUS.post(event);
        info.setReturnValue(event.getNewSize());
    }

    @Inject(method = "updatePose()V", at = @At(value = "HEAD"), cancellable = true)
    protected void updatePlayerPose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object)this;

        if (this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
            EntityPose entityPose6;
            if (this.isFallFlying()) {
                entityPose6 = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entityPose6 = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                entityPose6 = EntityPose.SWIMMING;
            } else if (this.isUsingRiptide()) {
                entityPose6 = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !player.getAbilities().flying) {
                entityPose6 = EntityPose.CROUCHING;
            } else {
                entityPose6 = EntityPose.STANDING;
            }

            EntityPose entityPose9;
            if (!this.isSpectator() && !this.hasVehicle() && !this.wouldPoseNotCollide(entityPose6)) {
                if (this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
                    entityPose9 = EntityPose.CROUCHING;
                } else {
                    if (!SizeChangeAbility.isSmall(player)) {
                        entityPose9 = EntityPose.SWIMMING;
                    } else {
                        entityPose9 = entityPose6;
                    }
                }
            } else {
                entityPose9 = entityPose6;
            }

            this.setPose(entityPose9);
            ci.cancel();
        }
    }
}

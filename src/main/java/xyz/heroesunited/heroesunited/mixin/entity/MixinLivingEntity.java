package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.HUEyeHeightEvent;
import xyz.heroesunited.heroesunited.common.events.HUCancelSprinting;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "setSprinting(Z)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetSprint(boolean sprinting, CallbackInfo callbackInfoReturnable) {
        HUCancelSprinting event = new HUCancelSprinting(this);
        MinecraftForge.EVENT_BUS.post(event);
        if (sprinting && event.isCanceled()) {
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    private void onGetEyeHeight(EntityPose pose, EntityDimensions size, CallbackInfoReturnable<Float> info) {
        if (pose != EntityPose.SLEEPING) {
            HUEyeHeightEvent event = new HUEyeHeightEvent((LivingEntity) (Object) this, info.getReturnValueF());
            MinecraftForge.EVENT_BUS.post(event);
            info.setReturnValue(event.getNewEyeHeight());
        }
    }

}

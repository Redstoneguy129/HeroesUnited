package xyz.heroesunited.heroesunited.mixin.entity;

import net.minecraft.entity.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.SizeChangeAbility;
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
        if (sprinting == true && event.isCanceled()) {
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    private void onGetEyeHeight(Pose pose, EntitySize size, CallbackInfoReturnable<Float> info) {
        if (pose != Pose.SLEEPING) {
            for (Ability ability : AbilityHelper.getAbilities(this)) {
                if (getType() == EntityType.PLAYER && ability instanceof SizeChangeAbility) {
                    float height = ((SizeChangeAbility) ability).getSize();
                    if (height != 1.0F) {
                        info.setReturnValue(info.getReturnValueF() * height);
                    }
                }
            }
        }
    }

}

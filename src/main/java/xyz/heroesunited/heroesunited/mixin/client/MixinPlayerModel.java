package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;

/**
 * This is for triggering the {@link xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent}.
 */
@Mixin(PlayerModel.class)
public class MixinPlayerModel {

    @Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V")
    private void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {
        if (entityIn == null || !(entityIn instanceof PlayerEntity)) return;
        MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent((PlayerEntity) entityIn, (PlayerModel) (Object) this, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch));
    }
}

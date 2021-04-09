package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLivingEvent;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lcom/mojang/blaze3d/matrix/MatrixStack;popPose()V"))
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new HURenderLivingEvent(entity, (LivingRenderer) (Object) this, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn));
    }
}

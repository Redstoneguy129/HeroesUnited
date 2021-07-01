package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(LivingRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    private T entity;
    private IRenderTypeBuffer renderTypeBuffer;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void captureThings(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, CallbackInfo ci, boolean shouldSit, float f, float f1, float f2, float f6, float f7, float f8, float f5) {
        this.entity = entity;
        this.renderTypeBuffer = renderTypeBuffer;
        this.limbSwing = f5;
        this.limbSwingAmount = f8;
        this.ageInTicks = f7;
        this.netHeadYaw = f2;
        this.headPitch = f6;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"))
    public void renderModel(M model, MatrixStack matrixStack, IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
        if (entity instanceof AbstractClientPlayerEntity && model instanceof PlayerModel) {
            HUClientUtil.renderModel((PlayerRenderer) (Object) this, (PlayerModel) model, (AbstractClientPlayerEntity) entity, matrixStack, renderTypeBuffer, builder, light, overlay, red, green, blue, alpha, limbSwing, limbSwingAmount, ageInTicks, headPitch, netHeadYaw);
            return;
        }
        model.renderToBuffer(matrixStack, builder, light, overlay, red, green, blue, alpha);
    }
}

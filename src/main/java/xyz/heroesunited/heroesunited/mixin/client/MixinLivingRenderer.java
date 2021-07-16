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
    private float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void captureThings(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, CallbackInfo ci, boolean shouldSit, float f, float f1, float netHeadYaw, float headPitch, float ageInTicks, float limbSwingAmount, float limbSwing) {
        this.entity = entity;
        this.renderTypeBuffer = renderTypeBuffer;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"))
    public void renderModel(M model, MatrixStack matrixStack, IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
        if (entity instanceof AbstractClientPlayerEntity && model instanceof PlayerModel) {
            HUClientUtil.renderModel((PlayerRenderer) (Object) this, (PlayerModel) model, (AbstractClientPlayerEntity) entity, matrixStack, renderTypeBuffer, builder, light, overlay, red, green, blue, alpha, limbSwing, limbSwingAmount, ageInTicks, headPitch, netHeadYaw);
        } else {
            model.renderToBuffer(matrixStack, builder, light, overlay, red, green, blue, alpha);
        }
    }
}

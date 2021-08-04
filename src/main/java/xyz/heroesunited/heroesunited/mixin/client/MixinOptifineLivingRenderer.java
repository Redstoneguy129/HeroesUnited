package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinOptifineLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    private T entity;
    private VertexConsumerProvider renderTypeBuffer;
    private float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void captureThings(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer, int light, CallbackInfo ci, float f, float f1, float netHeadYaw, float headPitch, float ageInTicks, float limbSwingAmount, float limbSwing) {
        this.entity = entity;
        this.renderTypeBuffer = renderTypeBuffer;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void renderModel(M model, MatrixStack matrixStack, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha) {
        if (entity instanceof AbstractClientPlayerEntity && model instanceof PlayerEntityModel) {
            HUClientUtil.renderModel((PlayerEntityRenderer) (Object) this, (PlayerEntityModel) model, (AbstractClientPlayerEntity) entity, matrixStack, renderTypeBuffer, builder, light, overlay, red, green, blue, alpha, limbSwing, limbSwingAmount, ageInTicks, headPitch, netHeadYaw);
        } else {
            model.render(matrixStack, builder, light, overlay, red, green, blue, alpha);
        }
    }
}

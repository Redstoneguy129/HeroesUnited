package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    private T entity;
    private MultiBufferSource renderTypeBuffer;
    private float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch;

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void captureThings(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, CallbackInfo ci, boolean shouldSit, float f, float f1, float netHeadYaw, float headPitch, float ageInTicks, float limbSwingAmount, float limbSwing) {
        this.entity = entity;
        this.renderTypeBuffer = renderTypeBuffer;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    public void renderModel(M model, PoseStack matrixStack, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha) {
        if (entity instanceof AbstractClientPlayer && model instanceof PlayerModel) {
            HUClientUtil.renderModel((PlayerRenderer) (Object) this, (PlayerModel) model, (AbstractClientPlayer) entity, matrixStack, renderTypeBuffer, builder, light, overlay, red, green, blue, alpha, limbSwing, limbSwingAmount, ageInTicks, headPitch, netHeadYaw);
        } else {
            model.renderToBuffer(matrixStack, builder, light, overlay, red, green, blue, alpha);
        }
    }
}

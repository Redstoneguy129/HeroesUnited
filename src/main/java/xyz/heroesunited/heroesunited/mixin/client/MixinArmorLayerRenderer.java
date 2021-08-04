package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorLayerRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    public MixinArmorLayerRenderer(FeatureRendererContext<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void onPreRender(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HURenderLayerEvent.Armor.Pre event = new HURenderLayerEvent.Armor.Pre(((LivingEntityRenderer<LivingEntity, net.minecraft.client.render.entity.model.EntityModel<LivingEntity>>) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"), cancellable = true)
    public void onPostRender(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HURenderLayerEvent.Armor.Post event = new HURenderLayerEvent.Armor.Post(((LivingEntityRenderer<LivingEntity, net.minecraft.client.render.entity.model.EntityModel<LivingEntity>>) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "setPartVisibility(Lnet/minecraft/client/renderer/entity/model/BipedModel;Lnet/minecraft/inventory/EquipmentSlotType;)V", at = @At("RETURN"))
    public void onSetArmorVisibility(A modelIn, EquipmentSlot slotIn, CallbackInfo ci) {
        HURenderLayerEvent.Armor.HUSetArmorPartVisibility event = new HURenderLayerEvent.Armor.HUSetArmorPartVisibility(modelIn, slotIn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Redirect(method = "renderArmorPiece(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/inventory/EquipmentSlotType;ILnet/minecraft/client/renderer/entity/model/BipedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V"))
    public void renderArmor(ArmorFeatureRenderer bipedArmorLayer, MatrixStack matrix, VertexConsumerProvider buffer, int packedlightIn, boolean withGlint, A model, float red, float green, float blue, Identifier armorResource) {
        VertexConsumer ivertexbuilder = ItemRenderer.getArmorGlintConsumer(buffer, RenderLayer.getEntityTranslucent(armorResource), false, withGlint);
        model.render(matrix, ivertexbuilder, packedlightIn, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }
}
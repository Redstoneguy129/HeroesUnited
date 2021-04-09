package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;

@Mixin(BipedArmorLayer.class)
public abstract class MixinArmorLayerRenderer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {

    public MixinArmorLayerRenderer(IEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void onPreRender(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HURenderLayerEvent.Armor.Pre event = new HURenderLayerEvent.Armor.Pre(((LivingRenderer<LivingEntity, net.minecraft.client.renderer.entity.model.EntityModel<LivingEntity>>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"), cancellable = true)
    public void onPostRender(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HURenderLayerEvent.Armor.Post event = new HURenderLayerEvent.Armor.Post(((LivingRenderer<LivingEntity, net.minecraft.client.renderer.entity.model.EntityModel<LivingEntity>>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "setPartVisibility(Lnet/minecraft/client/renderer/entity/model/BipedModel;Lnet/minecraft/inventory/EquipmentSlotType;)V", at = @At("RETURN"))
    public void onSetArmorVisibility(A modelIn, EquipmentSlotType slotIn, CallbackInfo ci) {
        HURenderLayerEvent.Armor.HUSetArmorPartVisibility event = new HURenderLayerEvent.Armor.HUSetArmorPartVisibility(modelIn, slotIn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Redirect(method = "renderArmorPiece(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/inventory/EquipmentSlotType;ILnet/minecraft/client/renderer/entity/model/BipedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;renderModel(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V"))
    public void renderArmor(BipedArmorLayer bipedArmorLayer, MatrixStack matrix, IRenderTypeBuffer buffer, int packedlightIn, boolean withGlint, A model, float red, float green, float blue, ResourceLocation armorResource) {
        IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityTranslucent(armorResource), false, withGlint);
        model.renderToBuffer(matrix, ivertexbuilder, packedlightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }
}
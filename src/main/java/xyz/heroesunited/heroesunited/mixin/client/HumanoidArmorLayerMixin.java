package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.RenderLayerEvent;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void onPreRender(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        RenderLayerEvent.Armor.Pre event = new RenderLayerEvent.Armor.Pre(((LivingEntityRenderer<LivingEntity, net.minecraft.client.model.EntityModel<LivingEntity>>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"), cancellable = true)
    public void onPostRender(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        RenderLayerEvent.Armor.Post event = new RenderLayerEvent.Armor.Post(((LivingEntityRenderer<LivingEntity, net.minecraft.client.model.EntityModel<LivingEntity>>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V", at = @At("RETURN"))
    public void onSetArmorVisibility(A modelIn, EquipmentSlot slotIn, CallbackInfo ci) {
        RenderLayerEvent.Armor.ArmorVisibility event = new RenderLayerEvent.Armor.ArmorVisibility(modelIn, slotIn);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
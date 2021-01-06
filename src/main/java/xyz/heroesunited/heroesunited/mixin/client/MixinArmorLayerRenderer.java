package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.util.IRendererArmorLayer;

@Mixin(BipedArmorLayer.class)
public abstract class MixinArmorLayerRenderer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> implements IRendererArmorLayer {

    public MixinArmorLayerRenderer(IEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void onPreRender(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HURenderLayerEvent.Armor.Pre event = new HURenderLayerEvent.Armor.Pre(((LivingRenderer<LivingEntity, net.minecraft.client.renderer.entity.model.EntityModel<LivingEntity>>) Minecraft.getInstance().getRenderManager().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"), cancellable = true)
    public void onPostRender(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HURenderLayerEvent.Armor.Post event = new HURenderLayerEvent.Armor.Post(((LivingRenderer<LivingEntity, net.minecraft.client.renderer.entity.model.EntityModel<LivingEntity>>) Minecraft.getInstance().getRenderManager().getRenderer(entitylivingbaseIn)), entitylivingbaseIn, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;setModelSlotVisible(Lnet/minecraft/client/renderer/entity/model/BipedModel;Lnet/minecraft/inventory/EquipmentSlotType;)V", at = @At("RETURN"))
    public void onSetArmorVisibility(A modelIn, EquipmentSlotType slotIn, CallbackInfo ci) {
        HURenderLayerEvent.Armor.HUSetArmorPartVisibility event = new HURenderLayerEvent.Armor.HUSetArmorPartVisibility(modelIn, slotIn);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;func_241739_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/inventory/EquipmentSlotType;ILnet/minecraft/client/renderer/entity/model/BipedModel;)V", at = @At("HEAD"), cancellable = true)
    public void renderArmorPart(MatrixStack p_241739_1_, IRenderTypeBuffer p_241739_2_, T p_241739_3_, EquipmentSlotType p_241739_4_, int p_241739_5_, A p_241739_6_, CallbackInfo ci) {
        BipedArmorLayer layer = (BipedArmorLayer) (Object) this;
        ItemStack itemstack = p_241739_3_.getItemStackFromSlot(p_241739_4_);
        if (itemstack.getItem() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.getItem();
            if (armoritem.getEquipmentSlot() == p_241739_4_) {
                p_241739_6_ = ForgeHooksClient.getArmorModel(p_241739_3_, itemstack, p_241739_4_, p_241739_6_);
                this.getEntityModel().setModelAttributes(p_241739_6_);
                this.setModelSlotVisible(p_241739_6_, p_241739_4_);
                boolean flag1 = itemstack.hasEffect();
                if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) {
                    int i = ((net.minecraft.item.IDyeableArmorItem)armoritem).getColor(itemstack);
                    float f = (float)(i >> 16 & 255) / 255.0F;
                    float f1 = (float)(i >> 8 & 255) / 255.0F;
                    float f2 = (float)(i & 255) / 255.0F;
                    this.renderArmor(p_241739_1_, p_241739_2_, p_241739_5_, flag1, p_241739_6_, f, f1, f2, layer.getArmorResource(p_241739_3_, itemstack, p_241739_4_, null));
                    this.renderArmor(p_241739_1_, p_241739_2_, p_241739_5_, flag1, p_241739_6_, 1.0F, 1.0F, 1.0F, layer.getArmorResource(p_241739_3_, itemstack, p_241739_4_, "overlay"));
                } else {
                    this.renderArmor(p_241739_1_, p_241739_2_, p_241739_5_, flag1, p_241739_6_, 1.0F, 1.0F, 1.0F, layer.getArmorResource(p_241739_3_, itemstack, p_241739_4_, null));
                }
            }
        }
        ci.cancel();
    }
}

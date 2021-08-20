package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.render.renderer.GeckoAccessoryRenderer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class GeckoAccessory extends DefaultAccessoryItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public GeckoAccessory(EquipmentAccessoriesSlot accessorySlot) {
        super(new Properties().setISTER(() -> GeckoAccessoryRenderer::new), accessorySlot);
    }

    public GeckoAccessory(EquipmentAccessoriesSlot accessorySlot, String name) {
        super(new Properties().setISTER(() -> GeckoAccessoryRenderer::new), accessorySlot, name);
    }

    public ResourceLocation getTextureFile() {
        return new ResourceLocation(this.getRegistryName().getNamespace(), String.format("textures/accessories/%s.png", this.getRegistryName().getPath()));
    }

    public ResourceLocation getModelFile() {
        return new ResourceLocation(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath()));
    }

    public ResourceLocation getAnimationFile() {
        return new ResourceLocation(this.getRegistryName().getNamespace(), String.format("animations/%s.animation.json", this.getRegistryName().getPath()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        if (EquipmentAccessoriesSlot.getWristAccessories().contains(accessorySlot)) {
            HandSide side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? HandSide.LEFT : HandSide.RIGHT;
            ItemCameraTransforms.TransformType transformType = side == HandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
            if (stack.getItem() == HUItems.FINN_ARM || stack.getItem() == HUItems.MADNESSCLAW) {
                transformType = ItemCameraTransforms.TransformType.HEAD;
            }

            matrix.pushPose();
            renderer.getModel().translateToHand(side, matrix);
            matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrix.translate((side == HandSide.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(player, stack, transformType, side == HandSide.LEFT, matrix, bufferIn, packedLightIn);
            matrix.popPose();
        }
        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.GLOVES)) {
            for (HandSide side : HandSide.values()) {
                ItemCameraTransforms.TransformType transformType = side == HandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
                matrix.pushPose();
                renderer.getModel().translateToHand(side, matrix);
                if (this.name.equals("Gillygogs")) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrix.scale(0.625F, -0.625F, -0.625F);
                    matrix.translate(side == HandSide.LEFT ? -0.6 : -0.4, -0.35D, -0.625D);
                    ResourceLocation modelFile = new ResourceLocation(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath() + (side == HandSide.LEFT ? "" : "_v2")));
                    new GeckoAccessoryRenderer(modelFile).render(this, matrix, bufferIn, packedLightIn, this.getDefaultInstance());
                } else {
                    matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrix.translate((side == HandSide.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
                    Minecraft.getInstance().getItemInHandRenderer().renderItem(player, stack, transformType, side == HandSide.LEFT, matrix, bufferIn, packedLightIn);
                }
                matrix.popPose();
            }
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.TSHIRT) || this.accessorySlot.equals(EquipmentAccessoriesSlot.JACKET) || this.accessorySlot.equals(EquipmentAccessoriesSlot.BELT)) {
            matrix.pushPose();
            renderer.getModel().body.translateAndRotate(matrix);
            matrix.translate(0.0D, -0.25D, 0.0D);
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrix.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn);
            matrix.popPose();
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.HELMET)) {
            matrix.pushPose();
            renderer.getModel().head.translateAndRotate(matrix);
            matrix.translate(0.0D, -0.25D, 0.0D);
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrix.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn);
            matrix.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, PlayerEntity entity, EquipmentAccessoriesSlot slot) {
        return null;
    }

    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

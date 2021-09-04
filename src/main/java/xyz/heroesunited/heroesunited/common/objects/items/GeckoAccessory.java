package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.render.renderer.GeckoAccessoryRenderer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.function.Consumer;

public class GeckoAccessory extends DefaultAccessoryItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public GeckoAccessory(EquipmentAccessoriesSlot accessorySlot) {
        super(new Properties(), accessorySlot);
    }

    public GeckoAccessory(EquipmentAccessoriesSlot accessorySlot, String name) {
        super(new Properties(), accessorySlot, name);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            private final BlockEntityWithoutLevelRenderer renderer = new GeckoAccessoryRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
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
    public void render(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        if (EquipmentAccessoriesSlot.getWristAccessories().contains(accessorySlot)) {
            HumanoidArm side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            ItemTransforms.TransformType transformType = side == HumanoidArm.LEFT ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
            if (stack.getItem() == HUItems.FINN_ARM || stack.getItem() == HUItems.MADNESSCLAW) {
                transformType = ItemTransforms.TransformType.HEAD;
            }

            matrix.pushPose();
            renderer.getModel().translateToHand(side, matrix);
            matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrix.translate((side == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(player, stack, transformType, side == HumanoidArm.LEFT, matrix, bufferIn, packedLightIn);
            matrix.popPose();
        }
        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.GLOVES)) {
            for (HumanoidArm side : HumanoidArm.values()) {
                ItemTransforms.TransformType transformType = side == HumanoidArm.LEFT ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
                matrix.pushPose();
                renderer.getModel().translateToHand(side, matrix);
                if (this.name.equals("Gillygogs")) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrix.scale(0.625F, -0.625F, -0.625F);
                    matrix.translate(side == HumanoidArm.LEFT ? -0.6 : -0.4, -0.35D, -0.625D);
                    ResourceLocation modelFile = new ResourceLocation(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath() + (side == HumanoidArm.LEFT ? "" : "_v2")));
                    new GeckoAccessoryRenderer(modelFile).render(this, matrix, bufferIn, packedLightIn, this.getDefaultInstance());
                } else {
                    matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrix.translate((side == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
                    Minecraft.getInstance().getItemInHandRenderer().renderItem(player, stack, transformType, side == HumanoidArm.LEFT, matrix, bufferIn, packedLightIn);
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
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn, 0);
            matrix.popPose();
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.HELMET)) {
            matrix.pushPose();
            renderer.getModel().head.translateAndRotate(matrix);
            matrix.translate(0.0D, -0.25D, 0.0D);
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            matrix.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn, 0);
            matrix.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, Player entity, EquipmentAccessoriesSlot slot) {
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

package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.RenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.client.renderer.GeckoAccessoryRenderer;
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
    public void render(EntityRendererProvider.Context context, LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        if (stack.getItem() == HUItems.AKIRA_JACKET) {
            GeckoAccessoryRenderer accessoryRenderer = (GeckoAccessoryRenderer) RenderProperties.get(this).getItemStackRenderer();
            GeoModel model = accessoryRenderer.getGeoModelProvider().getModel(getModelFile());
            //i really need rewrite accessory render code
            IBone body = accessoryRenderer.getGeoModelProvider().getBone("armorBody");
            GeoUtils.copyRotations(renderer.getModel().body, body);
            body.setPositionX(renderer.getModel().body.x);
            body.setPositionY(-renderer.getModel().body.y);
            body.setPositionZ(renderer.getModel().body.z);

            IBone rightArm = accessoryRenderer.getGeoModelProvider().getBone("armorRightArm");
            GeoUtils.copyRotations(renderer.getModel().rightArm, rightArm);
            rightArm.setPositionX(renderer.getModel().rightArm.x + 5);
            rightArm.setPositionY(2 - renderer.getModel().rightArm.y);
            rightArm.setPositionZ(renderer.getModel().rightArm.z);

            IBone leftArm = accessoryRenderer.getGeoModelProvider().getBone("armorLeftArm");
            GeoUtils.copyRotations(renderer.getModel().leftArm, leftArm);
            leftArm.setPositionX(renderer.getModel().leftArm.x - 5);
            leftArm.setPositionY(2 - renderer.getModel().leftArm.y);
            leftArm.setPositionZ(renderer.getModel().leftArm.z);

            poseStack.pushPose();
            poseStack.translate(0.0D, 24 / 16F, 0.0D);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            accessoryRenderer.render(model, this, partialTicks, RenderType.entityTranslucent(this.getTextureFile()), poseStack, bufferIn, bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureFile())), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            poseStack.popPose();
            return;
        }

        if (stack.getItem() == HUItems.HOKAGE_CAPE) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 24 / 16F, 0.0D);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            renderHokageCape(renderer, poseStack, bufferIn, packedLightIn, livingEntity, partialTicks);
            poseStack.popPose();
            return;
        }
        if (EquipmentAccessoriesSlot.wristAccessories().contains(accessorySlot)) {
            HumanoidArm side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            ItemTransforms.TransformType transformType = side == HumanoidArm.LEFT ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
            if (stack.getItem() == HUItems.FINN_ARM || stack.getItem() == HUItems.MADNESSCLAW) {
                transformType = ItemTransforms.TransformType.HEAD;
            }

            poseStack.pushPose();
            renderer.getModel().translateToHand(side, poseStack);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.translate((side == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, stack, transformType, side == HumanoidArm.LEFT, poseStack, bufferIn, packedLightIn);
            poseStack.popPose();
        }
        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.GLOVES)) {
            for (HumanoidArm side : HumanoidArm.values()) {
                ItemTransforms.TransformType transformType = side == HumanoidArm.LEFT ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
                poseStack.pushPose();
                renderer.getModel().translateToHand(side, poseStack);
                if (stack.getItem() == HUItems.SMALLGILLY) {
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.scale(0.625F, -0.625F, -0.625F);
                    poseStack.translate(side == HumanoidArm.LEFT ? -0.6 : -0.4, -0.35D, -0.625D);
                    ResourceLocation modelFile = new ResourceLocation(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath() + (side == HumanoidArm.LEFT ? "" : "_v2")));
                    new GeckoAccessoryRenderer(modelFile).render(this, poseStack, bufferIn, packedLightIn, stack);
                } else {
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.translate((side == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
                    Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, stack, transformType, side == HumanoidArm.LEFT, poseStack, bufferIn, packedLightIn);
                }
                poseStack.popPose();
            }
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.TSHIRT) || this.accessorySlot.equals(EquipmentAccessoriesSlot.JACKET) || this.accessorySlot.equals(EquipmentAccessoriesSlot.BELT)) {
            poseStack.pushPose();
            renderer.getModel().body.translateAndRotate(poseStack);
            poseStack.translate(0.0D, -0.25D, 0.0D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, poseStack, bufferIn, 0);
            poseStack.popPose();
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.HELMET)) {
            poseStack.pushPose();
            renderer.getModel().head.translateAndRotate(poseStack);
            poseStack.translate(0.0D, -0.25D, 0.0D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, poseStack, bufferIn, 0);
            poseStack.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderHokageCape(LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, float partialTicks) {
        GeckoAccessoryRenderer accessoryRenderer = new GeckoAccessoryRenderer();
        GeoModel model = accessoryRenderer.getGeoModelProvider().getModel(getModelFile());

        IBone headBone = accessoryRenderer.getGeoModelProvider().getBone("armorHead");
        GeoUtils.copyRotations(renderer.getModel().head, headBone);
        headBone.setPositionX(renderer.getModel().head.x);
        headBone.setPositionY(-renderer.getModel().head.y);
        headBone.setPositionZ(renderer.getModel().head.z);

        IBone bodyBone = accessoryRenderer.getGeoModelProvider().getBone("armorBody");
        GeoUtils.copyRotations(renderer.getModel().body, bodyBone);
        bodyBone.setPositionX(renderer.getModel().body.x);
        bodyBone.setPositionY(-renderer.getModel().body.y);
        bodyBone.setPositionZ(renderer.getModel().body.z);

        IBone rightArmBone = accessoryRenderer.getGeoModelProvider().getBone("armorRightArm");
        GeoUtils.copyRotations(renderer.getModel().rightArm, rightArmBone);
        rightArmBone.setPositionX(renderer.getModel().rightArm.x + 5);
        rightArmBone.setPositionY(2 - renderer.getModel().rightArm.y);
        rightArmBone.setPositionZ(renderer.getModel().rightArm.z);

        IBone leftArmBone = accessoryRenderer.getGeoModelProvider().getBone("armorLeftArm");
        GeoUtils.copyRotations(renderer.getModel().leftArm, leftArmBone);
        leftArmBone.setPositionX(renderer.getModel().leftArm.x - 5);
        leftArmBone.setPositionY(2 - renderer.getModel().leftArm.y);
        leftArmBone.setPositionZ(renderer.getModel().leftArm.z);

        if (livingEntity instanceof AbstractClientPlayer player) {
            double d0 = Mth.lerp(partialTicks, player.xCloakO, player.xCloak) - Mth.lerp(partialTicks, player.xo, player.getX());
            double d1 = Mth.lerp(partialTicks, player.yCloakO, player.yCloak) - Mth.lerp(partialTicks, player.yo, player.getY());
            double d2 = Mth.lerp(partialTicks, player.zCloakO, player.zCloak) - Mth.lerp(partialTicks, player.zo, player.getZ());
            float f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
            double d3 = Mth.sin(f * ((float) Math.PI / 180F));
            double d4 = -Mth.cos(f * ((float) Math.PI / 180F));
            float f1 = Mth.clamp((float) d1 * 10.0F, -6.0F, 32.0F);
            float f2 = Mth.clamp((float) (d0 * d3 + d2 * d4) * 100.0F, 0.0F, 150.0F);

            f1 = f1 + Mth.sin(Mth.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * Mth.lerp(partialTicks, player.oBob, player.bob);

            IBone cape = accessoryRenderer.getGeoModelProvider().getBone("cape");
            float rot = Mth.clamp(f2 + f1, 0F, 80F);
            cape.setRotationX((float) Math.toRadians(-rot));
            cape.setPositionY(0);
            cape.setPositionZ(0);
            rot = 0.15F + rot / 80F;
            if (player.isCrouching()) {
                cape.setRotationX(0.5F + cape.getRotationX());
                cape.setPositionY(2.2F);
                cape.setPositionZ(-1F);
                rot += 0.15F;
            }

            IBone right = accessoryRenderer.getGeoModelProvider().getBone("right");
            right.setRotationX(rot * (float) Math.toRadians(-9.5));
            right.setRotationY(rot * (float) Math.toRadians(-59));
            right.setRotationZ(rot * (float) Math.toRadians(8.8));

            IBone left = accessoryRenderer.getGeoModelProvider().getBone("left");
            left.setRotationX(rot * (float) Math.toRadians(-9.5));
            left.setRotationY(rot * (float) Math.toRadians(59));
            left.setRotationZ(rot * (float) Math.toRadians(-8.8));
        }

        accessoryRenderer.render(model, this, partialTicks, RenderType.entityTranslucent(this.getTextureFile()), poseStack, bufferIn, bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureFile())), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, LivingEntity livingEntity, EquipmentAccessoriesSlot slot) {
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

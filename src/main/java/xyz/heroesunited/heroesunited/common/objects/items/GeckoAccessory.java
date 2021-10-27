package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.util.GeoUtils;
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
        if (stack.getItem() == HUItems.HOKAGE_CAPE) {
            matrix.pushPose();
            matrix.translate(0.0D, 24 / 16F, 0.0D);
            matrix.scale(-1.0F, -1.0F, 1.0F);
            renderHokageCape(renderer, matrix, bufferIn, packedLightIn, player, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, slot);
            matrix.scale(-1.0F, -1.0F, 1.0F);
            matrix.translate(0.0D, -24 / 16F, 0.0D);
            matrix.popPose();
            return;
        }
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
                if (stack.getItem() == HUItems.SMALLGILLY) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    matrix.scale(0.625F, -0.625F, -0.625F);
                    matrix.translate(side == HandSide.LEFT ? -0.6 : -0.4, -0.35D, -0.625D);
                    ResourceLocation modelFile = new ResourceLocation(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath() + (side == HandSide.LEFT ? "" : "_v2")));
                    new GeckoAccessoryRenderer(modelFile).render(this, matrix, bufferIn, packedLightIn, stack);
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
    public void renderHokageCape(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
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

        double d0 = MathHelper.lerp(partialTicks, player.xCloakO, player.xCloak) - MathHelper.lerp(partialTicks, player.xo, player.getX());
        double d1 = MathHelper.lerp(partialTicks, player.yCloakO, player.yCloak) - MathHelper.lerp(partialTicks, player.yo, player.getY());
        double d2 = MathHelper.lerp(partialTicks, player.zCloakO, player.zCloak) - MathHelper.lerp(partialTicks, player.zo, player.getZ());
        float f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO);
        double d3 = MathHelper.sin(f * ((float)Math.PI / 180F));
        double d4 = -MathHelper.cos(f * ((float)Math.PI / 180F));
        float f1 = MathHelper.clamp((float)d1 * 10.0F, -6.0F, 32.0F);
        float f2 = MathHelper.clamp((float)(d0 * d3 + d2 * d4) * 100.0F, 0.0F, 150.0F);

        f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * MathHelper.lerp(partialTicks, player.oBob, player.bob);

        IBone cape = accessoryRenderer.getGeoModelProvider().getBone("cape");
        float rot = MathHelper.clamp(f2 + f1, 0F, 80F);
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

        accessoryRenderer.render(model, this, 0, RenderType.entityTranslucent(this.getTextureFile()), matrix, bufferIn, bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureFile())), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
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

package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.RenderProperties;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.renderer.GeckoAccessoryRenderer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.function.Consumer;

public class JasonAccessory extends GeckoAccessory {
    public JasonAccessory(EquipmentAccessoriesSlot accessorySlot) {
        super(accessorySlot, "FalloutWolfGod");
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            private final BlockEntityWithoutLevelRenderer renderer = new GeckoAccessoryRenderer() {
                @SuppressWarnings("unchecked")
                @Override
                public void render(GeoModel model, GeckoAccessory animatable, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
                    AnimationProcessor<JasonAccessory> processor = this.getGeoModelProvider().getAnimationProcessor();

                    for (GeoBone topLevelBone : model.topLevelBones) {
                        topLevelBone.setHidden(true);
                    }

                    EquipmentAccessoriesSlot slot = JasonAccessory.this.accessorySlot;
                    if (slot == EquipmentAccessoriesSlot.TSHIRT) {
                        processor.getBone("tshirt_body").setHidden(false);
                        processor.getBone("tshirt_right_arm").setHidden(false);
                        processor.getBone("tshirt_left_arm").setHidden(false);
                    }

                    if (slot == EquipmentAccessoriesSlot.JACKET) {
                        processor.getBone("body").setHidden(false);
                        processor.getBone("right_arm").setHidden(false);
                        processor.getBone("left_arm").setHidden(false);
                    }

                    if (slot == EquipmentAccessoriesSlot.PANTS) {
                        processor.getBone("right_leg").setHidden(false);
                        processor.getBone("left_leg").setHidden(false);
                    }

                    super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }
            };

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(EntityRendererProvider.Context context, LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 24 / 16F, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        GeckoAccessoryRenderer accessoryRenderer = (GeckoAccessoryRenderer) RenderProperties.get(this).getItemStackRenderer();
        GeoModel model = accessoryRenderer.getGeoModelProvider().getModel(getModelFile());
        AnimationProcessor<JasonAccessory> processor = accessoryRenderer.getGeoModelProvider().getAnimationProcessor();

        for (IBone iBone : processor.getModelRendererList()) {
            if (iBone.getName().contains("body")) {
                GeoUtils.copyRotations(renderer.getModel().body, iBone);
                iBone.setPositionX(renderer.getModel().body.x);
                iBone.setPositionY(-renderer.getModel().body.y);
                iBone.setPositionZ(renderer.getModel().body.z);
            }
            if (iBone.getName().contains("right_arm")) {
                GeoUtils.copyRotations(renderer.getModel().rightArm, iBone);
                iBone.setPositionX(renderer.getModel().rightArm.x + 5);
                iBone.setPositionY(2 - renderer.getModel().rightArm.y);
                iBone.setPositionZ(renderer.getModel().rightArm.z);
            }
            if (iBone.getName().contains("left_arm")) {
                GeoUtils.copyRotations(renderer.getModel().leftArm, iBone);
                iBone.setPositionX(renderer.getModel().leftArm.x - 5);
                iBone.setPositionY(2 - renderer.getModel().leftArm.y);
                iBone.setPositionZ(renderer.getModel().leftArm.z);
            }
        }

        IBone rightLegBone = processor.getBone("right_leg");
        GeoUtils.copyRotations(renderer.getModel().rightLeg, rightLegBone);
        rightLegBone.setPositionX(renderer.getModel().rightLeg.x + 2);
        rightLegBone.setPositionY(12 - renderer.getModel().rightLeg.y);
        rightLegBone.setPositionZ(renderer.getModel().rightLeg.z);

        IBone leftLegBone = processor.getBone("left_leg");
        GeoUtils.copyRotations(renderer.getModel().leftLeg, leftLegBone);
        leftLegBone.setPositionX(renderer.getModel().leftLeg.x - 2);
        leftLegBone.setPositionY(12 - renderer.getModel().leftLeg.y);
        leftLegBone.setPositionZ(renderer.getModel().leftLeg.z);

        accessoryRenderer.render(model, this, 0, RenderType.entityTranslucent(this.getTextureFile()), poseStack, bufferIn, bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureFile())), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -24 / 16F, 0.0D);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureFile() {
        return new ResourceLocation(HeroesUnited.MODID, "textures/accessories/jason_clothes.png");
    }

    @Override
    public ResourceLocation getModelFile() {
        return new ResourceLocation(HeroesUnited.MODID, "geo/jason_clothes.geo.json");
    }
}

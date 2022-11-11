package xyz.heroesunited.heroesunited.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.fml.ModList;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.client.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

import java.util.Arrays;

public class GeckoSuitRenderer<T extends SuitItem> extends GeoArmorRenderer<T> {

    public GeckoSuitRenderer() {
        this(new GeckoSuitModel<>());
    }

    public GeckoSuitRenderer(AnimatedGeoModel<T> modelProvider) {
        super(modelProvider);
    }

    @Override
    public void render(float partialTicks, PoseStack poseStack, VertexConsumer bufferIn, int packedLightIn) {
        poseStack.translate(0.0D, 24 / 16F, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        GeoModel model;
        if (HUPlayerUtil.haveSmallArms(this.entityLiving) && this.getGeoModelProvider() instanceof GeckoSuitModel) {
            model = this.getGeoModelProvider().getModel(((GeckoSuitModel<T>) this.getGeoModelProvider()).getSlimModelLocation(this.currentArmorItem));
        } else {
            model = this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelLocation(this.currentArmorItem));
        }

        AnimationEvent<T> itemEvent = new AnimationEvent<>(this.currentArmorItem, 0, 0, 0, false,
                Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
        this.getGeoModelProvider().setCustomAnimations(currentArmorItem, this.getInstanceId(this.currentArmorItem), itemEvent);
        this.fitToBiped();
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, getTextureLocation(currentArmorItem));
        Color renderColor = getRenderColor(currentArmorItem, partialTicks, poseStack, null, bufferIn, packedLightIn);
        RenderType renderType = getRenderType(currentArmorItem, partialTicks, poseStack, null, bufferIn, packedLightIn,
                getTextureLocation(currentArmorItem));
        render(model, currentArmorItem, partialTicks, renderType, poseStack, null, bufferIn, packedLightIn,
                OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliCompat.patchouliLoaded(poseStack);
        }
        poseStack.popPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -24 / 16F, 0.0D);
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public GeoArmorRenderer<T> applySlot(EquipmentSlot slot) {
        if (HUPlayerUtil.haveSmallArms(this.entityLiving) && this.getGeoModelProvider() instanceof GeckoSuitModel) {
            this.getGeoModelProvider().getModel(((GeckoSuitModel<T>) this.getGeoModelProvider()).getSlimModelLocation(this.currentArmorItem));
        } else {
            this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelLocation(this.currentArmorItem));
        }

        this.setBoneVisibility(this.headBone, false);
        this.setBoneVisibility(this.bodyBone, false);
        this.setBoneVisibility(this.rightArmBone, false);
        this.setBoneVisibility(this.leftArmBone, false);
        this.setBoneVisibility(this.rightLegBone, false);
        this.setBoneVisibility(this.leftLegBone, false);
        this.setBoneVisibility(this.rightBootBone, false);
        this.setBoneVisibility(this.rightBootBone, false);
        this.setBoneVisibility(this.leftBootBone, false);

        switch (slot) {
            case HEAD -> this.setBoneVisibility(this.headBone, true);
            case CHEST -> {
                this.setBoneVisibility(this.bodyBone, true);
                this.setBoneVisibility(this.rightArmBone, true);
                this.setBoneVisibility(this.leftArmBone, true);
            }
            case LEGS -> {
                this.setBoneVisibility(this.rightLegBone, true);
                this.setBoneVisibility(this.leftLegBone, true);
            }
            case FEET -> {
                this.setBoneVisibility(this.rightBootBone, true);
                this.setBoneVisibility(this.rightBootBone, true);
                this.setBoneVisibility(this.leftBootBone, true);
            }
            default -> {}
        }
        return this;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        if (HUPlayerUtil.haveSmallArms(this.entityLiving) && this.getGeoModelProvider() instanceof GeckoSuitModel) {
            return ((GeckoSuitModel<T>) this.getGeoModelProvider()).getSlimTextureLocation(instance);
        }
        return super.getTextureLocation(instance);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack poseStack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    public T getCurrentArmorItem() {
        return this.currentArmorItem;
    }

    @Override
    public int getInstanceId(T animatable) {
        return this.itemStack.isEmpty() ? super.getInstanceId(animatable) : GeckoLibUtil.getIDFromStack(this.itemStack);
    }
}
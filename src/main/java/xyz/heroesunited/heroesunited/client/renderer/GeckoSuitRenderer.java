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
import software.bernie.geckolib3.core.processor.IBone;
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
    public void render(float partialTicks, PoseStack stack, VertexConsumer bufferIn, int packedLightIn) {
        stack.translate(0.0D, 24 / 16F, 0.0D);
        stack.scale(-1.0F, -1.0F, 1.0F);
        GeoModel model;
        if (HUPlayerUtil.haveSmallArms(this.entityLiving) && this.getGeoModelProvider() instanceof GeckoSuitModel) {
            model = this.getGeoModelProvider().getModel(((GeckoSuitModel<T>) this.getGeoModelProvider()).getSlimModelLocation(this.currentArmorItem));
        } else {
            model = this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelLocation(this.currentArmorItem));
        }

        AnimationEvent<T> itemEvent = new AnimationEvent<>(this.currentArmorItem, 0, 0, 0, false,
                Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
        this.getGeoModelProvider().setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);
        this.fitToBiped();
        stack.pushPose();
        RenderSystem.setShaderTexture(0, getTextureLocation(currentArmorItem));
        Color renderColor = getRenderColor(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn);
        RenderType renderType = getRenderType(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn,
                getTextureLocation(currentArmorItem));
        render(model, currentArmorItem, partialTicks, renderType, stack, null, bufferIn, packedLightIn,
                OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliCompat.patchouliLoaded(stack);
        }
        stack.popPose();
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.translate(0.0D, -24 / 16F, 0.0D);
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public GeoArmorRenderer<T> applySlot(EquipmentSlot slot) {
        if (HUPlayerUtil.haveSmallArms(this.entityLiving) && this.getGeoModelProvider() instanceof GeckoSuitModel) {
            this.getGeoModelProvider().getModel(((GeckoSuitModel<T>) this.getGeoModelProvider()).getSlimModelLocation(this.currentArmorItem));
        } else {
            this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelLocation(this.currentArmorItem));
        }

        IBone headBone = this.getAndHideBone(this.headBone);
        IBone bodyBone = this.getAndHideBone(this.bodyBone);
        IBone rightArmBone = this.getAndHideBone(this.rightArmBone);
        IBone leftArmBone = this.getAndHideBone(this.leftArmBone);
        IBone rightLegBone = this.getAndHideBone(this.rightLegBone);
        IBone leftLegBone = this.getAndHideBone(this.leftLegBone);
        IBone rightBootBone = this.getAndHideBone(this.rightBootBone);
        IBone leftBootBone = this.getAndHideBone(this.leftBootBone);

        switch (slot) {
            case HEAD:
                if (headBone != null)
                    headBone.setHidden(false);
                break;
            case CHEST:
                if (bodyBone != null)
                    bodyBone.setHidden(false);
                if (rightArmBone != null)
                    rightArmBone.setHidden(false);
                if (leftArmBone != null)
                    leftArmBone.setHidden(false);
                break;
            case LEGS:
                if (rightLegBone != null)
                    rightLegBone.setHidden(false);
                if (leftLegBone != null)
                    leftLegBone.setHidden(false);
                break;
            case FEET:
                if (rightBootBone != null)
                    rightBootBone.setHidden(false);
                if (leftBootBone != null)
                    leftBootBone.setHidden(false);
                break;
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
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entitySolid(getTextureLocation(animatable));
    }

    public T getCurrentArmorItem() {
        return this.currentArmorItem;
    }

    @Override
    public Integer getUniqueID(T animatable) {
        return this.itemStack.isEmpty() ? super.getUniqueID(animatable) : GeckoLibUtil.getIDFromStack(this.itemStack);
    }
}
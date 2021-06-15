package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.common.abilities.Ability;

import java.awt.*;
import java.util.Arrays;

public class GeoAbilityRenderer<T extends Ability & IGeoAbility> extends BipedModel implements IGeoRenderer<T> {

    protected T currentAbility;
    protected AbstractClientPlayerEntity player;
    protected String name;

    public String headBone = "armorHead";
    public String bodyBone = "armorBody";
    public String rightArmBone = "armorRightArm";
    public String leftArmBone = "armorLeftArm";
    public String rightLegBone = "armorRightLeg";
    public String leftLegBone = "armorLeftLeg";
    public String rightBootBone = "armorRightBoot";
    public String leftBootBone = "armorLeftBoot";

    protected final AnimatedGeoModel<T> modelProvider;

    public GeoAbilityRenderer(AnimatedGeoModel<T> modelProvider) {
        super(1);
        this.modelProvider = modelProvider;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentAbility));
        AnimationEvent itemEvent = new AnimationEvent(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.player));
        modelProvider.setLivingAnimations(currentAbility, this.getUniqueID(this.currentAbility), itemEvent);

        if (!currentAbility.renderAsDefault()) {
            currentAbility.renderGeoAbilityRenderer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha, model, itemEvent, this.player, this);
        } else {
            matrixStackIn.translate(0.0D, 1.5D, 0.0D);
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            this.fitToBiped();
            matrixStackIn.pushPose();
            Minecraft.getInstance().textureManager.bind(this.getTextureLocation(this.currentAbility));
            RenderType renderType = this.getRenderType(this.currentAbility, 0, matrixStackIn, null, bufferIn, packedLightIn, this.getTextureLocation(this.currentAbility));
            this.render(model, this.currentAbility, 0, renderType, matrixStackIn, null, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            matrixStackIn.popPose();
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.translate(0.0D, -1.5D, 0.0D);
        }
    }

    public void fitToBiped() {
        IBone headBone = this.modelProvider.getBone(this.headBone);
        IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
        IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
        IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
        IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
        IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
        IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
        IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);

        try {
            GeoUtils.copyRotations(this.head, headBone);
            GeoUtils.copyRotations(this.body, bodyBone);
            GeoUtils.copyRotations(this.rightArm, rightArmBone);
            GeoUtils.copyRotations(this.leftArm, leftArmBone);
            GeoUtils.copyRotations(this.rightLeg, rightLegBone);
            GeoUtils.copyRotations(this.leftLeg, leftLegBone);
            GeoUtils.copyRotations(this.rightLeg, rightBootBone);
            GeoUtils.copyRotations(this.leftLeg, leftBootBone);
            headBone.setPositionX(this.head.x);
            headBone.setPositionY(-this.head.y);
            headBone.setPositionZ(this.head.z);
            bodyBone.setPositionX(this.body.x);
            bodyBone.setPositionY(-this.body.y);
            bodyBone.setPositionZ(this.body.z);
            rightArmBone.setPositionX(this.rightArm.x + 5.0F);
            rightArmBone.setPositionY(2.0F - this.rightArm.y);
            rightArmBone.setPositionZ(this.rightArm.z);
            leftArmBone.setPositionX(this.leftArm.x - 5.0F);
            leftArmBone.setPositionY(2.0F - this.leftArm.y);
            leftArmBone.setPositionZ(this.leftArm.z);
            rightLegBone.setPositionX(this.rightLeg.x + 2.0F);
            rightLegBone.setPositionY(12.0F - this.rightLeg.y);
            rightLegBone.setPositionZ(this.rightLeg.z);
            leftLegBone.setPositionX(this.leftLeg.x - 2.0F);
            leftLegBone.setPositionY(12.0F - this.leftLeg.y);
            leftLegBone.setPositionZ(this.leftLeg.z);
            rightBootBone.setPositionX(this.rightLeg.x + 2.0F);
            rightBootBone.setPositionY(12.0F - this.rightLeg.y);
            rightBootBone.setPositionZ(this.rightLeg.z);
            leftBootBone.setPositionX(this.leftLeg.x - 2.0F);
            leftBootBone.setPositionY(12.0F - this.leftLeg.y);
            leftBootBone.setPositionZ(this.leftLeg.z);
        } catch (Exception var10) {
            throw new RuntimeException("Could not find an armor bone.", var10);
        }
    }

    public void renderFirstPersonArm(T ability, PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
        GeoModel model = this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelLocation(ability));
        if (model.topLevelBones.size() == 0)
            return;
        GeoBone bone = model.getBone(side == HandSide.LEFT ? this.leftArmBone : this.rightArmBone).get();
        this.attackTime = 0.0F;
        this.crouching = false;
        this.swimAmount = 0.0F;
        matrix.pushPose();
        matrix.translate(0.0D, 1.5F, 0.0D);
        matrix.scale(-1.0F, -1.0F, 1.0F);
        AnimationEvent itemEvent = new AnimationEvent(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.player));
        this.getGeoModelProvider().setLivingAnimations(ability, this.getUniqueID(ability), itemEvent);

        ModelRenderer modelRenderer = side == HandSide.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
        GeoUtils.copyRotations(modelRenderer, bone);
        bone.setPositionX(side == HandSide.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5);
        bone.setPositionY(2 - modelRenderer.y);
        bone.setPositionZ(modelRenderer.z);

        matrix.pushPose();
        renderer.getModel().translateToHand(side, matrix);
        Minecraft.getInstance().textureManager.bind(this.getTextureLocation(ability));
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(ability)));
        Color renderColor = this.getRenderColor(ability, 0, matrix, null, builder, packedLightIn);
        bone.setHidden(false);
        this.renderRecursively(bone, matrix, builder, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        matrix.popPose();
        matrix.scale(-1.0F, -1.0F, 1.0F);
        matrix.translate(0.0D, -1.5F, 0.0D);
        matrix.popPose();
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    public void setCurrentAbility(AbstractClientPlayerEntity player, T ability, BipedModel from, String name) {
        this.player = player;
        this.currentAbility = ability;
        this.name = name;
        from.copyPropertiesTo(this);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    public AbstractClientPlayerEntity getPlayer() {
        return player;
    }
}

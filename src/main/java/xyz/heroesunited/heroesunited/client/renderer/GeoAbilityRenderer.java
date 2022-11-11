package xyz.heroesunited.heroesunited.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.GeoAbilityClientProperties;

import java.util.Arrays;

public class GeoAbilityRenderer<T extends Ability & IAnimatable> extends HumanoidModel<AbstractClientPlayer> implements IGeoRenderer<T> {

    protected AbstractClientPlayer player;
    protected final T currentAbility;

    public String headBone = "armorHead";
    public String bodyBone = "armorBody";
    public String rightArmBone = "armorRightArm";
    public String leftArmBone = "armorLeftArm";
    public String rightLegBone = "armorRightLeg";
    public String leftLegBone = "armorLeftLeg";
    public String rightBootBone = "armorRightBoot";
    public String leftBootBone = "armorLeftBoot";

    protected final AnimatedGeoModel<T> modelProvider;
    protected final GeoAbilityClientProperties<T> clientProperties;
    public MultiBufferSource rtb;

    public GeoAbilityRenderer(GeoAbilityClientProperties<T> clientProperties, T ability) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.currentAbility = ability;
        this.modelProvider = clientProperties.getGeoModel();
        this.clientProperties = clientProperties;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        GeoModel model = this.modelProvider.getModel(getModelLocation(this.currentAbility));
        if (!this.clientProperties.showingAnimationAlways()) this.doAnimationProcess();
        if (alpha != 0F && !this.clientProperties.renderGeoAbilityRenderer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha, false, model, this.player, this)) {
            matrixStackIn.translate(0.0D, 1.5D, 0.0D);
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            this.fitToBiped();
            matrixStackIn.pushPose();
            RenderSystem.setShaderTexture(0, this.getTextureLocation(this.currentAbility));
            RenderType renderType = this.getRenderType(this.currentAbility, 0, matrixStackIn, null, bufferIn, packedLightIn, this.getTextureLocation(this.currentAbility));
            this.render(model, this.currentAbility, 0, renderType, matrixStackIn, null, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStackIn.popPose();
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.translate(0.0D, -1.5D, 0.0D);
        }
    }

    public void fitToBiped() {
        if (this.headBone != null) {
            IBone headBone = this.modelProvider.getBone(this.headBone);
            GeoUtils.copyRotations(this.head, headBone);
            headBone.setPositionX(this.head.x);
            headBone.setPositionY(-this.head.y);
            headBone.setPositionZ(this.head.z);
        }

        if (this.bodyBone != null) {
            IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
            GeoUtils.copyRotations(this.body, bodyBone);
            bodyBone.setPositionX(this.body.x);
            bodyBone.setPositionY(-this.body.y);
            bodyBone.setPositionZ(this.body.z);
        }

        if (this.rightArmBone != null) {
            IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
            GeoUtils.copyRotations(this.rightArm, rightArmBone);
            rightArmBone.setPositionX(this.rightArm.x + 5);
            rightArmBone.setPositionY(2 - this.rightArm.y);
            rightArmBone.setPositionZ(this.rightArm.z);
        }

        if (this.leftArmBone != null) {
            IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
            GeoUtils.copyRotations(this.leftArm, leftArmBone);
            leftArmBone.setPositionX(this.leftArm.x - 5);
            leftArmBone.setPositionY(2 - this.leftArm.y);
            leftArmBone.setPositionZ(this.leftArm.z);
        }

        if (this.rightLegBone != null) {
            IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
            GeoUtils.copyRotations(this.rightLeg, rightLegBone);
            rightLegBone.setPositionX(this.rightLeg.x + 2);
            rightLegBone.setPositionY(12 - this.rightLeg.y);
            rightLegBone.setPositionZ(this.rightLeg.z);
            if (this.rightBootBone != null) {
                IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
                GeoUtils.copyRotations(this.rightLeg, rightBootBone);
                rightBootBone.setPositionX(this.rightLeg.x + 2);
                rightBootBone.setPositionY(12 - this.rightLeg.y);
                rightBootBone.setPositionZ(this.rightLeg.z);
            }
        }

        if (this.leftLegBone != null) {
            IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
            GeoUtils.copyRotations(this.leftLeg, leftLegBone);
            leftLegBone.setPositionX(this.leftLeg.x - 2);
            leftLegBone.setPositionY(12 - this.leftLeg.y);
            leftLegBone.setPositionZ(this.leftLeg.z);
            if (this.leftBootBone != null) {
                IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);
                GeoUtils.copyRotations(this.leftLeg, leftBootBone);
                leftBootBone.setPositionX(this.leftLeg.x - 2);
                leftBootBone.setPositionY(12 - this.leftLeg.y);
                leftBootBone.setPositionZ(this.leftLeg.z);
            }
        }
    }

    public void renderFirstPersonArm(PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, HumanoidArm side) {
        this.renderFirstPersonArm(renderer, poseStack, buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(currentAbility))), packedLightIn, OverlayTexture.NO_OVERLAY, side, 1f, 1f, 1f, 1f);
    }

    public void renderFirstPersonArm(PlayerRenderer renderer, PoseStack poseStack, VertexConsumer builder, int packedLightIn, int packedOverlayIn, HumanoidArm side, float red, float green, float blue, float alpha) {
        GeoModel model = this.modelProvider.getModel(getModelLocation(this.currentAbility));
        if (!this.clientProperties.showingAnimationAlways()) this.doAnimationProcess();
        if (alpha == 0F || model.topLevelBones.isEmpty())
            return;

        this.attackTime = 0.0F;
        this.crouching = false;
        this.swimAmount = 0.0F;
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5F, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        if (!this.clientProperties.renderGeoAbilityRenderer(poseStack, builder, packedLightIn, packedOverlayIn, red, green, blue, alpha, true, model, this.player, this)) {
            model.getBone(side == HumanoidArm.LEFT ? this.leftArmBone : this.rightArmBone).ifPresent(bone -> {
                if (!bone.childBones.isEmpty() || !bone.childCubes.isEmpty()) {
                    ModelPart modelRenderer = side == HumanoidArm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
                    GeoUtils.copyRotations(modelRenderer, bone);
                    bone.setPositionX(side == HumanoidArm.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5);
                    bone.setPositionY(2 - modelRenderer.y);
                    bone.setPositionZ(modelRenderer.z);
                    bone.setHidden(false);

                    poseStack.pushPose();
                    RenderSystem.setShaderTexture(0, this.getTextureLocation(this.currentAbility));
                    this.renderRecursively(bone, poseStack, builder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                    poseStack.popPose();
                }
            });
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.5F, 0.0D);
        poseStack.popPose();
    }

    @Override
    public void setCurrentRTB(MultiBufferSource rtb) {
        this.rtb = rtb;
    }

    @Override
    public MultiBufferSource getCurrentRTB() {
        return this.rtb;
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    public ResourceLocation getModelLocation(T instance) {
        return this.modelProvider.getModelLocation(instance);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setCurrentAbility(AbstractClientPlayer player, MultiBufferSource bufferSource, HumanoidModel from) {
        this.player = player;
        this.rtb = bufferSource;
        from.copyPropertiesTo(this);
    }

    public void doAnimationProcess() {
        this.modelProvider.getModel(getModelLocation(this.currentAbility));
        if (this.modelProvider.getAnimationFileLocation(this.currentAbility) != null) {
            AnimationEvent<T> abilityEvent = new AnimationEvent<>(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.player));
            this.modelProvider.setCustomAnimations(this.currentAbility, this.getInstanceId(this.currentAbility), abilityEvent);
        }
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack poseStack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }
}

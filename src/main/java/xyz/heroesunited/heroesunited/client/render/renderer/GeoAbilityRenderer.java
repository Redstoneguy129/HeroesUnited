package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import xyz.heroesunited.heroesunited.common.abilities.Ability;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class GeoAbilityRenderer<T extends Ability & IGeoAbility> extends BipedEntityModel implements IGeoRenderer<T> {

    protected T currentAbility;
    protected AbstractClientPlayerEntity player;

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
        super(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER));
        this.modelProvider = modelProvider;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentAbility));
        AnimationEvent abilityEvent = new AnimationEvent(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.player));
        modelProvider.setLivingAnimations(currentAbility, this.getUniqueID(this.currentAbility), abilityEvent);

        if (!currentAbility.renderAsDefault()) {
            currentAbility.renderGeoAbilityRenderer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha, model, abilityEvent, this.player, this);
        } else {
            matrixStackIn.translate(0.0D, 1.5D, 0.0D);
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            this.fitToBiped();
            matrixStackIn.push();
            RenderSystem.setShaderTexture(0, this.getTextureLocation(this.currentAbility));
            RenderLayer renderType = this.getRenderType(this.currentAbility, 0, matrixStackIn, null, bufferIn, packedLightIn, this.getTextureLocation(this.currentAbility));
            this.render(model, this.currentAbility, 0, renderType, matrixStackIn, null, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
            matrixStackIn.pop();
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
            matrixStackIn.translate(0.0D, -1.5D, 0.0D);
        }
    }

    public void fitToBiped() {
        if (this.headBone != null) {
            IBone headBone = this.modelProvider.getBone(this.headBone);
            GeoUtils.copyRotations(this.head, headBone);
            headBone.setPositionX(this.head.pivotX);
            headBone.setPositionY(-this.head.pivotY);
            headBone.setPositionZ(this.head.pivotZ);
        }

        if (this.bodyBone != null) {
            IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
            GeoUtils.copyRotations(this.body, bodyBone);
            bodyBone.setPositionX(this.body.pivotX);
            bodyBone.setPositionY(-this.body.pivotY);
            bodyBone.setPositionZ(this.body.pivotZ);
        }

        if (this.rightArmBone != null) {
            IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
            GeoUtils.copyRotations(this.rightArm, rightArmBone);
            rightArmBone.setPositionX(this.rightArm.pivotX + 5);
            rightArmBone.setPositionY(2 - this.rightArm.pivotY);
            rightArmBone.setPositionZ(this.rightArm.pivotZ);
        }

        if (this.leftArmBone != null) {
            IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
            GeoUtils.copyRotations(this.leftArm, leftArmBone);
            leftArmBone.setPositionX(this.leftArm.pivotX - 5);
            leftArmBone.setPositionY(2 - this.leftArm.pivotY);
            leftArmBone.setPositionZ(this.leftArm.pivotZ);
        }

        if (this.rightLegBone != null) {
            IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
            GeoUtils.copyRotations(this.rightLeg, rightLegBone);
            rightLegBone.setPositionX(this.rightLeg.pivotX + 2);
            rightLegBone.setPositionY(12 - this.rightLeg.pivotY);
            rightLegBone.setPositionZ(this.rightLeg.pivotZ);
            if (this.rightBootBone != null) {
                IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
                GeoUtils.copyRotations(this.rightLeg, rightBootBone);
                rightBootBone.setPositionX(this.rightLeg.pivotX + 2);
                rightBootBone.setPositionY(12 - this.rightLeg.pivotY);
                rightBootBone.setPositionZ(this.rightLeg.pivotZ);
            }
        }

        if (this.leftLegBone != null) {
            IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
            GeoUtils.copyRotations(this.leftLeg, leftLegBone);
            leftLegBone.setPositionX(this.leftLeg.pivotX - 2);
            leftLegBone.setPositionY(12 - this.leftLeg.pivotY);
            leftLegBone.setPositionZ(this.leftLeg.pivotZ);
            if (this.leftBootBone != null) {
                IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);
                GeoUtils.copyRotations(this.leftLeg, leftBootBone);
                leftBootBone.setPositionX(this.leftLeg.pivotX - 2);
                leftBootBone.setPositionY(12 - this.leftLeg.pivotY);
                leftBootBone.setPositionZ(this.leftLeg.pivotZ);
            }
        }
    }

    public void renderFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider buffer, int packedLightIn, Arm side) {
        this.renderFirstPersonArm(renderer, matrix, buffer.getBuffer(RenderLayer.getEntityTranslucent(getTextureLocation(currentAbility))), packedLightIn, side, 1f, 1f, 1f, 1f);
    }

    public void renderFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumer builder, int packedLightIn, Arm side, float red, float green, float blue, float alpha) {
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentAbility));
        AnimationEvent abilityEvent = new AnimationEvent(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.player));
        modelProvider.setLivingAnimations(currentAbility, this.getUniqueID(this.currentAbility), abilityEvent);

        if (model.topLevelBones.isEmpty())
            return;
        Optional<GeoBone> bone = model.getBone(side == Arm.LEFT ? this.leftArmBone : this.rightArmBone);
        if (!bone.isPresent() || bone.get().childBones.isEmpty() && bone.get().childCubes.isEmpty())
            return;

        this.handSwingProgress = 0.0F;
        this.sneaking = false;
        this.leaningPitch = 0.0F;
        matrix.push();
        matrix.translate(0.0D, 1.5F, 0.0D);
        matrix.scale(-1.0F, -1.0F, 1.0F);
        ModelPart modelRenderer = side == Arm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
        GeoUtils.copyRotations(modelRenderer, bone.get());
        bone.get().setPositionX(side == Arm.LEFT ? modelRenderer.pivotX - 5 : modelRenderer.pivotX + 5);
        bone.get().setPositionY(2 - modelRenderer.pivotY);
        bone.get().setPositionZ(modelRenderer.pivotZ);
        bone.get().setHidden(false);

        matrix.push();
        RenderSystem.setShaderTexture(0, this.getTextureLocation(this.currentAbility));
        this.renderRecursively(bone.get(), matrix, builder, packedLightIn, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
        matrix.pop();
        matrix.scale(-1.0F, -1.0F, 1.0F);
        matrix.translate(0.0D, -1.5F, 0.0D);
        matrix.pop();
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public Identifier getTextureLocation(T instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    public void setCurrentAbility(AbstractClientPlayerEntity player, T ability, BipedEntityModel from) {
        this.player = player;
        this.currentAbility = ability;
        from.setAttributes(this);
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    public AbstractClientPlayerEntity getPlayer() {
        return player;
    }
}

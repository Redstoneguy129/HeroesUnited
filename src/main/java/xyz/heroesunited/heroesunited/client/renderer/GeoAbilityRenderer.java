package xyz.heroesunited.heroesunited.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.GeoAbilityClientProperties;
import xyz.heroesunited.heroesunited.common.abilities.animatable.GeoAbility;

import javax.annotation.Nullable;
import java.util.List;

public class GeoAbilityRenderer<T extends Ability & GeoAbility> extends HumanoidModel<AbstractClientPlayer> implements GeoRenderer<T> {

    public static final DataTicket<Ability> ABILITY_DATA_TICKET = new DataTicket<>("ability", Ability.class);

    protected final List<GeoRenderLayer<T>> renderLayers = new ObjectArrayList<>();
    protected final GeoModel<T> model;
    protected final GeoAbilityClientProperties<T> clientProperties;
    protected Color color = null;

    protected T ability;
    protected HumanoidModel<?> baseModel;
    protected float scaleWidth = 1, scaleHeight = 1;

    protected Matrix4f entityRenderTranslations = new Matrix4f();
    protected Matrix4f modelRenderTranslations = new Matrix4f();

    protected BakedGeoModel lastModel = null;
    protected GeoBone head, body, rightArm, leftArm, rightLeg, leftLeg, rightBoot, leftBoot;

    protected AbstractClientPlayer player = null;

    public GeoAbilityRenderer(GeoAbilityClientProperties<T> clientProperties, T ability) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));

        this.model = clientProperties.getGeoModel();
        this.ability = ability;
        this.clientProperties = clientProperties;
    }

    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    @Override
    public T getAnimatable() {
        return this.ability;
    }

    @Override
    public long getInstanceId(T animatable) {
        return ability.name.hashCode() + this.player.getId();
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public List<GeoRenderLayer<T>> getRenderLayers() {
        return this.renderLayers;
    }

    public GeoAbilityRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
        this.renderLayers.add(renderLayer);

        return this;
    }

    public GeoAbilityRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    public GeoAbilityRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    @Nullable
    public GeoBone getHeadBone() {
        return this.model.getBone("armorHead").orElse(null);
    }

    @Nullable
    public GeoBone getBodyBone() {
        return this.model.getBone("armorBody").orElse(null);
    }

    @Nullable
    public GeoBone getRightArmBone() {
        return this.model.getBone("armorRightArm").orElse(null);
    }

    @Nullable
    public GeoBone getLeftArmBone() {
        return this.model.getBone("armorLeftArm").orElse(null);
    }

    @Nullable
    public GeoBone getRightLegBone() {
        return this.model.getBone("armorRightLeg").orElse(null);
    }

    @Nullable
    public GeoBone getLeftLegBone() {
        return this.model.getBone("armorLeftLeg").orElse(null);
    }

    @Nullable
    public GeoBone getRightBootBone() {
        return this.model.getBone("armorRightBoot").orElse(null);
    }

    @Nullable
    public GeoBone getLeftBootBone() {
        return this.model.getBone("armorLeftBoot").orElse(null);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

        applyBaseModel(this.baseModel);
        grabRelevantBones(getGeoModel().getBakedModel(getGeoModel().getModelResource(this.ability)));
        applyBaseTransformations(this.baseModel);

        if (this.scaleWidth != 1 && this.scaleHeight != 1)
            poseStack.scale(this.scaleWidth, this.scaleHeight, this.scaleWidth);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();

        if (mc.levelRenderer.shouldShowEntityOutlines() && mc.shouldEntityAppearGlowing(this.player))
            bufferSource = mc.renderBuffers().outlineBufferSource();

        float partialTick = mc.getFrameTime();
        RenderType renderType = getRenderType(this.ability, getTextureLocation(this.ability), bufferSource, partialTick);
        Color color = Color.ofRGBA(red, green, blue, alpha);
        if (this.color == null || !this.color.equals(color)) {
            this.color = color;
        }
        defaultRender(poseStack, this.ability, bufferSource, renderType, bufferSource.getBuffer(renderType), 0, partialTick, packedLight);
    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return this.color != null ? this.color : GeoRenderer.super.getRenderColor(animatable, partialTick, packedLight);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        poseStack.translate(0, 24 / 16f, 0);
        poseStack.scale(-1, -1, 1);

        if (!isReRender && !this.clientProperties.showingAnimationAlways()) {
            this.doAnimationProcess();
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());
        if (alpha != 0F && this.clientProperties.continueRendering(this, getGeoModel().getBakedModel(getGeoModel().getModelResource(this.ability)), this.player, false, partialTick, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha)) {
            GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
        poseStack.popPose();
    }

    public void doAnimationProcess() {
        if (this.player != null && this.model != null) {
            AnimationState<T> animationState = new AnimationState<>(this.ability, 0, 0, Minecraft.getInstance().getFrameTime(), false);
            long instanceId = getInstanceId(this.ability);

            animationState.setData(DataTickets.TICK, this.ability.getTick(this.player));
            animationState.setData(DataTickets.ENTITY, this.player);
            animationState.setData(ABILITY_DATA_TICKET, this.ability);
            this.model.addAdditionalStateData(this.ability, instanceId, animationState::setData);
            this.model.handleAnimations(this.ability, instanceId, animationState);
        }
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        RenderUtils.scaleMatrixForBone(poseStack, bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);
            Vec3 renderOffset = animatable.getPlayer().isCrouching() ? new Vec3(0.0D, -0.125D, 0.0D) : Vec3.ZERO;

            bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(localMatrix.translation(new Vector3f(renderOffset.toVector3f())));

            Matrix4f worldPosition = new Matrix4f(localMatrix);

            bone.setWorldSpaceMatrix(worldPosition.translation(new Vector3f(animatable.getPlayer().position().toVector3f())));
        }

        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender)
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.popPose();
    }

    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel == bakedModel)
            return;

        this.lastModel = bakedModel;
        this.head = getHeadBone();
        this.body = getBodyBone();
        this.rightArm = getRightArmBone();
        this.leftArm = getLeftArmBone();
        this.rightLeg = getRightLegBone();
        this.leftLeg = getLeftLegBone();
        this.rightBoot = getRightBootBone();
        this.leftBoot = getLeftBootBone();
    }

    public void prepForRender(@Nullable AbstractClientPlayer entity, @Nullable HumanoidModel<?> baseModel) {
        if (entity == null || baseModel == null)
            return;

        this.baseModel = baseModel;
        this.player = entity;
    }


    protected void applyBaseModel(HumanoidModel<?> baseModel) {
        this.young = baseModel.young;
        this.crouching = baseModel.crouching;
        this.riding = baseModel.riding;
        this.rightArmPose = baseModel.rightArmPose;
        this.leftArmPose = baseModel.leftArmPose;
    }

    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        if (this.head != null) {
            ModelPart headPart = baseModel.head;

            RenderUtils.matchModelPartRot(headPart, this.head);
            this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
        }

        if (this.body != null) {
            ModelPart bodyPart = baseModel.body;

            RenderUtils.matchModelPartRot(bodyPart, this.body);
            this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
        }

        if (this.rightArm != null) {
            ModelPart rightArmPart = baseModel.rightArm;

            RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
        }

        if (this.leftArm != null) {
            ModelPart leftArmPart = baseModel.leftArm;

            RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.x - 5f, 2f - leftArmPart.y, leftArmPart.z);
        }

        if (this.rightLeg != null) {
            ModelPart rightLegPart = baseModel.rightLeg;

            RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);

            if (this.rightBoot != null) {
                RenderUtils.matchModelPartRot(rightLegPart, this.rightBoot);
                this.rightBoot.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
            }
        }

        if (this.leftLeg != null) {
            ModelPart leftLegPart = baseModel.leftLeg;

            RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);

            if (this.leftBoot != null) {
                RenderUtils.matchModelPartRot(leftLegPart, this.leftBoot);
                this.leftBoot.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
            }
        }
    }

    @Override
    public void setAllVisible(boolean pVisible) {
        super.setAllVisible(pVisible);

        setBoneVisible(this.head, pVisible);
        setBoneVisible(this.body, pVisible);
        setBoneVisible(this.rightArm, pVisible);
        setBoneVisible(this.leftArm, pVisible);
        setBoneVisible(this.rightLeg, pVisible);
        setBoneVisible(this.leftLeg, pVisible);
        setBoneVisible(this.rightBoot, pVisible);
        setBoneVisible(this.leftBoot, pVisible);
    }


    public void renderFirstPersonArm(PlayerRenderer renderer, HumanoidArm side, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn) {
        this.renderFirstPersonArm(renderer, side, poseStack, buffer, buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(ability))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    public void renderFirstPersonArm(PlayerRenderer renderer, HumanoidArm side, PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer builder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        BakedGeoModel model = this.model.getBakedModel(getModelLocation(this.ability));
        if (!this.clientProperties.showingAnimationAlways()) this.doAnimationProcess();
        if (model.topLevelBones().isEmpty())
            return;

        this.attackTime = 0.0F;
        this.crouching = false;
        this.swimAmount = 0.0F;
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5F, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        if (alpha != 0F && this.clientProperties.continueRendering(this, model, this.player, true, Minecraft.getInstance().getPartialTick(), poseStack, bufferSource, builder, packedLightIn, packedOverlayIn, red, green, blue, alpha)) {
            GeoBone bone = side == HumanoidArm.LEFT ? getLeftArmBone() : getRightArmBone();
            if (bone != null && (!bone.getChildBones().isEmpty() || !bone.getCubes().isEmpty())) {
                ModelPart modelRenderer = side == HumanoidArm.LEFT ? renderer.getModel().leftArm : renderer.getModel().rightArm;
                RenderUtils.matchModelPartRot(modelRenderer, bone);
                bone.updatePosition(side == HumanoidArm.LEFT ? modelRenderer.x - 5 : modelRenderer.x + 5, 2 - modelRenderer.y, modelRenderer.z);
                bone.setHidden(false);

                poseStack.pushPose();
                RenderSystem.setShaderTexture(0, this.getTextureLocation(this.ability));
                this.renderRecursively(poseStack, this.ability, bone, null, null, builder, false, Minecraft.getInstance().getFrameTime(), packedLightIn, packedOverlayIn, red, green, blue, alpha);
                poseStack.popPose();
            }
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.5F, 0.0D);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.model.getTextureResource(instance);
    }

    public ResourceLocation getModelLocation(T instance) {
        return this.model.getModelResource(instance);
    }

    protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
        if (bone == null)
            return;

        bone.setHidden(!visible);
    }
}
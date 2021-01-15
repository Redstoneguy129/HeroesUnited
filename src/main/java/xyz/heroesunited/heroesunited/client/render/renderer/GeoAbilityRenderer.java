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
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.GeoUtils;
import software.bernie.geckolib3.util.RenderUtils;
import xyz.heroesunited.heroesunited.client.render.model.GeckoAbilityModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GeoAbilityRenderer<T extends IGeoAbility> extends BipedModel implements IGeoRenderer<T> {

    private T currentAbility;
    private LivingEntity entityLiving;
    private String name;

    // Set these to the names of your abilities bones
    public List<String> armorBones = Arrays.asList("armorHead", "armorBody", "armorRightArm", "armorLeftArm", "armorRightLeg", "armorLeftLeg", "armorRightBoot", "armorLeftBoot");

    private final AnimatedGeoModel<T> modelProvider;

    public GeoAbilityRenderer() {
        super(1);
        this.modelProvider = new GeckoAbilityModel();
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.translate(0.0D, 1.5F, 0.0D);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);

        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentAbility));
        AnimationEvent itemEvent = new AnimationEvent(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.entityLiving));
        modelProvider.setLivingAnimations(currentAbility, this.getUniqueID(this.currentAbility), itemEvent);
        matrixStackIn.push();
        Minecraft.getInstance().textureManager.bindTexture(getTextureLocation(currentAbility));
        Color renderColor = getRenderColor(currentAbility, 0, matrixStackIn, null, bufferIn, packedLightIn);
        RenderType renderType = getRenderType(currentAbility, 0, matrixStackIn, null, bufferIn, packedLightIn, getTextureLocation(currentAbility));
        render(model, currentAbility, 0, renderType, matrixStackIn, null, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        matrixStackIn.pop();
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        matrixStackIn.translate(0.0D, -1.5F, 0.0D);
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.push();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        if (!bone.isHidden) {
            for (GeoCube cube : bone.childCubes) {
                stack.push();
                renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                stack.pop();
            }
            for (GeoBone childBone : bone.childBones) {
                if (armorBones.contains(childBone.name)) {
                    armorBones.stream().filter(name -> childBone.name.equals(name)).forEachOrdered(name -> renderBone(name, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
                } else {
                    renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                }
            }
        }
        stack.pop();
    }

    private void renderBone(String str, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ModelRenderer from = getModelRendererById(str);
        GeoBone to = (GeoBone) this.modelProvider.getBone(str);

        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        matrixStackIn.translate(0.0D, -1.5F, 0.0D);

        matrixStackIn.push();
        if (currentAbility.copyPos()) matrixStackIn.translate(from.rotationPointX / 16.0F, from.rotationPointY / 16.0F, from.rotationPointZ / 16.0F);
        if (currentAbility.copyRotations()) GeoUtils.copyRotations(from, to);

        matrixStackIn.translate(0.0D, 1.5F, 0.0D);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        if (currentAbility.copyPos()) {
            if (from == bipedRightArm) matrixStackIn.translate(-0.31, 0.13, 0);
            if (from == bipedLeftArm) matrixStackIn.translate(0.31, 0.13, 0);
            if (from == bipedRightLeg) matrixStackIn.translate(-0.12, 0.75, 0);
            if (from == bipedLeftLeg) matrixStackIn.translate(0.12, 0.75, 0);
        }
        renderRecursively(to, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();

        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        matrixStackIn.translate(0.0D, -1.5F, 0.0D);
    }

    public ModelRenderer getModelRendererById(String name) {
        switch (name) {
            case "armorHead":
                return this.bipedHead;
            case "armorBody":
                return this.bipedBody;
            case "armorRightArm":
                return this.bipedRightArm;
            case "armorLeftArm":
                return this.bipedLeftArm;
            case "armorRightLeg":
            case "armorRightBoot":
                return this.bipedRightLeg;
            case "armorLeftLeg":
            case "armorLeftBoot":
                return this.bipedLeftLeg;
            default:
                return null;
        }
    }

    public void renderFirstPersonArm(T ability, PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
        this.swingProgress = 0.0F;
        this.isSneak = false;
        this.swimAnimation = 0.0F;
        matrix.translate(0.0D, 1.5F, 0.0D);
        matrix.scale(-1.0F, -1.0F, 1.0F);
        AnimationEvent itemEvent = new AnimationEvent(this.currentAbility, 0, 0, 0, false, Arrays.asList(this.currentAbility, this.entityLiving));
        modelProvider.setLivingAnimations(currentAbility, this.getUniqueID(this.currentAbility), itemEvent);
        matrix.push();
        Minecraft.getInstance().textureManager.bindTexture(getTextureLocation(currentAbility));
        GeoBone bone = (GeoBone) this.getGeoModelProvider().getAnimationProcessor().getBone(side == HandSide.LEFT ? "armorLeftArm" : "armorRightArm");
        if (bone != null) {
            this.renderRecursively(bone, matrix, bufferIn.getBuffer(RenderType.getEntityTranslucent(this.getTextureLocation(ability))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
        matrix.pop();
        matrix.scale(-1.0F, -1.0F, 1.0F);
        matrix.translate(0.0D, -1.5F, 0.0D);
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    public void setCurrentAbility(LivingEntity entityLiving, T ability, BipedModel from, String name) {
        this.entityLiving = entityLiving;
        this.currentAbility = ability;
        this.name = name;
        from.setModelAttributes(this);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.getEntityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public Integer getUniqueID(T animatable) {
        return Objects.hash(currentAbility instanceof Ability ? ((Ability)currentAbility).type : 1, name, this.entityLiving.getUniqueID().toString());
    }
}

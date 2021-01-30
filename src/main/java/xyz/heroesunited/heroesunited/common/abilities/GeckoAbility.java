package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.render.renderer.GeoAbilityRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.IGeoAbility;

public class GeckoAbility extends Ability implements IGeoAbility {
    private AnimationFactory factory = new AnimationFactory(this);
    @OnlyIn(Dist.CLIENT)
    private final GeoAbilityRenderer abilityRenderer = new GeoAbilityRenderer();

    public GeckoAbility() {
        super(AbilityType.GECKO);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        abilityRenderer.setCurrentAbility(player, this, renderer.getEntityModel(), name);
        abilityRenderer.render(matrix, bufferIn.getBuffer(RenderType.getEntityTranslucent(abilityRenderer.getTextureLocation(this))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
        abilityRenderer.setCurrentAbility(player, this, renderer.getEntityModel(), name);
        abilityRenderer.renderFirstPersonArm(this, renderer, matrix, bufferIn, packedLightIn, player, side);
    }

    public void registerControllers(AnimationData data) {
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getTexture() {
        if (this.getJsonObject() != null && this.getJsonObject().has("texture")) {
            if (JSONUtils.getString(this.getJsonObject(), "texture").equals("player")) {
                return Minecraft.getInstance().player.getLocationSkin();
            } else {
                return new ResourceLocation(JSONUtils.getString(this.getJsonObject(), "texture"));
            }
        } else return new ResourceLocation(this.getSuperpower().getNamespace(), "textures/ability/" + this.getSuperpower().getPath() + "_" + this.name + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getModelPath() {
        ResourceLocation res = new ResourceLocation(this.getSuperpower().getNamespace(), "geo/" + this.getSuperpower().getPath() + "_" + this.name + ".geo.json");
        return this.getJsonObject() != null ? new ResourceLocation(JSONUtils.getString(this.getJsonObject(), "model", res.toString())) : res;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getAnimationFile() {
        return new ResourceLocation(this.getSuperpower().getNamespace(), "animations/" + this.getSuperpower().getPath() + "_" + this.name + ".animation.json");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <T extends GeoAbilityRenderer> T getGeoRenderer() {
        return (T) abilityRenderer;
    }
}
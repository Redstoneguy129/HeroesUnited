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

public class GeckoAbility extends JSONAbility implements IGeoAbility {
    protected final AnimationFactory factory = new AnimationFactory(this);

    public GeckoAbility(AbilityType type) {
        super(type);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (getEnabled()) {
            GeoAbilityRenderer abilityRenderer = new GeoAbilityRenderer(getGeoModel());
            abilityRenderer.setCurrentAbility(player, this, renderer.getModel());
            abilityRenderer.renderToBuffer(matrix, bufferIn.getBuffer(RenderType.entityTranslucent(abilityRenderer.getTextureLocation(this))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
        if (getEnabled()) {
            GeoAbilityRenderer abilityRenderer = new GeoAbilityRenderer(getGeoModel());
            abilityRenderer.setCurrentAbility(player, this, renderer.getModel());
            abilityRenderer.renderFirstPersonArm(renderer, matrix, bufferIn, packedLightIn, side);
        }
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
            if (JSONUtils.getAsString(this.getJsonObject(), "texture").equals("player")) {
                return Minecraft.getInstance().player.getSkinTextureLocation();
            } else {
                return new ResourceLocation(JSONUtils.getAsString(this.getJsonObject(), "texture"));
            }
        } else return new ResourceLocation(getSuitOrSuperpowerName().getNamespace(), "textures/ability/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getModelPath() {
        ResourceLocation res = new ResourceLocation(getSuitOrSuperpowerName().getNamespace(), "geo/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".geo.json");
        return this.getJsonObject() != null ? new ResourceLocation(JSONUtils.getAsString(this.getJsonObject(), "model", res.toString())) : res;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getAnimationFile() {
        return new ResourceLocation(getSuitOrSuperpowerName().getNamespace(), "animations/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".animation.json");
    }

    private ResourceLocation getSuitOrSuperpowerName() {
        String str = getAdditionalData().contains("Suit") ? getAdditionalData().getString("Suit") : getAdditionalData().getString("Superpower");
        return new ResourceLocation(str);
    }
}
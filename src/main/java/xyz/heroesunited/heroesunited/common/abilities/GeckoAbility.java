package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.render.renderer.GeoAbilityRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.IGeoAbility;

public class GeckoAbility extends JSONAbility implements IGeoAbility {
    private AnimationFactory factory = new AnimationFactory(this);

    public GeckoAbility() {
        super(AbilityType.GECKO);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (getEnabled()) {
            GeoAbilityRenderer abilityRenderer = new GeoAbilityRenderer(getGeoModel());
            abilityRenderer.setCurrentAbility(player, this, renderer.getModel());
            abilityRenderer.render(matrix, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(abilityRenderer.getTextureLocation(this))), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, Arm side) {
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
    public Identifier getTexture() {
        if (this.getJsonObject() != null && this.getJsonObject().has("texture")) {
            if (JsonHelper.getString(this.getJsonObject(), "texture").equals("player")) {
                return MinecraftClient.getInstance().player.getSkinTexture();
            } else {
                return new Identifier(JsonHelper.getString(this.getJsonObject(), "texture"));
            }
        } else return new Identifier(getSuitOrSuperpowerName().getNamespace(), "textures/ability/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Identifier getModelPath() {
        Identifier res = new Identifier(getSuitOrSuperpowerName().getNamespace(), "geo/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".geo.json");
        return this.getJsonObject() != null ? new Identifier(JsonHelper.getString(this.getJsonObject(), "model", res.toString())) : res;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Identifier getAnimationFile() {
        return new Identifier(getSuitOrSuperpowerName().getNamespace(), "animations/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".animation.json");
    }

    private Identifier getSuitOrSuperpowerName() {
        String str = getAdditionalData().contains("Suit") ? getAdditionalData().getString("Suit") : getAdditionalData().getString("Superpower");
        return new Identifier(str);
    }
}
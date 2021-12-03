package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.HumanoidArm;
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
    public void render(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (getEnabled()) {
            GeoAbilityRenderer<GeckoAbility> abilityRenderer = new GeoAbilityRenderer<>(this);
            abilityRenderer.setCurrentAbility(player, renderer.getModel());
            abilityRenderer.renderToBuffer(matrix, bufferIn.getBuffer(RenderType.entityTranslucent(abilityRenderer.getTextureLocation(this))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonArm(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
        if (getEnabled()) {
            GeoAbilityRenderer<GeckoAbility> abilityRenderer = new GeoAbilityRenderer<>(this);
            abilityRenderer.setCurrentAbility(player, renderer.getModel());
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
            if (GsonHelper.getAsString(this.getJsonObject(), "texture").equals("player")) {
                return Minecraft.getInstance().player.getSkinTextureLocation();
            } else {
                return new ResourceLocation(GsonHelper.getAsString(this.getJsonObject(), "texture"));
            }
        } else
            return new ResourceLocation(getSuitOrSuperpowerName().getNamespace(), "textures/ability/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getModelPath() {
        ResourceLocation res = new ResourceLocation(getSuitOrSuperpowerName().getNamespace(), "geo/" + getSuitOrSuperpowerName().getPath() + "_" + this.name + ".geo.json");
        return this.getJsonObject() != null ? new ResourceLocation(GsonHelper.getAsString(this.getJsonObject(), "model", res.toString())) : res;
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
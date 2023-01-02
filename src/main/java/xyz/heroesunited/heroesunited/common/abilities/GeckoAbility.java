package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.common.abilities.animatable.GeoAbility;

import java.util.function.Consumer;

public class GeckoAbility extends JSONAbility implements GeoAbility {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new GeoAbilityClientProperties<>(this) {

            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (getEnabled()) {
                    this.abilityRenderer.prepForRender(player, renderer.getModel());
                    this.abilityRenderer.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(abilityRenderer.getTextureLocation(GeckoAbility.this))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                }
            }

            @Override
            public boolean renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
                if (getEnabled()) {
                    this.abilityRenderer.prepForRender(player, renderer.getModel());
                    this.abilityRenderer.renderFirstPersonArm(renderer, poseStack, bufferIn, packedLightIn, side);
                }
                return true;
            }

            @Override
            public ResourceLocation getTexture() {
                if (this.ability.getJsonObject().has("texture")) {
                    if (GsonHelper.getAsString(this.ability.getJsonObject(), "texture").equals("player") && this.ability.player instanceof AbstractClientPlayer player) {
                        return player.getSkinTextureLocation();
                    } else {
                        return new ResourceLocation(GsonHelper.getAsString(this.ability.getJsonObject(), "texture"));
                    }
                } else
                    return new ResourceLocation(getPowerLocation().getNamespace(), "textures/ability/" + getPowerLocation().getPath() + "_" + this.ability.name + ".png");
            }

            @Override
            public ResourceLocation getModelPath() {
                if (this.ability.getJsonObject().has("model")) {
                    return new ResourceLocation(this.ability.getJsonObject().get("model").getAsString());
                }
                return new ResourceLocation(getPowerLocation().getNamespace(), String.format("geo/%s_%s.geo.json", getPowerLocation().getPath(), this.ability.name));
            }

            @Override
            public ResourceLocation getAnimationFile() {
                if (this.ability.getJsonObject().has("animation")) {
                    return new ResourceLocation(this.ability.getJsonObject().get("animation").getAsString());
                }
                return new ResourceLocation(getPowerLocation().getNamespace(), "animations/" + getPowerLocation().getPath() + "_" + this.ability.name + ".animation.json");
            }

            private ResourceLocation getPowerLocation() {
                for (String key : this.ability.getAdditionalData().getAllKeys()) {
                    String power = this.ability.getAdditionalData().getString(key);
                    if (power.contains(":")) {
                        return new ResourceLocation(power);
                    }
                }
                return new ResourceLocation(this.ability.getAdditionalData().getString("Superpower"));
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
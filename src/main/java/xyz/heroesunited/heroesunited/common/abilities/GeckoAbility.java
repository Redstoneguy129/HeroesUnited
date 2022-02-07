package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.renderer.GeoAbilityRenderer;
import xyz.heroesunited.heroesunited.client.renderer.IGeoAbility;

import java.util.function.Consumer;

public class GeckoAbility extends JSONAbility implements IGeoAbility {
    protected final AnimationFactory factory = new AnimationFactory(this);

    public GeckoAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            private final GeoAbilityRenderer<GeckoAbility> abilityRenderer = new GeoAbilityRenderer<>(GeckoAbility.this);

            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (getEnabled()) {
                    this.abilityRenderer.setCurrentAbility(player, renderer.getModel());
                    this.abilityRenderer.renderToBuffer(matrix, bufferIn.getBuffer(RenderType.entityTranslucent(abilityRenderer.getTextureLocation(GeckoAbility.this))), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                }
            }

            @Override
            public boolean renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
                if (getEnabled()) {
                    this.abilityRenderer.setCurrentAbility(player, renderer.getModel());
                    this.abilityRenderer.renderFirstPersonArm(renderer, matrix, bufferIn, packedLightIn, side);
                }
                return true;
            }
        });
    }

    public void registerControllers(AnimationData data) {
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getTexture() {
        if (this.getJsonObject().has("texture")) {
            if (GsonHelper.getAsString(this.getJsonObject(), "texture").equals("player")) {
                return Minecraft.getInstance().player.getSkinTextureLocation();
            } else {
                return new ResourceLocation(GsonHelper.getAsString(this.getJsonObject(), "texture"));
            }
        } else
            return new ResourceLocation(getPowerLocation().getNamespace(), "textures/ability/" + getPowerLocation().getPath() + "_" + this.name + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getModelPath() {
        if (this.getJsonObject().has("model")) {
            return new ResourceLocation(this.getJsonObject().get("model").getAsString());
        }
        return new ResourceLocation(getPowerLocation().getNamespace(), String.format("geo/%s_%s.geo.json", getPowerLocation().getPath(), this.name));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getAnimationFile() {
        return new ResourceLocation(getPowerLocation().getNamespace(), "animations/" + getPowerLocation().getPath() + "_" + this.name + ".animation.json");
    }

    protected ResourceLocation getPowerLocation() {
        if (this.getAdditionalData().contains("Suit")) {
            return new ResourceLocation(this.getAdditionalData().getString("Suit"));
        }
        return new ResourceLocation(this.getAdditionalData().getString("Superpower"));
    }
}
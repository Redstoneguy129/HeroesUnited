package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUHideLayerEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.renderer.IPlayerModel;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.PlayerGeoModel;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import javax.annotation.Nullable;
import java.util.Arrays;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow @Nullable protected abstract RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_);
    @Shadow protected abstract boolean isBodyVisible(T p_225622_1_);
    @Shadow protected abstract float getWhiteOverlayProgress(T p_225625_1_, float p_225625_2_);
    @Shadow protected M model;

    @SuppressWarnings("unchecked")
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    public void captureThings(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer) || !(this.model instanceof PlayerModel)) return;
        AbstractClientPlayer player = (AbstractClientPlayer) entity;
        PlayerRenderer renderer = (PlayerRenderer) (Object) this;
        PlayerModel<T> playerModel = (PlayerModel<T>) this.model;
        IPlayerModel iModel = ((IPlayerModel) playerModel);

        boolean flag = !isBodyVisible(entity) && !player.isInvisibleTo(Minecraft.getInstance().player);
        RenderType type = getRenderType(entity, isBodyVisible(entity), flag, Minecraft.getInstance().shouldEntityAppearGlowing(player));
        if (type != null) {
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                cap.getAnimatedModel().getModel(cap.getAnimatedModel().getModelLocation(cap));
                PlayerGeoModel.ModelData modelData = new PlayerGeoModel.ModelData(renderer, iModel.limbSwing(), iModel.limbSwingAmount(), iModel.ageInTicks(), iModel.netHeadYaw(), iModel.headPitch());
                AnimationEvent<IHUPlayer> animationEvent = new AnimationEvent<>(cap, iModel.limbSwing(), iModel.limbSwingAmount(), Minecraft.getInstance().getFrameTime(), false, Arrays.asList(player, modelData, player.getUUID()));
                if (!(Minecraft.getInstance().getOverlay() instanceof LoadingOverlay)) {
                    cap.getAnimatedModel().setLivingAnimations(cap, player.getUUID().hashCode(), animationEvent);
                }
            });

            MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent(player, playerModel, iModel.limbSwing(), iModel.limbSwingAmount(), iModel.ageInTicks(), iModel.netHeadYaw(), iModel.headPitch()));
            HUClientUtil.copyAnglesToWear(playerModel);

            if (MinecraftForge.EVENT_BUS.post(new HUChangeRendererEvent(player, renderer, matrixStack, buffer, buffer.getBuffer(type), light, getOverlayCoords(player, getWhiteOverlayProgress(entity, partialTicks)), iModel.limbSwing(), iModel.limbSwingAmount(), iModel.ageInTicks(), iModel.netHeadYaw(), iModel.headPitch()))) {
                playerModel.setAllVisible(false);
            }
        }
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V"))
    public void addLayer(RenderLayer layerRenderer, PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, Entity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        HUHideLayerEvent event = new HUHideLayerEvent(p_225628_4_);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.getBlockedLayers().contains(layerRenderer.getClass())) {
            layerRenderer.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
        }
    }
}

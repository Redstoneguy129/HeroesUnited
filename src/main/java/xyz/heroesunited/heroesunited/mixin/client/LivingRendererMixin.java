package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import xyz.heroesunited.heroesunited.client.HULayerRenderer;
import xyz.heroesunited.heroesunited.client.events.HideLayerEvent;
import xyz.heroesunited.heroesunited.client.events.RendererChangeEvent;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;
import xyz.heroesunited.heroesunited.client.renderer.IPlayerModel;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.PlayerGeoModel;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow @Nullable protected abstract RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_);
    @Shadow protected abstract boolean isBodyVisible(T p_225622_1_);
    @Shadow protected abstract float getWhiteOverlayProgress(T p_225625_1_, float p_225625_2_);
    @Shadow protected M model;

    @Shadow public abstract boolean addLayer(RenderLayer<T, M> p_115327_);

    @Shadow @Final protected List<RenderLayer<T, M>> layers;

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/EntityModel;F)V", at = @At("TAIL"))
    public void mixinInit(EntityRendererProvider.Context context, M model, float shadowSize, CallbackInfo ci) {
        if (model instanceof HumanoidModel) {
            this.addLayer(new HULayerRenderer(context, (LivingEntityRenderer) (Object) this));
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    public void rendererChange(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayer player) || !(this.model instanceof PlayerModel)) return;
        PlayerRenderer renderer = (PlayerRenderer) (Object) this;
        PlayerModel<T> playerModel = (PlayerModel<T>) this.model;
        IPlayerModel iModel = ((IPlayerModel) playerModel);

        boolean flag = !isBodyVisible(entity) && !player.isInvisibleTo(Minecraft.getInstance().player);
        RenderType type = getRenderType(entity, isBodyVisible(entity), flag, Minecraft.getInstance().shouldEntityAppearGlowing(player));
        if (type != null) {
            for (PlayerPart value : PlayerPart.values()) {
                ((IHUModelPart) (Object) value.modelPart(playerModel)).resetSize();
            }
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                cap.getAnimatedModel().getModel(cap.getAnimatedModel().getModelLocation(cap));
                PlayerGeoModel.ModelData modelData = new PlayerGeoModel.ModelData(renderer, iModel.limbSwing(), iModel.limbSwingAmount(), iModel.ageInTicks(), iModel.netHeadYaw(), iModel.headPitch());
                AnimationEvent<IHUPlayer> animationEvent = new AnimationEvent<>(cap, iModel.limbSwing(), iModel.limbSwingAmount(), Minecraft.getInstance().getFrameTime(), false, Arrays.asList(player, modelData, player.getUUID()));
                if (!(Minecraft.getInstance().getOverlay() instanceof LoadingOverlay)) {
                    cap.getAnimatedModel().setLivingAnimations(cap, player.getUUID().hashCode(), animationEvent);
                }
            });

            MinecraftForge.EVENT_BUS.post(new SetupAnimEvent(player, playerModel, iModel.limbSwing(), iModel.limbSwingAmount(), iModel.ageInTicks(), iModel.netHeadYaw(), iModel.headPitch()));
            HUClientUtil.copyAnglesToWear(playerModel);

            if (MinecraftForge.EVENT_BUS.post(new RendererChangeEvent(player, renderer, matrixStack, buffer, buffer.getBuffer(type), light, getOverlayCoords(player, getWhiteOverlayProgress(entity, partialTicks)), iModel.limbSwing(), iModel.limbSwingAmount(), iModel.ageInTicks(), iModel.netHeadYaw(), iModel.headPitch()))) {
                playerModel.setAllVisible(false);
            }
        }
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<RenderLayer<T, M>> injected(LivingEntityRenderer livingRenderer) {
        if (livingRenderer.getModel() instanceof IPlayerModel model) {
            List<RenderLayer<T, M>> layers = new ArrayList<>(this.layers);
            HideLayerEvent event = new HideLayerEvent(model.livingEntity(), new ArrayList<>(this.layers));
            MinecraftForge.EVENT_BUS.post(event);
            layers.removeIf(layerRenderer -> event.getBlockedLayers().contains(layerRenderer.getClass()));
            return layers;
        }
        return this.layers;
    }
}

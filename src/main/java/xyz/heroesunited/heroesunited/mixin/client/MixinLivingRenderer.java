package xyz.heroesunited.heroesunited.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
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
import java.util.List;

import static net.minecraft.client.renderer.entity.LivingRenderer.getOverlayCoords;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow @Nullable protected abstract RenderType getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_);
    @Shadow protected abstract boolean isBodyVisible(T p_225622_1_);
    @Shadow protected abstract float getWhiteOverlayProgress(T p_225625_1_, float p_225625_2_);
    @Shadow protected M model;

    @Shadow @Final protected List<LayerRenderer<T, M>> layers;

    @SuppressWarnings("unchecked")
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingRenderer;getRenderType(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"))
    public void captureThings(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayerEntity) || !(this.model instanceof PlayerModel)) return;
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
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
                if (!(Minecraft.getInstance().getOverlay() instanceof ResourceLoadProgressGui)) {
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

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingRenderer;layers:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<LayerRenderer<T, M>> injected(LivingRenderer livingRenderer) {
        List<LayerRenderer<T, M>> layers = Lists.newArrayList();
        layers.addAll(this.layers);
        HUHideLayerEvent event = new HUHideLayerEvent(((IPlayerModel) livingRenderer.getModel()).entity());
        MinecraftForge.EVENT_BUS.post(event);
        layers.removeIf(layerRenderer -> event.getBlockedLayers().contains(layerRenderer.getClass()));
        return layers;
    }
}

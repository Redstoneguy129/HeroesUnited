package xyz.heroesunited.heroesunited.util.compat;

import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.PlayerGeoModel;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import java.util.Arrays;

public class ObfuscateHandler {

    @SubscribeEvent
    public void renderPre(PlayerModelEvent.Render.Pre event) {
        if (event.getPlayer() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();
            PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                cap.getAnimatedModel().getModel(cap.getAnimatedModel().getModelLocation(cap));
                PlayerGeoModel.ModelData modelData = new PlayerGeoModel.ModelData(renderer, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getHeadPitch(), event.getNetHeadYaw());
                AnimationEvent<IHUPlayer> animationEvent = new AnimationEvent<>(cap, event.getLimbSwing(), event.getLimbSwingAmount(), Minecraft.getInstance().getFrameTime(), false, Arrays.asList(player, modelData, player.getUUID()));
                if (!(Minecraft.getInstance().getOverlay() instanceof ResourceLoadProgressGui)) {
                    cap.getAnimatedModel().setLivingAnimations(cap, player.getUUID().hashCode(), animationEvent);
                }
            });

            MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent(player, event.getModelPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch()));
            HUClientUtil.copyAnglesToWear(event.getModelPlayer());

            event.setCanceled(MinecraftForge.EVENT_BUS.post(new HUChangeRendererEvent(player, renderer, event.getMatrixStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getBuilder(), event.getLight(), event.getOverlay(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch())));
        }
    }
}

package xyz.heroesunited.heroesunited.util.compat;

import com.mrcrayfish.obfuscate.client.event.PlayerModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

public class ObfuscateHandler {

    @SubscribeEvent
    public void renderPre(PlayerModelEvent.Render.Pre event) {
        if (event.getPlayer() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getPlayer();
            MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent(player, event.getModelPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch()));
            HUClientUtil.copyAnglesToWear(event.getModelPlayer());

            PlayerRenderer playerrenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            event.setCanceled(MinecraftForge.EVENT_BUS.post(new HUChangeRendererEvent(player, playerrenderer, event.getMatrixStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getBuilder(), event.getLight(), event.getOverlay(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch())));
        }
    }
}

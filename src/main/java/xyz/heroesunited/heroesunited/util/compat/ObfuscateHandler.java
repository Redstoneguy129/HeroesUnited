package xyz.heroesunited.heroesunited.util.compat;

/** @TODO Obfuscate compat */
public class ObfuscateHandler {
/*
    @SubscribeEvent
    public void renderPre(PlayerModelEvent.Render.Pre event) {
        if (event.getPlayer() instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) event.getPlayer();
            MinecraftForge.EVENT_BUS.post(new HUSetRotationAnglesEvent(player, event.getModelPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch()));
            HUClientUtil.copyAnglesToWear(event.getModelPlayer());

            PlayerRenderer playerrenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            event.setCanceled(MinecraftForge.EVENT_BUS.post(new HUChangeRendererEvent(player, playerrenderer, event.getMatrixStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getBuilder(), event.getLight(), event.getOverlay(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch())));
        }
    }
*/
}

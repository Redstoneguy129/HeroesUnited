package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired before the entity model is rendered.
 * Cancelling this event will prevent the entity model from being rendered.
 * <p>
 * This event is suitable for any additional renders you want to apply to the entity,
 * or to render a model other than the entity.
 */
@Cancelable
public class RendererChangeEvent extends PlayerEvent {

    private final PlayerRenderer renderer;
    private final PoseStack stack;
    private final MultiBufferSource buffers;
    private final VertexConsumer builder;
    private final int light, overlay;
    private final float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks;

    public RendererChangeEvent(AbstractClientPlayer playerEntity, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource buffers, VertexConsumer builder, int light, int overlay, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super(playerEntity);
        this.renderer = renderer;
        this.stack = poseStack;
        this.buffers = buffers;
        this.light = light;
        this.builder = builder;
        this.overlay = overlay;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.partialTicks = Minecraft.getInstance().getFrameTime();
    }

    @Override
    public AbstractClientPlayer getPlayer() {
        return (AbstractClientPlayer) super.getPlayer();
    }

    public PlayerRenderer getRenderer() {
        return renderer;
    }

    public PoseStack getPoseStack() {
        return stack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return buffers;
    }

    public VertexConsumer getBuilder() {
        return builder;
    }

    public int getPackedLight() {
        return light;
    }

    public int getOverlay() {
        return overlay;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public float getAgeInTicks() {
        return this.ageInTicks;
    }

    public float getNetHeadYaw() {
        return this.netHeadYaw;
    }

    public float getHeadPitch() {
        return this.headPitch;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}

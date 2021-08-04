package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired before the entity model is rendered.
 * Cancelling this event will prevent the entity model from being rendered.
 *
 * This event is suitable for any additional renders you want to apply to the entity,
 * or to render a model other than the entity.
 */
@Cancelable
public class HUChangeRendererEvent extends PlayerEvent {

    private final PlayerEntityRenderer renderer;
    private final MatrixStack stack;
    private final VertexConsumerProvider buffers;
    private final VertexConsumer builder;
    private final int light, overlay;
    private final float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks;

    public HUChangeRendererEvent(AbstractClientPlayerEntity playerEntity, PlayerEntityRenderer renderer, MatrixStack stack, VertexConsumerProvider buffers, VertexConsumer builder, int light, int overlay, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super(playerEntity);
        this.renderer = renderer;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
        this.builder = builder;
        this.overlay = overlay;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.partialTicks = MinecraftClient.getInstance().getTickDelta();
    }

    @Override
    public AbstractClientPlayerEntity getPlayer() {
        return (AbstractClientPlayerEntity) super.getPlayer();
    }

    public PlayerEntityRenderer getRenderer() {
        return renderer;
    }

    public MatrixStack getMatrixStack() {
        return stack;
    }

    public VertexConsumerProvider getBuffers() {
        return buffers;
    }

    public VertexConsumer getBuilder() {
        return builder;
    }

    public int getLight() {
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

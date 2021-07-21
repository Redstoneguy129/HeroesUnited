package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
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

    private final PlayerRenderer renderer;
    private final MatrixStack stack;
    private final IRenderTypeBuffer buffers;
    private final IVertexBuilder builder;
    private final int light, overlay;
    private final float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks;

    public HUChangeRendererEvent(AbstractClientPlayerEntity playerEntity, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, IVertexBuilder builder, int light, int overlay, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        this.partialTicks = Minecraft.getInstance().getFrameTime();
    }

    @Override
    public AbstractClientPlayerEntity getPlayer() {
        return (AbstractClientPlayerEntity) super.getPlayer();
    }

    public PlayerRenderer getRenderer() {
        return renderer;
    }

    public MatrixStack getMatrixStack() {
        return stack;
    }

    public IRenderTypeBuffer getBuffers() {
        return buffers;
    }

    public IVertexBuilder getBuilder() {
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

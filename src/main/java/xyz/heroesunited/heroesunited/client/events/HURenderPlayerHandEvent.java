package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when player hand should be rendered.
 * Can be used to create own hand renderer like Alien, suits and etc.
 */
public abstract class HURenderPlayerHandEvent extends PlayerEvent {

    private final PlayerEntityRenderer renderer;
    private final Arm side;
    private final MatrixStack stack;
    private final VertexConsumerProvider buffers;
    private final int light;
    private final AbstractClientPlayerEntity player;

    public HURenderPlayerHandEvent(AbstractClientPlayerEntity player, PlayerEntityRenderer renderer, MatrixStack stack, VertexConsumerProvider buffers, int light, Arm side) {
        super(player);
        this.player = player;
        this.renderer = renderer;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
        this.side = side;
    }

    public PlayerEntityRenderer getRenderer() {
        return renderer;
    }

    public AbstractClientPlayerEntity getPlayer() {
        return player;
    }

    public Arm getSide() {
        return side;
    }

    public MatrixStack getMatrixStack() {
        return stack;
    }

    public VertexConsumerProvider getBuffers() {
        return buffers;
    }

    public int getLight() {
        return light;
    }

    /**
     * Fired before hand should be rendered.
     * Canceling the event causes the hand to not render.
     */
    @Cancelable
    public static class Pre extends HURenderPlayerHandEvent {
        public Pre(AbstractClientPlayerEntity player, PlayerEntityRenderer renderer, MatrixStack stack, VertexConsumerProvider buffers, int light, Arm side) {
            super(player, renderer, stack, buffers, light, side);
        }
    }

    /**
     * Fired after hand rendering.
     * Can be used to render own hand.
     */
    public static class Post extends HURenderPlayerHandEvent {
        public Post(AbstractClientPlayerEntity player, PlayerEntityRenderer renderer, MatrixStack stack, VertexConsumerProvider buffers, int light, Arm side) {
            super(player, renderer, stack, buffers, light, side);
        }
    }
}

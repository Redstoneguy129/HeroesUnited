package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.HandSide;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when player hand should be rendered.
 * Can be used to create own hand renderer like Alien, suits and etc.
 */
public abstract class HURenderPlayerHandEvent extends PlayerEvent {

    private final PlayerRenderer renderer;
    private final HandSide side;
    private final MatrixStack stack;
    private final IRenderTypeBuffer buffers;
    private final int light;
    private final AbstractClientPlayerEntity player;

    public HURenderPlayerHandEvent(AbstractClientPlayerEntity player, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, int light, HandSide side) {
        super(player);
        this.player = player;
        this.renderer = renderer;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
        this.side = side;
    }

    public PlayerRenderer getRenderer() {
        return renderer;
    }

    public AbstractClientPlayerEntity getPlayer() {
        return player;
    }

    public HandSide getSide() {
        return side;
    }

    public MatrixStack getMatrixStack() {
        return stack;
    }

    public IRenderTypeBuffer getBuffers() {
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
        public Pre(AbstractClientPlayerEntity player, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, int light, HandSide side) {
            super(player, renderer, stack, buffers, light, side);
        }
    }

    /**
     * Fired after hand rendering.
     * Can be used to render own hand.
     */
    public static class Post extends HURenderPlayerHandEvent {
        public Post(AbstractClientPlayerEntity player, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, int light, HandSide side) {
            super(player, renderer, stack, buffers, light, side);
        }
    }
}

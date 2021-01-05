package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/*
 * Use this to render Alien hand and others
 */
public abstract class HURenderPlayerHandEvent extends PlayerEvent {

    private final PlayerRenderer renderer;
    private final HandSide side;
    private final MatrixStack stack;
    private final IRenderTypeBuffer buffers;
    private final int light;

    public HURenderPlayerHandEvent(PlayerEntity player, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, int light, HandSide side) {
        super(player);
        this.renderer = renderer;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
        this.side = side;
    }

    public PlayerRenderer getRenderer() {
        return renderer;
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

    @Cancelable
    public static class Pre extends HURenderPlayerHandEvent {
        public Pre(PlayerEntity player, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, int light, HandSide side) {
            super(player, renderer, stack, buffers, light, side);
        }
    }

    public static class Post extends HURenderPlayerHandEvent {
        public Post(PlayerEntity player, PlayerRenderer renderer, MatrixStack stack, IRenderTypeBuffer buffers, int light, HandSide side) {
            super(player, renderer, stack, buffers, light, side);
        }
    }
}

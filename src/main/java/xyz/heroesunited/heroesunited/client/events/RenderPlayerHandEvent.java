package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when player hand should be rendered.
 * Can be used to create own hand renderer like Alien, suits and etc.
 */
public abstract class RenderPlayerHandEvent extends PlayerEvent {

    private final PlayerRenderer renderer;
    private final HumanoidArm side;
    private final PoseStack stack;
    private final MultiBufferSource buffers;
    private final int light;
    private final AbstractClientPlayer player;

    public RenderPlayerHandEvent(AbstractClientPlayer player, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource buffers, int light, HumanoidArm side) {
        super(player);
        this.player = player;
        this.renderer = renderer;
        this.stack = poseStack;
        this.buffers = buffers;
        this.light = light;
        this.side = side;
    }

    public PlayerRenderer getRenderer() {
        return renderer;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public HumanoidArm getSide() {
        return side;
    }

    public PoseStack getPoseStack() {
        return stack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return buffers;
    }

    public int getPackedLight() {
        return light;
    }

    /**
     * Fired before hand should be rendered.
     * Canceling the event causes the hand to not render.
     */
    @Cancelable
    public static class Pre extends RenderPlayerHandEvent {
        public Pre(AbstractClientPlayer player, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource buffers, int light, HumanoidArm side) {
            super(player, renderer, poseStack, buffers, light, side);
        }
    }

    /**
     * Fired after hand rendering.
     * Can be used to render own hand.
     */
    public static class Post extends RenderPlayerHandEvent {
        public Post(AbstractClientPlayer player, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource buffers, int light, HumanoidArm side) {
            super(player, renderer, poseStack, buffers, light, side);
        }
    }
}

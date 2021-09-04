package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * This event is called before the shadow should be rendered
 * Can be used to change entity shadow size. (Seems logic i think)
 */
public class HUChangeShadowSizeEvent extends EntityEvent {

    private final PoseStack matrixStack;
    private final MultiBufferSource bufferIn;
    private final float partialTicks;
    private final float darkness;
    private final LevelReader world;
    private final float defaultSize;
    private float size;

    public HUChangeShadowSizeEvent(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, Entity entity, float darkness, float partialTicks, LevelReader world, float size) {
        super(entity);
        this.defaultSize = size;
        this.size = defaultSize;
        this.matrixStack = matrixStack;
        this.bufferIn = renderTypeBuffer;
        this.darkness = darkness;
        this.partialTicks = partialTicks;
        this.world = world;
    }

    public PoseStack getMatrixStack() {
        return matrixStack;
    }

    public float getDarkness() {
        return darkness;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public MultiBufferSource getBufferIn() {
        return bufferIn;
    }

    public LevelReader getWorld() {
        return world;
    }

    public float getDefaultSize() {
        return defaultSize;
    }

    public void setNewSize(float value) {
        this.size = value;
    }

    public float getSize() {
        return size;
    }
}

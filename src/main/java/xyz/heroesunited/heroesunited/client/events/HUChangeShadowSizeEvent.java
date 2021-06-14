package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * This event is called before the shadow should be rendered
 * Can be used to change entity shadow size. (Seems logic i think)
 */
public class HUChangeShadowSizeEvent extends EntityEvent {

    private final MatrixStack matrixStack;
    private final IRenderTypeBuffer bufferIn;
    private final float partialTicks;
    private final float darkness;
    private final IWorldReader world;
    private final float defaultSize;
    private float size;

    public HUChangeShadowSizeEvent(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, Entity entity, float darkness, float partialTicks, IWorldReader world, float size) {
        super(entity);
        this.defaultSize = size;
        this.size = defaultSize;
        this.matrixStack = matrixStack;
        this.bufferIn = renderTypeBuffer;
        this.darkness = darkness;
        this.partialTicks = partialTicks;
        this.world = world;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public float getDarkness() {
        return darkness;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public IRenderTypeBuffer getBufferIn() {
        return bufferIn;
    }

    public IWorldReader getWorld() {
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

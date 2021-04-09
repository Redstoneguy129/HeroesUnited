package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class HURenderLivingEvent extends LivingEvent {

    private final LivingRenderer renderer;
    private final float partialTicks;
    private final float entityYaw;
    private final MatrixStack stack;
    private final IRenderTypeBuffer buffers;
    private final int light;

    public HURenderLivingEvent(LivingEntity entity, LivingRenderer renderer, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffers, int light) {
        super(entity);
        this.renderer = renderer;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
    }

    public LivingRenderer getRenderer() {
        return renderer;
    }

    public float getEntityYaw() {
        return entityYaw;
    }

    public float getPartialTicks() {
        return partialTicks;
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
}

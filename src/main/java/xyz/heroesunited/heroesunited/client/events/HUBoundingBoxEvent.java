package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingEvent;

/*
This is run so that you can pass a custom box in while boxes are being made.
 */
public class HUBoundingBoxEvent extends LivingEvent {

    private final AxisAlignedBB axisAlignedBB;

    public HUBoundingBoxEvent(LivingEntity entity, AxisAlignedBB axisAlignedBB) {
        super(entity);
        this.axisAlignedBB = axisAlignedBB;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.axisAlignedBB;
    }

    public void setBoundingBox(AxisAlignedBB axisAlignedBB) {
        this.getEntityLiving().setBoundingBox(axisAlignedBB);
    }
}

package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when a block and entity collide.
 * U can cancel it to make intangible entities
 */
@Cancelable
public class HUCancelBlockCollision extends BlockEvent {

    private final Entity entity;

    public HUCancelBlockCollision(IWorld world, BlockPos pos, BlockState state, Entity entity) {
        super(world, pos, state);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}

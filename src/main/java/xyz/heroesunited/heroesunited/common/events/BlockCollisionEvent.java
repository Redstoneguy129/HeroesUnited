package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when a block and entity collide.
 * U can cancel it to make intangible entities
 */
@Cancelable
public class BlockCollisionEvent extends BlockEvent {

    private final Entity entity;

    public BlockCollisionEvent(LevelAccessor world, BlockPos pos, BlockState state, Entity entity) {
        super(world, pos, state);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}

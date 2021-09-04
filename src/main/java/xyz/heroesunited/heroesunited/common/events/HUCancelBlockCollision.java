package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Fired when a block and entity collide.
 * U can cancel it to make intangible entities
 */
public class HUCancelBlockCollision extends BlockEvent {

    private Entity entity;

    public HUCancelBlockCollision(LevelAccessor world, BlockPos pos, BlockState state, Entity entity) {
        super(world, pos, state);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}

package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class HUCancelSprinting extends EntityEvent {

    public HUCancelSprinting(Entity entity) {
        super(entity);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}

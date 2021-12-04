package xyz.heroesunited.heroesunited.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.heroesunited.heroesunited.client.events.BlockLightEvent;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {

    /**
     * @author grillo78
     * @reason
     */
    @Overwrite
    default int getLightEmission(BlockPos pos) {
        BlockGetter iBlockReader = ((BlockGetter) this);
        int lightEmission = iBlockReader.getBlockState(pos).getLightEmission(iBlockReader, pos);
        BlockLightEvent event = new BlockLightEvent(lightEmission, pos, iBlockReader);
        MinecraftForge.EVENT_BUS.post(event);
        if (lightEmission != event.getValue()) {
            return event.getValue();
        }
        return lightEmission;
    }
}
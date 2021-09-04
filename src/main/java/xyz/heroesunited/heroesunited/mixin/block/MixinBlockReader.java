package xyz.heroesunited.heroesunited.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.heroesunited.heroesunited.client.events.HUChangeBlockLightEvent;

@Mixin(BlockGetter.class)
public interface MixinBlockReader {

    /**
     * @author grillo78
     * @reason because can
     */
    @Overwrite
    default int getLightEmission(BlockPos pos) {
        BlockGetter iBlockReader = ((BlockGetter) this);
        HUChangeBlockLightEvent event = new HUChangeBlockLightEvent(iBlockReader.getBlockState(pos).getLightEmission(iBlockReader, pos), pos, iBlockReader);
        event.setNewValue(event.getDefaultValue());
        MinecraftForge.EVENT_BUS.post(event);
        return event.getValue();
    }
}
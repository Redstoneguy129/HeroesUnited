package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.heroesunited.heroesunited.client.events.HUChangeBlockLightEvent;

@Mixin(BlockView.class)
public interface MixinIBlockReader {

    /**
     * @author grillo78
     * @reason because can
     */
    @Overwrite
    default int getLuminance(BlockPos pos) {
        BlockView iBlockReader = ((BlockView) this);
        HUChangeBlockLightEvent event = new HUChangeBlockLightEvent(iBlockReader.getBlockState(pos).getLuminance(), pos, iBlockReader);
        event.setNewValue(event.getDefaultValue());
        MinecraftForge.EVENT_BUS.post(event);
        return event.getValue();
    }
}
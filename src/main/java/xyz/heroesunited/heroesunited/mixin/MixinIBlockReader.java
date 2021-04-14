package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.heroesunited.heroesunited.client.events.HUChangeBlockLightEvent;

@Mixin(IBlockReader.class)
public interface MixinIBlockReader {

    /**
     * @author grillo78
     */
    @Overwrite
    default int getLightEmission(BlockPos pos) {
        IBlockReader iBlockReader = ((IBlockReader) this);
        HUChangeBlockLightEvent event = new HUChangeBlockLightEvent(iBlockReader.getBlockState(pos).getLightValue(iBlockReader, pos), pos, iBlockReader);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getValue();
    }
}
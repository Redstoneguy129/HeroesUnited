package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.HUChangeBlockLightEvent;

@Mixin(IBlockReader.class)
public interface MixinIBlockReader {

    @Inject(at = @At("RETURN"), method = "getLightEmission(Lnet/minecraft/util/math/BlockPos;)I", cancellable = true)
    default void getLightEmission(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        HUChangeBlockLightEvent event = new HUChangeBlockLightEvent(cir.getReturnValue(), pos, (IBlockReader) this);
        MinecraftForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.getValue());
    }
}
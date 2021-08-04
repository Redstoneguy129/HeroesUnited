package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xyz.heroesunited.heroesunited.common.events.HUSetTimeOfDay;

@Mixin(DimensionType.class)
public class MixinDimensionType {

    @ModifyConstant(method = "getSkyAngle(J)F", constant = @Constant(doubleValue = 24000.0D))
    private double modifyDayTime(double dayTime) {
        HUSetTimeOfDay event = new HUSetTimeOfDay((DimensionType) (Object) this, 24000.0D);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getValue();
    }
}

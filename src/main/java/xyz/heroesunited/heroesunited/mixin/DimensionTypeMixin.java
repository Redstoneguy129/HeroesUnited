package xyz.heroesunited.heroesunited.mixin;

import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xyz.heroesunited.heroesunited.common.events.LengthOfDayEvent;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {

    @ModifyConstant(method = "timeOfDay", constant = @Constant(doubleValue = 24000.0D))
    private double modifyDayTime(double dayTime) {
        LengthOfDayEvent event = new LengthOfDayEvent((DimensionType) (Object) this, 24000.0D);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getTime();
    }
}

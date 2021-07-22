package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xyz.heroesunited.heroesunited.client.events.HUChangeLightEvent;

@Mixin(LightTexture.class)
public class MixinLightTexture {

    @ModifyConstant(method = "updateLightTexture(F)V", constant = @Constant(floatValue = 0.0F, ordinal = 1))
    private float getNightVision(float value) {
        HUChangeLightEvent event = new HUChangeLightEvent(value);
        event.setNewValue(event.getDefaultValue());
        MinecraftForge.EVENT_BUS.post(event);
        return event.getValue();
    }

}
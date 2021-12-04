package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.client.events.ClientGlowingEvent;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(at = @At("RETURN"), method = "shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z", cancellable = true)
    public void onShouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> ci) {
        ClientGlowingEvent event = new ClientGlowingEvent(entity);
        MinecraftForge.EVENT_BUS.post(event);
        if (!ci.getReturnValue()) {
            ci.setReturnValue(event.shouldGlow());
        }
    }
}

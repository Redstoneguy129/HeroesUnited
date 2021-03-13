package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.storage.ServerWorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorldInfo.class)
public class MixinServerWorldInfo {

    @Inject(at = @At("HEAD"), method = "worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;", cancellable = true)
    private void getLifecycle(CallbackInfoReturnable<Lifecycle> cir) {
        cir.setReturnValue(Lifecycle.stable());
    }
}
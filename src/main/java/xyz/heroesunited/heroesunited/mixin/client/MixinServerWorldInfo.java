package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelProperties.class)
public class MixinServerWorldInfo {

    @Inject(at = @At("HEAD"), method = "getLifecycle()Lcom/mojang/serialization/Lifecycle;", cancellable = true)
    private void getLifecycle(CallbackInfoReturnable<Lifecycle> cir) {
        cir.setReturnValue(Lifecycle.stable());
    }
}
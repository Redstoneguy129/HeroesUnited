package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(KeyBinding.class)
public interface InvokerKeyBinding {
    @Invoker("release")
    void releaseKey();
}

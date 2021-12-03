package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(KeyMapping.class)
public interface InvokerKeyBinding {
    @Invoker("release")
    void releaseKey();
}

package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(KeyBinding.class)
public interface InvokerKeyBinding {
    @Invoker("release")
    void releaseKey();
}

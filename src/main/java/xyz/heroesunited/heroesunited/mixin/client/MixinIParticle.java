package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(Particle.class)
public interface MixinIParticle {
    @Accessor
    ClientWorld getLevel();
}

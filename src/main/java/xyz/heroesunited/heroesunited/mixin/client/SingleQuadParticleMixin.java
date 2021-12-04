package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.heroesunited.heroesunited.HeroesUnited;

@Mixin(SingleQuadParticle.class)
public abstract class SingleQuadParticleMixin extends Particle {

    protected SingleQuadParticleMixin(ClientLevel p_i232411_1_, double p_i232411_2_, double p_i232411_4_, double p_i232411_6_) {
        super(p_i232411_1_, p_i232411_2_, p_i232411_4_, p_i232411_6_);
    }

    @ModifyVariable(method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V", at = @At("STORE"), ordinal = 4)
    private float injected(float size) {
        if (this.level.dimension().equals(HeroesUnited.SPACE)) {
            return size * 0.01F;
        }
        return size;
    }
}

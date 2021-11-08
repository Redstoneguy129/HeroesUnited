package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.heroesunited.heroesunited.HeroesUnited;

@Mixin(TexturedParticle.class)
public abstract class MixinTexturedParticle extends Particle {

    protected MixinTexturedParticle(ClientWorld p_i232411_1_, double p_i232411_2_, double p_i232411_4_, double p_i232411_6_) {
        super(p_i232411_1_, p_i232411_2_, p_i232411_4_, p_i232411_6_);
    }

    @ModifyVariable(method = "render(Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V", at = @At("STORE"), ordinal = 4)
    private float injected(float size) {
        if (this.level.dimension().equals(HeroesUnited.SPACE)) {
            return size * 0.01F;
        }
        return size;
    }
}

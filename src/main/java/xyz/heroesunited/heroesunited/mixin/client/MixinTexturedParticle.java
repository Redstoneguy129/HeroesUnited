package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.heroesunited.heroesunited.HeroesUnited;

@Mixin(TexturedParticle.class)
public abstract class MixinTexturedParticle extends Particle {

    protected MixinTexturedParticle(ClientWorld p_i232411_1_, double p_i232411_2_, double p_i232411_4_, double p_i232411_6_) {
        super(p_i232411_1_, p_i232411_2_, p_i232411_4_, p_i232411_6_);
    }

    @Shadow public abstract float getQuadSize(float p_217561_1_);

    @Redirect(method = "render(Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/client/renderer/ActiveRenderInfo;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/TexturedParticle;getQuadSize(F)F"))
    private float changeQuadSize(TexturedParticle texturedParticle, float partialTicks) {
        if (this.level.dimension().equals(HeroesUnited.SPACE)) {
            return this.getQuadSize(partialTicks) *0.01F;
        }
        return this.getQuadSize(partialTicks);
    }
}

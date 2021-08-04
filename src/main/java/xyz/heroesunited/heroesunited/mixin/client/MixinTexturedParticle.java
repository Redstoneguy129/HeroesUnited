package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.particle.BillboardParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.heroesunited.heroesunited.HeroesUnited;

@Mixin(BillboardParticle.class)
public abstract class MixinTexturedParticle {

    @Shadow public abstract float getSize(float tickDelta);

    @Redirect(method = "buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BillboardParticle;getSize(F)F"))
    private float changeSize(BillboardParticle texturedParticle, float partialTicks) {
        if (((AccessorParticle) this).getLevel().getRegistryKey().equals(HeroesUnited.SPACE)) {
            return this.getSize(partialTicks) *0.01F;
        }
        return this.getSize(partialTicks);
    }
}

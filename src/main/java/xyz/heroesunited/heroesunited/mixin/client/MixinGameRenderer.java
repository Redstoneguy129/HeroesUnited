package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

import static xyz.heroesunited.heroesunited.HeroesUnited.MODID;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow protected abstract void loadShader(Identifier id);

    @Inject(method = "onCameraEntitySet(Lnet/minecraft/entity/Entity;)V", at = @At(value = "TAIL"))
    public void onCameraEntitySet(Entity entity, CallbackInfo ci) {
        if (entity instanceof Horas) {
            loadShader(new Identifier(MODID, "shaders/post/horas.json"));
        }
    }
}

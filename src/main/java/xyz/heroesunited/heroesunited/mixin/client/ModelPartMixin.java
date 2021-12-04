package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.render.renderer.IHUModelPart;

@Mixin(ModelPart.class)
public class ModelPartMixin implements IHUModelPart {

    private CubeDeformation size = new CubeDeformation(1);

    @Inject(method = "translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"))
    public void render(PoseStack poseStack, CallbackInfo ci) {
        poseStack.scale(1 + size.growX * 0.0625F, 1 + size.growY * 0.0625F, 1 + size.growZ * 0.0625F);
        resetSize();
    }

    @Inject(method = "copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("TAIL"))
    public void copyFrom(ModelPart part, CallbackInfo ci) {
        this.size = ((IHUModelPart) (Object) part).size();
    }

    @Override
    public void resetSize() {
        this.setSize(CubeDeformation.NONE);
    }

    @Override
    public void setSize(CubeDeformation size) {
        this.size = size;
    }

    @Override
    public CubeDeformation size() {
        return size;
    }
}
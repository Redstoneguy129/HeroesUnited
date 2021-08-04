package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.util.IHUModelRenderer;

@Mixin(ModelPart.class)
public class MixinModelRenderer implements IHUModelRenderer {

    private Vec3f size = new Vec3f(1f, 1f, 1f);

    @Inject(method = "rotate(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("HEAD"))
    public void render(MatrixStack matrixStack, CallbackInfo ci) {
        matrixStack.scale(size.getX(), size.getY(), size.getZ());
    }

    @Inject(method = "copyTransform(Lnet/minecraft/client/model/ModelPart;)V", at = @At("TAIL"))
    public void copyFrom(ModelPart modelRenderer, CallbackInfo ci) {
        this.size = ((IHUModelRenderer) (Object) modelRenderer).getSize();
    }

    @Override
    public void setSize(Vec3f size) {
        this.size = size;
    }

    @Override
    public Vec3f getSize() {
        return size;
    }
}
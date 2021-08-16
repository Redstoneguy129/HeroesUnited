package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.util.IHUModelRenderer;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer implements IHUModelRenderer {

    private Vector3f size = new Vector3f(1f, 1f, 1f);

    @Inject(method = "translateAndRotate(Lcom/mojang/blaze3d/matrix/MatrixStack;)V", at = @At("HEAD"))
    public void render(MatrixStack matrixStack, CallbackInfo ci) {
        matrixStack.scale(size.x(), size.y(), size.z());
        setSize(new Vector3f(1f, 1f, 1f));
    }

    @Inject(method = "copyFrom(Lnet/minecraft/client/renderer/model/ModelRenderer;)V", at = @At("TAIL"))
    public void copyFrom(ModelRenderer modelRenderer, CallbackInfo ci) {
        this.size = ((IHUModelRenderer) modelRenderer).getSize();
    }

    @Override
    public void setSize(Vector3f size) {
        this.size = size;
    }

    @Override
    public Vector3f getSize() {
        return size;
    }
}
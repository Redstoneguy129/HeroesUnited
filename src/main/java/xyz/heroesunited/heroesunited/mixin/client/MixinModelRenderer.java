package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.util.IHUModelRenderer;

@Mixin(ModelPart.class)
public class MixinModelRenderer implements IHUModelRenderer {

    private Vector3f size = new Vector3f(1f, 1f, 1f);

    @Inject(method = "translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"))
    public void render(PoseStack matrixStack, CallbackInfo ci) {
        matrixStack.scale(size.x(), size.y(), size.z());
        setSize(new Vector3f(1f, 1f, 1f));
    }

    @Inject(method = "copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("TAIL"))
    public void copyFrom(ModelPart modelRenderer, CallbackInfo ci) {
        this.size = ((IHUModelRenderer) (Object) modelRenderer).getSize();
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
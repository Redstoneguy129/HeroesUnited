package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.util.HUPartSize;

import java.util.List;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements HUPartSize {

    @Shadow public abstract void setRotation(float pXRot, float pYRot, float pZRot);

    @Shadow public abstract void setPos(float pX, float pY, float pZ);

    @Shadow @Final public List<ModelPart.Cube> cubes;

    private boolean changedScale = false;

    @Inject(method = "copyFrom", at = @At("HEAD"), cancellable = true)
    public void inject(ModelPart pModelPart, CallbackInfo ci) {
        if (changedScale) {
            ci.cancel();
            this.setRotation(pModelPart.xRot, pModelPart.yRot, pModelPart.zRot);
            this.setPos(pModelPart.x, pModelPart.y, pModelPart.z);
            this.changedScale = false;
        }
    }

    @Override
    public void changedScale() {
        this.changedScale = true;
    }

    private Vector3f size = new Vector3f(0);

    @Override
    public void setSize(Vector3f size) {
        this.size = size;
        for (ModelPart.Cube cube : this.cubes) {
            ((HUPartSize) cube).setSize(this.size);
        }
    }

    @Override
    public Vector3f size() {
        return size;
    }
}

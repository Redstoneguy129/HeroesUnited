package xyz.heroesunited.heroesunited.mixin.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;
import xyz.heroesunited.heroesunited.client.renderer.OldPartRenderer;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements IHUModelPart {

    @Mutable @Shadow @Final private Map<String, ModelPart> children;
    @Mutable @Shadow @Final private List<ModelPart.Cube> cubes;

    private CubeDeformation size = CubeDeformation.NONE;

    @Inject(method = "translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("TAIL"))
    public void render(PoseStack poseStack, CallbackInfo ci) {
        poseStack.translate(size.growX * -0.001F, size.growY * -0.001F, size.growZ * -0.001F);
        poseStack.scale(1.0F + size.growX / 16.0F, 1.0F + size.growY / 16.0F, 1.0F + size.growZ / 16.0F);
        this.resetSize();
    }

    @Inject(method = "copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("TAIL"))
    public void copyFrom(ModelPart part, CallbackInfo ci) {
        CubeDeformation size = ((IHUModelPart) (Object) part).size();
        this.size.extend(size.growX, size.growY, size.growZ);
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

    @Override
    public void copyFrom(OldPartRenderer renderer) {
        ModelPart modelPart = (ModelPart) (Object) this;
        Map<String, ModelPart> children = Maps.newHashMap();
        List<OldPartRenderer> modelRenderers = renderer.children;
        for (int i = 0; i < modelRenderers.size(); i++) {
            String name = "dummy" + i;
            children.put(name, modelRenderers.get(i).bake(name));
        }
        modelPart.setRotation(renderer.xRot, renderer.yRot, renderer.zRot);
        modelPart.setPos(renderer.x, renderer.y, renderer.z);
        this.cubes = renderer.cubes;
        this.children = children;
    }
}
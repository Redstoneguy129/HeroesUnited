package xyz.heroesunited.heroesunited.mixin.client;

import com.google.common.collect.Maps;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements IHUModelPart {

    @Mutable @Shadow @Final private Map<String, ModelPart> children;
    @Mutable @Shadow @Final private List<ModelPart.Cube> cubes;

    private CubeDeformation size = CubeDeformation.NONE;

    @Override
    public void resetSize() {
        this.size = CubeDeformation.NONE;
        for (ModelPart.Cube cube : this.cubes) {
            ((IHUModelPart) cube).setSize(this.size);
        }
    }

    @Override
    public void setSize(CubeDeformation size) {
        this.size = this.size.extend(size.growX, size.growY, size.growZ);
        for (ModelPart.Cube cube : this.cubes) {
            ((IHUModelPart) cube).setSize(this.size);
        }
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
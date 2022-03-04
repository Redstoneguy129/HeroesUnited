package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;

import java.util.List;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements IHUModelPart {

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
}
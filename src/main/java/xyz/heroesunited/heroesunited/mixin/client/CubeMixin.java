package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;

@Mixin(ModelPart.Cube.class)
public class CubeMixin implements IHUModelPart {

    @Mutable @Shadow @Final private ModelPart.Polygon[] polygons;
    @Mutable @Shadow @Final public float minX;
    @Mutable @Shadow @Final public float minY;
    @Mutable @Shadow @Final public float minZ;
    @Shadow @Final public float maxX;
    @Shadow @Final public float maxY;
    @Shadow @Final public float maxZ;
    public int u, v;
    public float expandX, expandY, expandZ;
    public float texWidth, texHeight;
    public float dimensionX, dimensionY, dimensionZ;
    public boolean mirror;
    public CubeDeformation size;

    @Inject(at = @At("TAIL"), method = "<init>(IIFFFFFFFFFZFF)V")
    public void mixinInit(int p_104343_, int p_104344_, float p_104345_, float p_104346_, float p_104347_, float p_104348_, float p_104349_, float p_104350_, float p_104351_, float p_104352_, float p_104353_, boolean p_104354_, float p_104355_, float p_104356_, CallbackInfo ci) {
        this.u = p_104343_;
        this.v = p_104344_;
        this.expandX = p_104351_;
        this.expandY = p_104352_;
        this.expandZ = p_104353_;
        this.dimensionX = p_104348_;
        this.dimensionY = p_104349_;
        this.dimensionZ = p_104350_;
        this.mirror = p_104354_;
        this.texWidth = p_104355_;
        this.texHeight = p_104356_;
    }

    @Override
    public void setSize(CubeDeformation size) {
        if (this.size == size) return;
        this.size = size;

        float growX = size.growX + expandX;
        float growY = size.growY + expandY;
        float growZ = size.growZ + expandZ;

        float originX = this.minX - growX;
        float originY = this.minY - growY;
        float originZ = this.minZ - growZ;
        this.polygons = new ModelPart.Polygon[6];
        float f = this.maxX + growX;
        float f1 = this.maxY + growY;
        float f2 = this.maxZ + growZ;
        if (this.mirror) {
            float f3 = f;
            f = originX;
            originX = f3;
        }

        ModelPart.Vertex vertex7 = new ModelPart.Vertex(originX, originY, originZ, 0.0F, 0.0F);
        ModelPart.Vertex vertex = new ModelPart.Vertex(f, originY, originZ, 0.0F, 8.0F);
        ModelPart.Vertex vertex1 = new ModelPart.Vertex(f, f1, originZ, 8.0F, 8.0F);
        ModelPart.Vertex vertex2 = new ModelPart.Vertex(originX, f1, originZ, 8.0F, 0.0F);
        ModelPart.Vertex vertex3 = new ModelPart.Vertex(originX, originY, f2, 0.0F, 0.0F);
        ModelPart.Vertex vertex4 = new ModelPart.Vertex(f, originY, f2, 0.0F, 8.0F);
        ModelPart.Vertex vertex5 = new ModelPart.Vertex(f, f1, f2, 8.0F, 8.0F);
        ModelPart.Vertex vertex6 = new ModelPart.Vertex(originX, f1, f2, 8.0F, 0.0F);
        float f5 = this.u + this.dimensionZ;
        float f6 = this.u + this.dimensionZ + this.dimensionX;
        float f7 = this.u + this.dimensionZ + this.dimensionX + this.dimensionX;
        float f8 = this.u + this.dimensionZ + this.dimensionX + this.dimensionZ;
        float f9 = this.u + this.dimensionZ + this.dimensionX + this.dimensionZ + this.dimensionX;
        float f11 = this.v + this.dimensionZ;
        float f12 = this.v + this.dimensionZ + this.dimensionY;
        this.polygons[2] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex4, vertex3, vertex7, vertex}, f5, (float) this.v, f6, f11, this.texWidth, this.texHeight, this.mirror, Direction.DOWN);
        this.polygons[3] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex1, vertex2, vertex6, vertex5}, f6, f11, f7, (float) this.v, this.texWidth, this.texHeight, this.mirror, Direction.UP);
        this.polygons[1] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex7, vertex3, vertex6, vertex2}, (float) this.u, f11, f5, f12, this.texWidth, this.texHeight, this.mirror, Direction.WEST);
        this.polygons[4] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex, vertex7, vertex2, vertex1}, f5, f11, f6, f12, this.texWidth, this.texHeight, this.mirror, Direction.NORTH);
        this.polygons[0] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex4, vertex, vertex1, vertex5}, f6, f11, f8, f12, this.texWidth, this.texHeight, this.mirror, Direction.EAST);
        this.polygons[5] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex3, vertex4, vertex5, vertex6}, f8, f11, f9, f12, this.texWidth, this.texHeight, this.mirror, Direction.SOUTH);
    }

    @Override
    public CubeDeformation size() {
        return this.size;
    }

}
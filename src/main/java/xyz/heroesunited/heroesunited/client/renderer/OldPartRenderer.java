package xyz.heroesunited.heroesunited.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Literally just a class that imitate old 1.16.5 ModelRenderer class that now is ModelPart.
 * May decrease performance.
 */
//@Deprecated(forRemoval = true, since = "1.19")
public class OldPartRenderer {
   private float xTexSize = 64.0F, yTexSize = 32.0F;
   private int xTexOffs, yTexOffs;
   public float x, y, z, xRot, yRot, zRot;
   public boolean mirror = false;
   public final List<ModelPart.Cube> cubes = new ArrayList<>();
   public final List<OldPartRenderer> children = new ArrayList<>();

   public OldPartRenderer(TextureSize size, int xTexOffs, int yTexOffs) {
      this(size);
      this.texOffs(xTexOffs, yTexOffs);
   }

   public OldPartRenderer(TextureSize size) {
      this.xTexSize = size.getWidth();
      this.yTexSize = size.getHeight();
   }

   public OldPartRenderer() {
   }

   public ModelPart bake(String name) {
      Map<String, ModelPart> children = Maps.newHashMap();
      List<OldPartRenderer> modelRenderers = this.children;
      for (int i = 0; i < modelRenderers.size(); i++) {
         String id = name + "_child_" + i;
         children.put(id, modelRenderers.get(i).bake(id));
      }
      ModelPart part = new ModelPart(cubes, children);
      part.setRotation(this.xRot, this.yRot, this.zRot);
      part.setPos(this.x, this.y, this.z);
      return part;
   }

   public void copyFrom(OldPartRenderer modelRenderer) {
      this.xRot = modelRenderer.xRot;
      this.yRot = modelRenderer.yRot;
      this.zRot = modelRenderer.zRot;
      this.x = modelRenderer.x;
      this.y = modelRenderer.y;
      this.z = modelRenderer.z;
   }

   public void addChild(OldPartRenderer modelRenderer) {
      this.children.add(modelRenderer);
   }

   public OldPartRenderer texOffs(int xTexOffs, int yTexOffs) {
      this.xTexOffs = xTexOffs;
      this.yTexOffs = yTexOffs;
      return this;
   }

   public void setPos(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void setRotation(float xRot, float yRot, float zRot) {
      this.xRot = xRot;
      this.yRot = yRot;
      this.zRot = zRot;
   }

   public OldPartRenderer addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ) {
      this.addBox(this.xTexOffs, this.yTexOffs, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, 0.0F, 0.0F, 0.0F, this.mirror);
      return this;
   }

   public OldPartRenderer addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, boolean mirror) {
      this.addBox(this.xTexOffs, this.yTexOffs, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, 0.0F, 0.0F, 0.0F, mirror);
      return this;
   }

   public void addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float inflate) {
      this.addBox(this.xTexOffs, this.yTexOffs, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, inflate, inflate, inflate, this.mirror);
   }

   public void addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float expandX, float expandY, float expandZ) {
      this.addBox(this.xTexOffs, this.yTexOffs, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, expandX, expandY, expandZ, this.mirror);
   }

   public void addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float inflate, boolean mirror) {
      this.addBox(this.xTexOffs, this.yTexOffs, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, inflate, inflate, inflate, mirror);
   }

   private void addBox(int u, int v, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, float expandX, float expandY, float expandZ, boolean mirror) {
      this.cubes.add(new ModelPart.Cube(u, v, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, expandX, expandY, expandZ, mirror, this.xTexSize, this.yTexSize));
   }

   public static class TextureSize {
       private final float width;
       private final float height;

       public TextureSize(float size) {
           this.width = size;
           this.height = size;
       }

       public TextureSize(float width, float height) {
           this.width = width;
           this.height = height;
       }

       public float getHeight() {
           return height;
       }

       public float getWidth() {
           return width;
       }
   }
}
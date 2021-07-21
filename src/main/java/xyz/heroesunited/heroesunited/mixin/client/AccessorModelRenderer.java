package xyz.heroesunited.heroesunited.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelRenderer.class)
public interface AccessorModelRenderer {

    @Accessor("cubes")
    ObjectList<ModelRenderer.ModelBox> getCubes();
}

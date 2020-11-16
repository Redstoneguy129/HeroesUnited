package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IHUModel {

    ModelRenderer getModelHead();

    List<ModelRenderer> getModelRenderers();

}

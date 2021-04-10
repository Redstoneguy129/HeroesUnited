package xyz.heroesunited.heroesunited.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

public class HUMainTexture {

    private final ResourceLocation base, output, mask;

    public HUMainTexture(ResourceLocation base, ResourceLocation output, ResourceLocation mask) {
        this.base = base;
        this.output = output;
        this.mask = mask;
    }

    public ResourceLocation getTexture() {
        ResourceLocation baseOutput = new ResourceLocation(base.getNamespace(), String.format("%s_%d", base.getPath(), mask.hashCode()));
        AlphaMaskTexture texture = new AlphaMaskTexture(base, mask, output);
        if (Minecraft.getInstance().getTextureManager().getTexture(baseOutput) != texture) {
            Minecraft.getInstance().getTextureManager().register(baseOutput, texture);
        }
        if (Minecraft.getInstance().getTextureManager().getTexture(output) == null) {
            Minecraft.getInstance().getTextureManager().register(output, new SimpleTexture(output));
        }
        return baseOutput;
    }
}

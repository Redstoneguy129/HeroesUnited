package xyz.heroesunited.heroesunited.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class HUMainTexture {

    private final ResourceLocation base, output, mask;

    public HUMainTexture(ResourceLocation base, ResourceLocation output, ResourceLocation mask) {
        this.base = base;
        this.output = output;
        this.mask = mask;
    }

    public ResourceLocation getTexture() {
        ResourceLocation output = new ResourceLocation(this.output.getNamespace(), String.format("%s_%d", this.output.getPath(), this.mask.hashCode()));

        if (Minecraft.getInstance().getTextureManager().getTexture(output) == null) {
            Minecraft.getInstance().getTextureManager().register(output, new AlphaMaskTexture(this.base, this.mask, this.output));
        }

        return output;
    }
}

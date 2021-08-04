package xyz.heroesunited.heroesunited.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class HUMainTexture {

    private final Identifier base, output, mask;

    public HUMainTexture(Identifier base, Identifier output, Identifier mask) {
        this.base = base;
        this.output = output;
        this.mask = mask;
    }

    public Identifier getTexture() {
        Identifier output = new Identifier(this.output.getNamespace(), String.format("%s_%d", this.output.getPath(), this.mask.hashCode()));

        if (MinecraftClient.getInstance().getTextureManager().getTexture(output) == null) {
            MinecraftClient.getInstance().getTextureManager().registerTexture(output, new AlphaMaskTexture(this.base, this.mask, this.output));
        }

        return output;
    }
}

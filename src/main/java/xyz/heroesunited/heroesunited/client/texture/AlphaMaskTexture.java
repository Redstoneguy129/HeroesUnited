package xyz.heroesunited.heroesunited.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AlphaMaskTexture extends SimpleTexture {

    private final ResourceLocation maskLocation, output;

    public AlphaMaskTexture(ResourceLocation base, ResourceLocation maskLocation) {
        this(base, maskLocation, base);
    }

    public AlphaMaskTexture(ResourceLocation base, ResourceLocation maskLocation, ResourceLocation output) {
        super(base);
        this.maskLocation = maskLocation;
        this.output = output;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        NativeImage image = getSkinOrImage(manager, this.location);
        NativeImage output = getSkinOrImage(manager, this.output);
        NativeImage mask = NativeImage.read(manager.getResource(this.maskLocation).get().open());

        for (int y = 0; y < mask.getHeight(); ++y) {
            for (int x = 0; x < mask.getWidth(); ++x) {
                int pixelMask = mask.getPixelRGBA(x, y);
                int pixelOutput = output.getPixelRGBA(x, y);
                Color color = new Color(pixelMask, true);
                Color colorDefault = new Color(image.getPixelRGBA(x, y), true);
                boolean isBlack = color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
                boolean isWhite = color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255;
                if (colorDefault.getRGB() == pixelOutput) {
                    if (isBlack || isWhite) {
                        float hue = 1F - (color.getRed() + color.getGreen() + color.getBlue()) / 3F / 255F;
                        image.setPixelRGBA(x, y, new Color(colorDefault.getRed(), colorDefault.getGreen(),
                                colorDefault.getBlue(), pixelMask == 0 ? 0 : (int) (colorDefault.getAlpha() * hue)).getRGB());
                    } else {
                        image.setPixelRGBA(x, y, pixelMask);
                    }
                } else {
                    image.setPixelRGBA(x, y, pixelMask == 0 || isWhite ?
                                colorDefault.getRGB() : isBlack ? pixelOutput : pixelMask);
                }
            }
        }
        mask.close();

        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
                this.bind();
                image.upload(0, 0, 0, false);
            });
        } else {
            TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
            this.bind();
            image.upload(0, 0, 0, false);
        }
    }

    @Override
    public void close() {
        this.releaseId();
    }

    public static NativeImage getSkinOrImage(ResourceManager manager, ResourceLocation location) throws IOException {
        if (location.getPath().startsWith("skins/")) {
            String s = location.getPath().replace("skins/", "");
            File file = new File(Minecraft.getInstance().getSkinManager().skinsDirectory.getAbsolutePath(), (s.length() > 2 ? s.substring(0, 2) : "xx"));
            return NativeImage.read(new FileInputStream(new File(file, s)));
        }
        return NativeImage.read(manager.getResource(location).get().open());
    }

    public static ResourceLocation getTexture(ResourceLocation base, ResourceLocation outputTex, ResourceLocation mask) {
        ResourceLocation output = new ResourceLocation(outputTex.getNamespace(), String.format("%s_alpha_mask_%d", outputTex.getPath(), mask.hashCode()));

        if (!(Minecraft.getInstance().getTextureManager().getTexture(output, MissingTextureAtlasSprite.getTexture()) instanceof AlphaMaskTexture)) {
            Minecraft.getInstance().getTextureManager().register(output, new AlphaMaskTexture(base, mask, outputTex));
        }

        return output;
    }
}

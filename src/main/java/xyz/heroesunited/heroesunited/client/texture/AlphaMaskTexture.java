package xyz.heroesunited.heroesunited.client.texture;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
    public void load(IResourceManager manager) throws IOException {
        releaseId();
        InputStream[] streams = new InputStream[3];
        NativeImage image = NativeImage.read(streams[0] = manager.getResource(this.location).getInputStream());
        NativeImage mask = NativeImage.read(streams[1] = manager.getResource(this.maskLocation).getInputStream());
        NativeImage output = NativeImage.read(streams[2] = manager.getResource(this.output).getInputStream());

        for (int y = 0; y < mask.getHeight(); ++y) {
            for (int x = 0; x < mask.getWidth(); ++x) {
                int pixelMask = mask.getPixelRGBA(x, y);
                Color color = new Color(pixelMask, true);
                Color colorOutput = new Color(output.getPixelRGBA(x, y), true);
                Color colorDefault = new Color(image.getPixelRGBA(x, y), true);
                boolean isBlack = color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
                boolean isWhite = color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255;
                if (colorDefault.equals(colorOutput)) {
                    if (isBlack || isWhite) {
                        float hue = 1F - (color.getRed() + color.getGreen() + color.getBlue()) / 3F / 255F;
                        int newAlpha = pixelMask == 0 ? 0 : (int) (colorDefault.getAlpha() * hue);
                        Color newColor = new Color(colorDefault.getRed(), colorDefault.getGreen(), colorDefault.getBlue(), newAlpha);
                        image.setPixelRGBA(x, y, newColor.getRGB());
                    } else {
                        image.setPixelRGBA(x, y, color.getRGB());
                    }
                } else {
                    if (pixelMask == 0 || isWhite) {
                        image.setPixelRGBA(x, y, colorDefault.getRGB());
                    } else {
                        if (isBlack) {
                            image.setPixelRGBA(x, y, colorOutput.getRGB());
                        } else {
                            image.setPixelRGBA(x, y, color.getRGB());
                        }
                    }
                }
            }
        }
        mask.close();
        TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, false);
        for (InputStream stream : streams) {
            if (stream != null) {
                stream.close();
            }
        }
    }
}

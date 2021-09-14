package xyz.heroesunited.heroesunited.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

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
    public void load(IResourceManager manager) throws IOException {
        releaseId();
        NativeImage image = NativeImage.read(manager.getResource(this.location).getInputStream());
        File skinsDirectory = Minecraft.getInstance().getSkinManager().skinsDirectory;
        if (this.location.getPath().startsWith("skins/")) {
            String s = this.location.getPath().replace("skins/", "");
            File file = new File(skinsDirectory.getAbsolutePath(), (s.length() > 2 ? s.substring(0, 2) : "xx"));
            image = NativeImage.read(new FileInputStream(new File(file, s)));
        }
        NativeImage mask = NativeImage.read(manager.getResource(this.maskLocation).getInputStream());
        NativeImage output = NativeImage.read(manager.getResource(this.output).getInputStream());
        if (this.output.getPath().startsWith("skins/")) {
            String s = this.output.getPath().replace("skins/", "");
            File file = new File(skinsDirectory.getAbsolutePath(), (s.length() > 2 ? s.substring(0, 2) : "xx"));
            output = NativeImage.read(new FileInputStream(new File(file, s)));
        }

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
    }
}

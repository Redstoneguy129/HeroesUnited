package xyz.heroesunited.heroesunited.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class AlphaMaskTexture extends ResourceTexture {

    private final Identifier maskLocation, output;

    public AlphaMaskTexture(Identifier base, Identifier maskLocation) {
        this(base, maskLocation, base);
    }
    
    public AlphaMaskTexture(Identifier base, Identifier maskLocation, Identifier output) {
        super(base);
        this.maskLocation = maskLocation;
        this.output = output;
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
        clearGlId();
        InputStream[] streams = new InputStream[3];
        NativeImage image = NativeImage.read(streams[0] = manager.getResource(this.location).getInputStream());
        NativeImage mask = NativeImage.read(streams[1] = manager.getResource(this.maskLocation).getInputStream());
        NativeImage output = NativeImage.read(streams[2] = manager.getResource(this.output).getInputStream());

        for (int y = 0; y < mask.getHeight(); ++y) {
            for (int x = 0; x < mask.getWidth(); ++x) {
                int pixelMask = mask.getPixelColor(x, y);
                Color color = new Color(pixelMask, true);
                Color colorOutput = new Color(output.getPixelColor(x, y), true);
                Color colorDefault = new Color(image.getPixelColor(x, y), true);
                boolean isBlack = color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;
                boolean isWhite = color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255;
                if (colorDefault.equals(colorOutput)) {
                    if (isBlack || isWhite) {
                        float hue = 1F - (color.getRed() + color.getGreen() + color.getBlue()) / 3F / 255F;
                        int newAlpha = pixelMask == 0 ? 0 : (int) (colorDefault.getAlpha() * hue);
                        Color newColor = new Color(colorDefault.getRed(), colorDefault.getGreen(), colorDefault.getBlue(), newAlpha);
                        image.setPixelColor(x, y, newColor.getRGB());
                    } else {
                        image.setPixelColor(x, y, color.getRGB());
                    }
                } else {
                    if (pixelMask == 0 || isWhite) {
                        image.setPixelColor(x, y, colorDefault.getRGB());
                    } else {
                        if (isBlack) {
                            image.setPixelColor(x, y, colorOutput.getRGB());
                        } else {
                            image.setPixelColor(x, y, color.getRGB());
                        }
                    }
                }
            }
        }
        mask.close();
        TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, false);
        for (InputStream stream : streams) {
            if (stream != null) {
                stream.close();
            }
        }
    }
}

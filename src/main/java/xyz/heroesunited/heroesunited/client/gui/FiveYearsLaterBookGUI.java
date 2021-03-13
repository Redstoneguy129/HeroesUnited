package xyz.heroesunited.heroesunited.client.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.net.URI;
import java.net.URISyntaxException;

public class FiveYearsLaterBookGUI extends Screen {

    private String[] pages = new String[]{"cover", "p1", "p2", "p3", "p4", "p5", "p6", "p7"};
    private int xSize, ySize;
    private int pageNum = 0;

    public FiveYearsLaterBookGUI() {
        super(new TranslationTextComponent("screen.heroesunited.fiveyearslater"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        this.xSize = (width - 200) / 2;
        this.ySize = (height - 260) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        SnowWidget.drawSnowOnScreen(matrixStack, this.width, this.height);
        this.renderBackground(matrixStack);
        String page;
        try {
            page = pages[pageNum];
        } catch (ArrayIndexOutOfBoundsException e) {
            page = pages[0];
        }
        matrixStack.pushPose();
        this.minecraft.getTextureManager().bind(new ResourceLocation(HeroesUnited.MODID, String.format("textures/gui/comic/5yl%s.png", page)));
        blit(matrixStack, xSize, ySize, 0, 0, 200, 260, 200, 260);
        this.buttons.clear();
        this.addButton(new Button(xSize - 20, ySize, 20, 20, new TranslationTextComponent("<"), b -> backPage()));
        this.addButton(new Button(xSize + 200, ySize, 20, 20, new TranslationTextComponent(">"), b -> nextPage()));
        if (pageNum == pages.length - 1) {
            this.addButton(new Button(xSize + 25, ySize + (260 / 2) + 50, 150, 20, new TranslationTextComponent("Check Out The 5YL Comic!"), b -> minecraft.setScreen(new ConfirmOpenLinkScreen(this::confirmCallback, "https://www.theinktank.co/5yearslater", true))));
        }
        matrixStack.popPose();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void confirmCallback(boolean will) {
        this.getMinecraft().setScreen(null);
        if (will) {
            try {
                Util.getPlatform().openUri(new URI("https://www.theinktank.co/5yearslater"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void nextPage() {
        pageNum++;
        if (pageNum > pages.length - 1) {
            pageNum = 0;
        }
    }

    private void backPage() {
        pageNum -= 1;
        if (pageNum < 0) {
            pageNum = pages.length - 1;
        }
    }
}

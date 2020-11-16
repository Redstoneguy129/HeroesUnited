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

    public enum Page {
        COVER("cover"),
        P1("p1"),
        P2("p2"),
        P3("p3"),
        P4("p4"),
        P5("p5"),
        P6("p6"),
        P7( "p7");

        private final ResourceLocation texture;

        Page(String name) {
            this.texture = new ResourceLocation(HeroesUnited.MODID, String.format("textures/gui/comic/5yl%s.png", name));
        }

        public ResourceLocation getTexture() {
            return this.texture;
        }

        public static Page getPage(int page) {
            Page foundPage;
            try {
                foundPage = Page.values()[page];
            } catch (ArrayIndexOutOfBoundsException e) {
                foundPage = COVER;
            }
            return foundPage;
        }
    }

    private int xCanvas;
    private int yCanvas;
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
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        matrixStack.push();
        Page page = Page.getPage(pageNum);
        this.xCanvas = (width - 200) / 2;
        this.yCanvas = (height - 260) / 2;
        this.getMinecraft().getTextureManager().bindTexture(page.getTexture());
        blit(matrixStack, xCanvas, yCanvas, 0, 0, 200, 260, 200, 260);
        this.buttons.clear();
        this.addButton(new Button(xCanvas-20, yCanvas, 20, 20, new TranslationTextComponent("<"), p_onPress_1_ -> backPage()));
        this.addButton(new Button(xCanvas+200, yCanvas, 20, 20, new TranslationTextComponent(">"), p_onPress_1_ -> nextPage()));
        if(pageNum == Page.values().length-1) {
            this.addButton(new Button(xCanvas+25, yCanvas+(260/2)+50, 150, 20, new TranslationTextComponent("Check Out The 5YL Comic!"), p_onPress_1_ -> goTo5YLPage()));
        }
        matrixStack.pop();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void goTo5YLPage() {
        this.getMinecraft().displayGuiScreen(new ConfirmOpenLinkScreen(this::confirmCallback, "https://www.theinktank.co/5yearslater", true));
    }

    private void confirmCallback(boolean will) {
        this.getMinecraft().displayGuiScreen(null);
        if(will) {
            try {
                Util.getOSType().openURI(new URI("https://www.theinktank.co/5yearslater"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void nextPage() {
        pageNum++;
        if(pageNum > Page.values().length-1) {
            pageNum = 0;
        }
    }

    private void backPage() {
        pageNum-=1;
        if(pageNum < 0) {
            pageNum = Page.values().length-1;
        }
    }
}

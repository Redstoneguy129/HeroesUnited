package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;

public class AccessoriesScreen extends ContainerScreen<AccessoriesContainer> {

    private float oldMouseX, oldMouseY;

    private static final ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation(HeroesUnited.MODID + ":textures/gui/accessories_gui.png");

    public AccessoriesScreen(AccessoriesContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        SnowWidget.drawSnowOnScreen(matrix, this.width, this.height);
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        int i = this.getGuiLeft();
        int j = this.getGuiTop();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(INVENTORY_GUI_TEXTURE);
        this.blit(matrixStack, i, j, 0, 0, this.getXSize(), this.getYSize());
        InventoryScreen.renderEntityInInventory(i + 51, j + 75, 30, (float) (i + 51) - this.oldMouseX, (float) (j + 75 - 50) - this.oldMouseY, this.minecraft.player);
    }
}
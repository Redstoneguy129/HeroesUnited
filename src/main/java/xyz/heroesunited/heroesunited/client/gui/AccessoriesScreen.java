package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.HUClientEventHandler;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.List;

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
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputMappings.Input mouseKey = InputMappings.getKey(pKeyCode, pScanCode);
        if (HUClientEventHandler.ACCESSORIES_SCREEN.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(INVENTORY_GUI_TEXTURE);
        this.blit(matrixStack, left, top, 0, 0, this.getXSize(), this.getYSize());
        InventoryScreen.renderEntityInInventory(left + 51, top + 75, 30, (float) (left + 51) - this.oldMouseX, (float) (top + 75 - 50) - this.oldMouseY, this.minecraft.player);

        List<Slot> slots = this.menu.slots;
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.getItem().isEmpty() && slot.isActive() && slot instanceof AccessoriesContainer.AccessorySlot) {
                EquipmentAccessoriesSlot accessoriesSlot = EquipmentAccessoriesSlot.getFromSlotIndex(i);
                if (accessoriesSlot != null && accessoriesSlot != EquipmentAccessoriesSlot.WRIST) {
                    ResourceLocation resourceLocation = new ResourceLocation(HeroesUnited.MODID, "textures/gui/accessories_slots/" + accessoriesSlot.name().toLowerCase() + ".png");
                    this.minecraft.getTextureManager().bind(resourceLocation);
                    if (accessoriesSlot == EquipmentAccessoriesSlot.GLOVES) {
                        blit(matrixStack, left + 77, top + 44, 0, 0, 16, 16, 16, 16);
                    } else {
                        blit(matrixStack, left + (i > 7 ? 153 : i > 3 ? 126 : 99), top + (i > 7 ? 8 : i > 3 ? 8 + (i - 4) * 18 : 8 + i * 18), 0, 0, 16, 16, 16, 16);
                    }
                }
            }
        }
    }
}
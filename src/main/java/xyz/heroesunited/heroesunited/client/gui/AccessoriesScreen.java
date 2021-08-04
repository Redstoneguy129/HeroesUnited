package xyz.heroesunited.heroesunited.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.List;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AccessoriesScreen extends HandledScreen<AccessoriesContainer> {

    private float oldMouseX, oldMouseY;

    private static final Identifier INVENTORY_GUI_TEXTURE = new Identifier(HeroesUnited.MODID + ":textures/gui/accessories_gui.png");

    public AccessoriesScreen(AccessoriesContainer screenContainer, PlayerInventory inv, Text titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        SnowWidget.drawSnowOnScreen(matrix, this.width, this.height);
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrix, mouseX, mouseY);
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int x, int y) {
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack, float partialTicks, int x, int y) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bind(INVENTORY_GUI_TEXTURE);
        this.drawSprite(matrixStack, left, top, 0, 0, this.getXSize(), this.getYSize());
        InventoryScreen.drawEntity(left + 51, top + 75, 30, (float) (left + 51) - this.oldMouseX, (float) (top + 75 - 50) - this.oldMouseY, this.client.player);

        List<Slot> slots = this.handler.slots;
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.getStack().isEmpty() && slot.isEnabled() && slot instanceof AccessoriesContainer.AccessorySlot) {
                EquipmentAccessoriesSlot accessoriesSlot = EquipmentAccessoriesSlot.getFromSlotIndex(i);
                if (accessoriesSlot != EquipmentAccessoriesSlot.WRIST) {
                    Identifier resourceLocation = new Identifier(HeroesUnited.MODID, "textures/gui/accessories_slots/" + accessoriesSlot.name().toLowerCase() + ".png");
                    this.client.getTextureManager().bind(resourceLocation);
                    if (accessoriesSlot == EquipmentAccessoriesSlot.GLOVES) {
                        this.drawTexture(matrixStack, left + 77, top + 44, 0, 0, 16, 16, 16, 16);
                    } else {
                        this.drawTexture(matrixStack, left + (i > 3 ? 141 : 109), top + (i > 3 ? 8 + (i - 4) * 18 : 8 + i * 18), 0, 0, 16, 16, 16, 16);
                    }
                }
            }
        }
    }
}
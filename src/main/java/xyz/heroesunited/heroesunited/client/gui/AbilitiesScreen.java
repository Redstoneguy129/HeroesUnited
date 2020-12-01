package xyz.heroesunited.heroesunited.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerSetTheme;
import xyz.heroesunited.heroesunited.common.networking.server.ServerToggleAbility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For adding new themes, use @AbilityHelper.addTheme(modid, location);
 */
public class AbilitiesScreen extends Screen {

    private final ResourceLocation ABILITY_GUI = new ResourceLocation(HeroesUnited.MODID, "textures/gui/ability_gui.png");
    private final int xSize = 200, ySize = 170;
    public AbilityList abilityList;
    private int left, top, maxTextureX = 190, maxTextureY = 50;
    public static List<ResourceLocation> themes = Lists.newArrayList(getTheme("default"), getTheme("black"), getTheme("rainbow"));

    public AbilitiesScreen() {
        super(new TranslationTextComponent("gui.heroesunited.abilities"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        left = (width - xSize) / 2;
        top = (height - ySize) / 2;
        this.abilityList = new AbilityList(this.minecraft, this, 200, height, top + 50, top + 130, this.font.FONT_HEIGHT + 12);
        this.abilityList.setLeftPos(left + 20);
        IHUPlayer cap = HUPlayer.getCap(minecraft.player);
        this.addButton(new Button(left + 110, top + 5, 80, 20, new TranslationTextComponent("Change Theme"), (b) -> {
            if (cap.getTheme() >= themes.size())
                cap.setTheme(0);
            HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerSetTheme(cap.getTheme() + 1, themes.size()));
            minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));
        this.children.add(abilityList);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        IHUPlayer cap = HUPlayer.getCap(minecraft.player);
        this.renderBackground(matrixStack);
        matrixStack.push();
        ResourceLocation theme = getTheme("default");
        if (cap.getTheme() < themes.size()) {
            theme = themes.get(cap.getTheme());
        }
        //Background
        minecraft.getTextureManager().bindTexture(theme);
        blit(matrixStack, left, top, 0, 0, xSize, ySize, xSize, ySize);

        //List abilities
        if (this.abilityList != null) {
            this.abilityList.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        minecraft.getTextureManager().bindTexture(theme);
        blit(matrixStack, left, top, 0, 0, 200, 45, xSize, ySize);

        //Player Head and nick
        minecraft.getTextureManager().bindTexture(ABILITY_GUI);
        blit(matrixStack, left + 1, top + 1, 0, 0, 40, 40, maxTextureX, maxTextureY);

        minecraft.getTextureManager().bindTexture(minecraft.player.getLocationSkin());
        blit(matrixStack, left + 5, top + 5, 32, 32, 32, 32, 256, 256);
        font.func_238406_a_(matrixStack, "" + minecraft.player.getName().getString(), left + 42, top + 7, 16777215, false);
        matrixStack.pop();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public static ResourceLocation getTheme(String name) {
        final ResourceLocation THEME = new ResourceLocation(HeroesUnited.MODID, "textures/gui/themes/"+ name + ".png");
        return THEME;
    }

    public static class AbilityListEntry extends ExtendedList.AbstractListEntry<AbilityListEntry> {

        private final AbilityType type;
        private final AbilitiesScreen parent;
        private final boolean active;

        public AbilityListEntry(AbilityType type, AbilitiesScreen parent, boolean active) {
            this.type = type;
            this.parent = parent;
            this.active = active;
        }

        @Override
        public void render(MatrixStack matrixStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            RenderSystem.pushMatrix();
            FontRenderer fontRenderer = this.parent.font;
            Color color = isMouseOver ? AbilityHelper.canActiveAbility(this.type, this.parent.minecraft.player) ? Color.GREEN : Color.RED : Color.WHITE;

            RenderSystem.color3f(color.getRed(), color.getGreen(), color.getBlue());
            this.parent.minecraft.textureManager.bindTexture(parent.ABILITY_GUI);
            blit(matrixStack, left, top + 2, 40, 30, 150, 20, parent.maxTextureX, parent.maxTextureY);
            RenderSystem.color4f(255F, 255f, 255f, 1f);

            fontRenderer.func_238406_a_(matrixStack, fontRenderer.func_238412_a_(this.type.getDisplayName().getString(), entryWidth - 25), left + 5, top + 8, 0xfefefe, false);

            if (this.active) {
                this.parent.minecraft.textureManager.bindTexture(parent.ABILITY_GUI);
                blit(matrixStack, left + entryWidth - 70, top + 4, 40, 0, 16, 16, parent.maxTextureX, parent.maxTextureY);
            }

            RenderSystem.popMatrix();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int type) {
            if (!this.type.alwaysActive()) {
                HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerToggleAbility(this.type));
                this.parent.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return false;
        }
    }

    public class AbilityList extends ExtendedList<AbilityListEntry> {

        private AbilitiesScreen parent;
        private int listWidth;

        public AbilityList(Minecraft mcIn, AbilitiesScreen parent, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
            this.parent = parent;
            this.listWidth = widthIn;
            this.refreshList();
        }

        public void refreshList() {
            this.clearEntries();
            Collection<AbilityType> abilities = new ArrayList<>(AbilityHelper.getAbilities(this.minecraft.player));
            AbilityType type = Superpower.getTypeFromSuperpower(minecraft.player);
            if (type != null && !type.isHidden()) {
                this.addEntry(new AbilityListEntry(type, this.parent, abilities.contains(type)));
            }
        }

        @Override
        public int getRowWidth() {
            return this.listWidth;
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getLeft() + this.width - 30;
        }

        public int getMaxScroll() {
            return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            int k = this.getRowLeft();
            int l = this.y0 + 4 - (int) this.getScrollAmount();
            this.renderList(matrixStack, k, l, mouseX, mouseY, partialTicks);

            if (getEventListeners().size() == 0) {
                this.drawCenteredString(matrixStack, parent.font, "You don't have any ability yet", left + 95, top + 95, 16777215);
            }

            //Scrollbar
            int i = this.getScrollbarPosition();
            int j = i + 6;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            RenderSystem.disableTexture();
            int k1 = this.getMaxScroll();
            if (k1 > 0) {
                int l1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
                l1 = MathHelper.clamp(l1, 32, this.y1 - this.y0 - 8);
                int i2 = (int) this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
                if (i2 < this.y0) {
                    i2 = this.y0;
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(i, this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(j, this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(j, this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(i, this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(i, (i2 + l1), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(j, (i2 + l1), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(j, i2, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(i, i2, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(i, (i2 + l1 - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((j - 1), (i2 + l1 - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos((j - 1), i2, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos(i, i2, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
                tessellator.draw();
            }
            RenderSystem.enableTexture();

        }
    }

}

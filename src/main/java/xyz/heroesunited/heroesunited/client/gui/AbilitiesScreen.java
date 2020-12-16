package xyz.heroesunited.heroesunited.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerSetTheme;

import java.awt.*;
import java.util.List;

/**
 * For adding new themes, use @AbilityHelper.addTheme(modid, location);
 */
public class AbilitiesScreen extends Screen {

    private final ResourceLocation HEAD = new ResourceLocation(HeroesUnited.MODID, "textures/gui/head.png");
    private final ResourceLocation BUTTON = new ResourceLocation(HeroesUnited.MODID, "textures/gui/ability_button.png");
    public static List<ResourceLocation> themes = Lists.newArrayList(getTheme("default"), getTheme("black"), getTheme("rainbow"));
    private int left, top, scrollOffset;
    private List<Ability> types;

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
        left = (width - 200) / 2;
        top = (height - 170) / 2;
        types = Lists.newArrayList(Superpower.getTypesFromSuperpower(this.minecraft.player).values());
        IHUPlayer cap = HUPlayer.getCap(minecraft.player);

        this.addButton(new Button(left + 110, top + 5, 80, 20, new TranslationTextComponent("Change Theme"), (b) -> {
            if (cap.getTheme() >= themes.size())
                cap.setTheme(0);
            HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerSetTheme(cap.getTheme() + 1, themes.size()));
            minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));

        for (int i = scrollOffset; i < scrollOffset + 4; i++) {
            if (i >= types.size()) break;
            int n = i - scrollOffset;
            Ability ability = types.get(i);
            if (ability != null && !ability.isHidden()) {
                this.addButton(new AbilityButton(left + 25, top + 50 + 25 * n, 150, 20, this, ability));
            }
        }
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
        blit(matrixStack, left, top, 0, 0, 200, 170, 200, 170);
        renderScrollbar(left + 187.5F, 6);
        if (types.size() == 0) {
            this.drawCenteredString(matrixStack, this.font, "You don't have any ability yet", left + 95, top + 95, 16777215);
        }
        //Player Head and nick
        minecraft.getTextureManager().bindTexture(HEAD);
        blit(matrixStack, left + 1, top + 1, 0, 0, 40, 40, 40, 40);

        minecraft.getTextureManager().bindTexture(minecraft.player.getLocationSkin());
        blit(matrixStack, left + 5, top + 5, 32, 32, 32, 32, 256, 256);
        font.func_238406_a_(matrixStack, minecraft.player.getName().getString(), left + 42, top + 7, 16777215, false);
        matrixStack.pop();

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        buttons.forEach(button -> {
            if (button instanceof AbilityButton) {
                this.renderAbilityDescription(matrixStack, mouseX, mouseY, (AbilityButton) button);
            }
        });
    }

    public void renderAbilityDescription(MatrixStack matrix, int mx, int my, AbilityButton button) {
        if (!(mx >= button.x && mx <= button.x + button.getWidth() && my >= button.y && my <= button.y + button.getHeightRealms())) return;
        if (button.ability.getHoveredDescription() == null) return;
        int bgX = mx + button.descWidth > this.width ? mx - button.descWidth : mx + 15;
        int bgY = my + button.descHeight + 10 > this.height ? my - 5 - button.descHeight : my;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        float color = 0.2F;

        builder.pos(bgX, bgY + button.descHeight, 0).color(color, color, color, 0.8F).endVertex();
        builder.pos(bgX + button.descWidth, bgY + button.descHeight, 0).color(color, color, color, 0.8F).endVertex();
        builder.pos(bgX + button.descWidth, bgY, 0).color(color, color, color, 0.8F).endVertex();
        builder.pos(bgX, bgY, 0).color(color, color, color, 0.8F).endVertex();
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();

        for (int i = 0; i < button.abilityDescription.size(); i++) {
            String line = button.abilityDescription.get(i);
            this.font.drawStringWithShadow(matrix, line, bgX + 10, bgY + 10 + i * 13, 0xFFFFFF);
        }
        boolean activate = AbilityHelper.canActiveAbility(button.ability, this.minecraft.player);
        this.font.drawStringWithShadow(matrix, activate ? "Ability can be activated" : "Ability cannot be activated", bgX + 10, bgY + 10 + (button.abilityDescription.size() + 1) * 13, activate ? 0x00FF00 : 0xFF0000);
    }

    public static class AbilityButton extends Button {

        private final AbilitiesScreen parent;
        private final Ability ability;
        private List<String> abilityDescription = Lists.newArrayList();
        private int descWidth, descHeight;

        public AbilityButton(int x, int y, int w, int h, AbilitiesScreen screen, Ability ability) {
            super(x, y, w, h, StringTextComponent.EMPTY, AbilityButton::onPressed);
            this.parent = screen;
            this.ability = ability;
            this.prepareDescriptionRender();
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            Minecraft mc = Minecraft.getInstance();
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            Color color = AbilityHelper.getEnabled(this.ability, mc.player) ? hovered ? Color.ORANGE : Color.YELLOW :
                    hovered ? AbilityHelper.canActiveAbility(this.ability, mc.player) ? Color.GREEN : Color.RED : Color.WHITE;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            mc.getTextureManager().bindTexture(this.parent.BUTTON);
            builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            builder.pos(x, y + height, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(0, 1).endVertex();
            builder.pos(x + width, y + height, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(1, 1).endVertex();
            builder.pos(x + width, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(1, 0).endVertex();
            builder.pos(x, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex(0, 0).endVertex();
            tessellator.draw();
            RenderSystem.disableBlend();
            RenderSystem.color3f(1f, 1f, 1f);
            this.ability.drawIcon(stack, x + 2, y + 2);
            mc.fontRenderer.drawString(stack, this.ability.name, x + 20, y + 6, 0xFFFFFFFF);
            mc.fontRenderer.drawString(stack, this.ability.name, x + 22, y + 8, 0);
        }

        private static void onPressed(Button button) {
            AbilityButton btn = (AbilityButton) button;
            btn.parent.minecraft.player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            if (!btn.ability.alwaysActive()) {
                if (AbilityHelper.getEnabled(btn.ability, btn.parent.minecraft.player)) {
                    HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerDisableAbility(btn.ability.name));
                } else {
                    HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerEnableAbility(btn.ability.name, btn.ability.serializeNBT()));
                }
                btn.parent.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            });
        }

        private void prepareDescriptionRender() {
            abilityDescription.clear();
            if (this.ability.getHoveredDescription() == null) return;
            int maxWidth = 0;
            for (int i = 0; i < this.ability.getHoveredDescription().size(); i++) {
                String text = this.ability.getHoveredDescription().get(i);
                int j = 0, width = text.length();
                while (width > 0) {
                    width -= 40;
                    if (width < 0) {
                        abilityDescription.add(text.substring(j, j + 40 + width));
                        j += 40;
                        continue;
                    }
                    abilityDescription.add(text.substring(j, j + 40));
                    j += 40;
                }
            }
            for (String s : abilityDescription) {
                int width = Minecraft.getInstance().fontRenderer.getStringWidth(s);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            descWidth = maxWidth + 20;
            descHeight = abilityDescription.size() * 13 + 40;
        }
    }

    public static ResourceLocation getTheme(String name) {
        final ResourceLocation THEME = new ResourceLocation(HeroesUnited.MODID, "textures/gui/themes/" + name + ".png");
        return THEME;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        int next = scrollOffset - (int) value;
        if (next >= 0 && next <= types.size() - 4) {
            scrollOffset = next;
            init();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (Widget widget : buttons) {
                if (widget.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void renderScrollbar(float x, int width) {
        int barHeight = 4 * 25 + 20;
        double step = barHeight / (types.size() != 0 ? types.size() : 1);
        double start = scrollOffset * step;
        double end = Math.min(barHeight, (scrollOffset + 4) * step);
        int y = top + 36;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        RenderSystem.color3f(1f, 1f, 1f);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x - 1.5, y + 2 + barHeight, 0).color(0, 0, 0, 255).endVertex();
        builder.pos(x + width + 1.5, y + 2 + barHeight, 0).color(0, 0, 0, 255).endVertex();
        builder.pos(x + width + 1.5, y + 7, 0).color(0, 0, 0, 255).endVertex();
        builder.pos(x - 1.5, y + 7, 0).color(0, 0, 0, 255).endVertex();

        builder.pos(x, y + end, 0).color(128, 128, 128, 255).endVertex();
        builder.pos(x + width, y + end, 0).color(128, 128, 128, 255).endVertex();
        builder.pos(x + width, y + 9 + start, 0).color(128, 128, 128, 255).endVertex();
        builder.pos(x, y + 9 + start, 0).color(128, 128, 128, 255).endVertex();

        builder.pos(x + 1, y - 1 + end, 0).color(192, 192, 192, 255).endVertex();
        builder.pos(x + width - 1, y - 1 + end, 0).color(192, 192, 192, 255).endVertex();
        builder.pos(x + width - 1, y + 10 + start, 0).color(192, 192, 192, 255).endVertex();
        builder.pos(x + 1, y + 10 + start, 0).color(192, 192, 192, 255).endVertex();

        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}

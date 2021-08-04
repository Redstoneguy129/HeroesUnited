package xyz.heroesunited.heroesunited.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.Level;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerSetTheme;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For adding new themes, use @AbilityHelper.addTheme(modid, location);
 */
public class AbilitiesScreen extends Screen {

    private final Identifier HEAD = new Identifier(HeroesUnited.MODID, "textures/gui/head.png");
    private final Identifier BUTTON = new Identifier(HeroesUnited.MODID, "textures/gui/ability_button.png");
    public static List<Identifier> themes = Lists.newArrayList(getTheme("default"), getTheme("black"), getTheme("rainbow"));
    public static int INDEX = 0;
    private int left, top;

    public AbilitiesScreen() {
        super(new TranslatableText("gui.heroesunited.abilities"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        this.clearChildren();
        left = (width - 200) / 2;
        top = (height - 170) / 2;
        IHUPlayer cap = HUPlayer.getCap(client.player);

        this.addDrawableChild(new ButtonWidget(left + 110, top + 5, 80, 20, new TranslatableText("Change Theme"), (b) -> {
            if (cap.getTheme() >= themes.size())
                cap.setTheme(0);
            HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerSetTheme(cap.getTheme() + 1, themes.size()));
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));
        List<Ability> abilities = getCurrentDisplayedAbilities(this.client.player);
        for (int i = 0; i < abilities.size(); i++) {
            this.addDrawableChild(new AbilityButton(left, top, i, this, abilities.get(i)));
        }
    }

    public static List<Ability> getCurrentDisplayedAbilities(PlayerEntity player) {
        List<Ability> abilities = Lists.newArrayList(), list = Lists.newArrayList();
        abilities.addAll(HUAbilityCap.getCap(player).getAbilities().values().stream()
                .filter(a -> a != null && !a.isHidden(player))
                .collect(Collectors.toList()));

        if (abilities.isEmpty()) {
            return list;
        }

        if (INDEX >= abilities.size()) {
            INDEX = 0;
        } else if (INDEX < 0) {
            INDEX = abilities.size() - 1;
        }

        list.add(abilities.get(INDEX));

        int i = INDEX + 1, added = 1;
        while (list.size() < 4 && added < abilities.size()) {
            if (i >= abilities.size()) {
                i = 0;
            }
            list.add(abilities.get(i));
            i++;
            added++;
        }

        return list;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        IHUPlayer cap = HUPlayer.getCap(client.player);
        SnowWidget.drawSnowOnScreen(matrixStack, this.width, this.height);
        this.renderBackground(matrixStack);
        matrixStack.push();
        Identifier theme = getTheme("default");
        if (cap != null && cap.getTheme() < themes.size()) {
            theme = themes.get(cap.getTheme());
        }
        RenderSystem.setShaderTexture(0, theme);
        drawTexture(matrixStack, left, top, 0, 0, 200, 170, 200, 170);
        if (getCurrentDisplayedAbilities(this.client.player).isEmpty()) {
            drawCenteredText(matrixStack, this.textRenderer, "You don't have any ability yet", left + 95, top + 95, 16777215);
        }

        RenderSystem.setShaderTexture(0, HEAD);
        drawTexture(matrixStack, left + 1, top + 1, 0, 0, 40, 40, 40, 40);

        RenderSystem.setShaderTexture(0, client.player.getSkinTexture());
        drawTexture(matrixStack, left + 5, top + 5, 32, 32, 32, 32, 256, 256);
        textRenderer.drawWithShadow(matrixStack, client.player.getName().getString(), left + 42, top + 7, 16777215, false);
        matrixStack.pop();
        if (cap != null) {
            cap.getSuperpowerLevels().forEach((res, lvl) -> {
                if (HUPackSuperpowers.hasSuperpower(client.player, res)) {
                    renderLevelBar(matrixStack, lvl);
                }
            });
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawables.forEach(button -> {
            if (button instanceof AbilityButton) {
                this.renderAbilityDescription(matrixStack, mouseX, mouseY, (AbilityButton) button);
            }
        });
    }

    public void renderLevelBar(MatrixStack matrixStack, Level level) {
        matrixStack.push();
        RenderSystem.setShaderColor(1.0F, 0F, 0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        int height = top + 160;
        int width = left + 8;
        this.drawTexture(matrixStack, width, height, 0, 64, 182, 5);
        this.drawTexture(matrixStack, width, height, 0, 69, (int) Math.min(level.getExperience(), 182), 5);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

        String s = "" + level.getLevel();
        int i1 = (this.width - this.textRenderer.getWidth(s)) / 2;
        int j1 = height - 4;
        this.textRenderer.draw(matrixStack, s, (float) (i1 + 1), (float) j1, 0);
        this.textRenderer.draw(matrixStack, s, (float) (i1 - 1), (float) j1, 0);
        this.textRenderer.draw(matrixStack, s, (float) i1, (float) (j1 + 1), 0);
        this.textRenderer.draw(matrixStack, s, (float) i1, (float) (j1 - 1), 0);
        this.textRenderer.draw(matrixStack, s, (float) i1, (float) j1, -65536);
    }

    public void renderAbilityDescription(MatrixStack matrix, int mx, int my, AbilityButton button) {
        if (!(mx >= button.x && mx <= button.x + button.getWidth() && my >= button.y && my <= button.y + button.getHeight()))
            return;
        if (button.ability.getHoveredDescription() == null) return;
        int bgX = mx + button.descWidth > this.width ? mx - button.descWidth : mx + 15;
        int bgY = my + button.descHeight + 10 > this.height ? my - 5 - button.descHeight : my;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float color = 0.2F;

        builder.vertex(bgX, bgY + button.descHeight, 0).color(color, color, color, 0.8F).next();
        builder.vertex(bgX + button.descWidth, bgY + button.descHeight, 0).color(color, color, color, 0.8F).next();
        builder.vertex(bgX + button.descWidth, bgY, 0).color(color, color, color, 0.8F).next();
        builder.vertex(bgX, bgY, 0).color(color, color, color, 0.8F).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();

        for (int i = 0; i < button.abilityDescription.size(); i++) {
            String line = button.abilityDescription.get(i);
            this.textRenderer.drawWithShadow(matrix, line, bgX + 10, bgY + 10 + i * 13, 0xFFFFFF);
        }
        boolean activate = AbilityHelper.canActiveAbility(button.ability, this.client.player);
        this.textRenderer.drawWithShadow(matrix, activate ? "Ability can be activated" : "Ability cannot be activated", bgX + 10, bgY + 10 + (button.abilityDescription.size() + 1) * 13, activate ? 0x00FF00 : 0xFF0000);
    }

    public static class AbilityButton extends ButtonWidget {

        private final AbilitiesScreen parent;
        private final Ability ability;
        private List<String> abilityDescription = Lists.newArrayList();
        private int descWidth, descHeight;

        public AbilityButton(int x, int y, int id, AbilitiesScreen screen, Ability ability) {
            super(x + 25, y + 50 + 25 * id, 150, 20, LiteralText.EMPTY, AbilityButton::onPressed);
            this.parent = screen;
            this.ability = ability;
            this.prepareDescriptionRender();
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            MinecraftClient mc = MinecraftClient.getInstance();
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            Color color = AbilityHelper.getEnabled(this.ability.name, mc.player) ? hovered ? Color.ORANGE : Color.YELLOW :
                    hovered ? AbilityHelper.canActiveAbility(this.ability, mc.player) ? Color.GREEN : Color.RED : Color.WHITE;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, this.parent.BUTTON);
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            builder.vertex(x, y + height, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(0, 1).next();
            builder.vertex(x + width, y + height, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(1, 1).next();
            builder.vertex(x + width, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(1, 0).next();
            builder.vertex(x, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(0, 0).next();
            tessellator.draw();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            this.ability.drawIcon(stack, x + 2, y + 2);
            RenderSystem.disableBlend();
            String name = this.ability.getTitle().getString().length() > 20 ? this.ability.getTitle().getString().substring(0, 20) : this.ability.getTitle().getString();
            mc.textRenderer.draw(stack, name, x + 21, y + 7, 0);
            mc.textRenderer.draw(stack, name, x + 20, y + 6, 0xFFFFFFFF);
        }

        private static void onPressed(ButtonWidget button) {
            AbilityButton btn = (AbilityButton) button;
            if (!btn.ability.alwaysActive(btn.parent.client.player)) {
                if (AbilityHelper.getEnabled(btn.ability.name, btn.parent.client.player)) {
                    HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerDisableAbility(btn.ability.name));
                } else {
                    HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerEnableAbility(btn.ability.name, btn.ability.serializeNBT()));
                }
                btn.parent.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }

        private void prepareDescriptionRender() {
            abilityDescription.clear();
            if (this.ability.getHoveredDescription() == null) return;
            int maxWidth = 0;
            for (int i = 0; i < this.ability.getHoveredDescription().size(); i++) {
                String text = this.ability.getHoveredDescription().get(i).getString();
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
                int width = MinecraftClient.getInstance().textRenderer.getWidth(s);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            descWidth = maxWidth + 20;
            descHeight = abilityDescription.size() * 13 + 40;
        }
    }

    public static Identifier getTheme(String name) {
        final Identifier THEME = new Identifier(HeroesUnited.MODID, "textures/gui/themes/" + name + ".png");
        return THEME;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        if (value > 0) {
            INDEX--;
        } else {
            INDEX++;
        }
        init();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (Drawable widget : drawables) {
            if (widget instanceof ClickableWidget) {
                if (mouseButton == 0 && ((ClickableWidget) widget).mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        return false;
    }
}

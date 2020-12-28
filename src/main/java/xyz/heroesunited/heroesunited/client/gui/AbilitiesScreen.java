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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.server.ServerDisableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerEnableAbility;
import xyz.heroesunited.heroesunited.common.networking.server.ServerSetTheme;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For adding new themes, use @AbilityHelper.addTheme(modid, location);
 */
public class AbilitiesScreen extends Screen {

    private final ResourceLocation HEAD = new ResourceLocation(HeroesUnited.MODID, "textures/gui/head.png");
    private final ResourceLocation BUTTON = new ResourceLocation(HeroesUnited.MODID, "textures/gui/ability_button.png");
    public static List<ResourceLocation> themes = Lists.newArrayList(getTheme("default"), getTheme("black"), getTheme("rainbow"));
    public static int INDEX = 0;
    private int left, top;

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
        buttons.clear();
        left = (width - 200) / 2;
        top = (height - 170) / 2;
        IHUPlayer cap = HUPlayer.getCap(minecraft.player);

        this.addButton(new Button(left + 110, top + 5, 80, 20, new TranslationTextComponent("Change Theme"), (b) -> {
            if (cap.getTheme() >= themes.size())
                cap.setTheme(0);
            HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerSetTheme(cap.getTheme() + 1, themes.size()));
            minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }));
        List<Ability> abilities = getCurrentDisplayedAbilities(this.minecraft.player);
        for (int i = 0; i < abilities.size(); i++) {
            this.addButton(new AbilityButton(left, top, i, this, abilities.get(i)));
        }
    }

    public static List<Ability> getCurrentDisplayedAbilities(PlayerEntity player) {
        List<Ability> abilities = Lists.newArrayList();
        HUPlayer.getCap(player).getAbilities().values().stream()
                .filter(a -> a != null && !a.isHidden())
                .collect(Collectors.toList()).forEach(creator -> abilities.add(creator));
        List<Ability> list = Lists.newArrayList();

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
        IHUPlayer cap = HUPlayer.getCap(minecraft.player);
        this.renderBackground(matrixStack);
        matrixStack.push();
        ResourceLocation theme = getTheme("default");
        if (cap.getTheme() < themes.size()) {
            theme = themes.get(cap.getTheme());
        }
        minecraft.getTextureManager().bindTexture(theme);
        blit(matrixStack, left, top, 0, 0, 200, 170, 200, 170);
        if (getCurrentDisplayedAbilities(this.minecraft.player).isEmpty()) {
            this.drawCenteredString(matrixStack, this.font, "You don't have any ability yet", left + 95, top + 95, 16777215);
        }

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

        public AbilityButton(int x, int y, int id, AbilitiesScreen screen, Ability ability) {
            super(x + 25, y + 50 + 25 * id, 150, 20, StringTextComponent.EMPTY, AbilityButton::onPressed);
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
            String name = this.ability.getTitle().getString().length() > 20 ? this.ability.getTitle().getString().substring(0, 20) : this.ability.getTitle().getString();
            mc.fontRenderer.drawString(stack, name, x + 21, y + 7, 0);
            mc.fontRenderer.drawString(stack, name, x + 20, y + 6, 0xFFFFFFFF);
        }

        private static void onPressed(Button button) {
            AbilityButton btn = (AbilityButton) button;
            if (!btn.ability.alwaysActive()) {
                if (AbilityHelper.getEnabled(btn.ability, btn.parent.minecraft.player)) {
                    HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerDisableAbility(btn.ability.name));
                } else {
                    HUNetworking.INSTANCE.send(PacketDistributor.SERVER.noArg(), new ServerEnableAbility(btn.ability.name, btn.ability.serializeNBT()));
                }
                btn.parent.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
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
        for (Widget widget : buttons) {
            if (mouseButton == 0 && widget.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        return false;
    }
}

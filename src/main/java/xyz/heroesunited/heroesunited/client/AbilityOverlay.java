package xyz.heroesunited.heroesunited.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;

import java.util.List;
import java.util.Map;

public class AbilityOverlay implements IIngameOverlay {
    private final Map<String, Integer> NAMES_TIMER = Maps.newHashMap();

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
        gui.setupOverlayRenderState(true, false);
        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null && mc.player.isAlive()) {
            List<Ability> abilities = HUClientEventHandler.getCurrentDisplayedAbilities(mc.player);
            if (abilities.size() > 0) {
                final ResourceLocation widgets = new ResourceLocation(HeroesUnited.MODID, "textures/gui/widgets.png");
                int y = screenHeight / 3;

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                RenderSystem.setShaderTexture(0, widgets);
                GuiComponent.blit(poseStack, 0, y, 0, 0, 22, 102, 64, 128);

                for (int i = 0; i < abilities.size(); i++) {
                    Ability ability = AbilityHelper.getAnotherAbilityFromMap(AbilityHelper.getAbilities(mc.player), abilities.get(i));
                    int abilityY = y + 3 + i * 20;

                    poseStack.pushPose();
                    poseStack.translate(0, 0, 500D);
                    ability.drawIcon(poseStack, 3, abilityY);
                    poseStack.popPose();
                    RenderSystem.setShaderTexture(0, widgets);

                    if (ability.getMaxCooldown(mc.player) != 0) {
                        int progress = (int) ((ability.getDataManager().<Integer>getValue("prev_cooldown") + (ability.getDataManager().<Integer>getValue("cooldown") - ability.getDataManager().<Integer>getValue("prev_cooldown")) * partialTicks) / ability.getMaxCooldown(mc.player) * 16);
                        if (progress > 0) {
                            GuiComponent.blit(poseStack, 3, abilityY, 46, 0, progress, 16, 64, 128);
                        }
                    }
                    if (ability.getEnabled()) {
                        GuiComponent.blit(poseStack, -1, abilityY - 4, 22, 0, 24, 24, 64, 128);
                        NAMES_TIMER.putIfAbsent(ability.name, 80);
                    }

                    if (NAMES_TIMER.containsKey(ability.name)) {
                        int j = NAMES_TIMER.get(ability.name);
                        if (j > 0) {
                            NAMES_TIMER.replace(ability.name, --j);
                        } else {
                            if (!ability.getEnabled()) {
                                NAMES_TIMER.remove(ability.name);
                            }
                        }

                        float f = (float) j - mc.getFrameTime();
                        int j1 = 255;
                        if (j > 60F) {
                            float f6 = 80.0f - f;
                            j1 = (int)(f6 * 255.0F / 20F);
                        }

                        if (j <= 20F) {
                            j1 = (int)(f * 255.0F / 20F);
                        }

                        j1 = Mth.clamp(j1, 0, 255);

                        if (j1 > 8) {
                            mc.font.drawShadow(poseStack, ability.getTitle(), 26, abilityY + 3, 16777215 | (j1 << 24 & -16777216));
                        }
                    }
                    if (mc.screen instanceof ChatScreen && ability.getKey() != 0) {
                        KeyMapping keyBinding = ability.getKey() < 6 ? HUClientEventHandler.ABILITY_KEYS.get(ability.getKey() - 1) : ability.getKey() == 7 ? mc.options.keyJump : ability.getKey() == 8 ? mc.options.keyAttack : mc.options.keyUse;
                        PoseStack stack = new PoseStack();
                        stack.pushPose();
                        if (keyBinding.getKey().getDisplayName().getString().length() != 1) {
                            stack.translate(5, abilityY / 4F, 0);
                            stack.scale(0.75F, 0.75F, 1.0F);
                        }
                        mc.font.drawShadow(poseStack, keyBinding.getKey().getDisplayName(), 20, abilityY + 12, 0xdfdfdf);
                        stack.popPose();
                    }
                }
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();

                if (!NAMES_TIMER.isEmpty()) {
                    for (String s : NAMES_TIMER.keySet()) {
                        boolean contains = false;
                        for (Ability ability : abilities) {
                            if (ability.name.equals(s)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            NAMES_TIMER.remove(s);
                        }
                    }
                }
            }
        }
    }
}

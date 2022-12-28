package xyz.heroesunited.heroesunited.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbilityOverlay implements IGuiOverlay {

    private static final Map<String, Integer> NAMES_TIMER = Maps.newConcurrentMap();
    private static final ResourceLocation WIDGETS = new ResourceLocation(HeroesUnited.MODID, "textures/gui/widgets.png");
    private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
    protected static int INDEX = 0;

    @Override
    public void render(ForgeGui gui, PoseStack mStack, float partialTicks, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.isAlive()) {
            List<Ability> abilities = getCurrentDisplayedAbilities(mc.player);
            if (!abilities.isEmpty()) {

                int x = HUConfig.CLIENT.rightSideAbilityBar.get() ? width - 27 : 3, y = height / 2 - ((int) (abilities.size() * 24F) / 2);
                for (int i = 0; i < abilities.size(); i++) {
                    Ability ability = AbilityHelper.getSameAbilityFrom(AbilityHelper.getAbilities(mc.player), abilities.get(i));
                    int abilityY = y + 3 + i * 24;

                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();

                    if (AbilityOverlay.getAbilities(mc.player).size() > abilities.size() &&
                            (i == 0 || i == abilities.size() - 1)) {
                        RenderSystem.setShaderColor(1F, 1F, 1F, 0.5F);
                        RenderSystem.setShaderTexture(0, ICON_OVERLAY_LOCATION);
                        if (i == 0) {
                            GuiComponent.blit(mStack, x - 12, abilityY - 18, 96.0F, 0.0F, 32, 32, 256, 256);
                        }

                        if (i == abilities.size() - 1) {
                            GuiComponent.blit(mStack, x - 12, abilityY + 2, 64.0F, 0.0F, 32, 32, 256, 256);
                        }
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                    RenderSystem.setShaderTexture(0, AbilityOverlay.WIDGETS);
                    GuiComponent.blit(mStack, x, abilityY - 3, 0, 0, 22, 22, 64, 24);

                    mStack.pushPose();
                    ability.getClientProperties().drawIcon(mStack, ability.getJsonObject(), x + 3, abilityY);
                    mStack.popPose();

                    mStack.pushPose();
                    mStack.translate(0, 0, 500D);
                    RenderSystem.setShaderTexture(0, AbilityOverlay.WIDGETS);
                    if (ability.getMaxCooldown() != 0) {
                        int progress = (int) (ability.getCooldownProgress(partialTicks) * 16F);
                        if (progress > 0) {
                            GuiComponent.blit(mStack, x + 3, abilityY, 46, 0, progress, 16, 64, 24);
                        }
                    }
                    if (ability.getEnabled()) {
                        GuiComponent.blit(mStack, x - 1, abilityY - 4, 22, 0, 24, 24, 64, 24);
                        NAMES_TIMER.putIfAbsent(ability.name, 100);
                    }
                    mStack.popPose();
                    RenderSystem.disableBlend();
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
                        if (j > 80) {
                            j1 = (int) ((100F - f) * 255.0F / 20F);
                        }

                        if (j <= 20) {
                            j1 = (int) (f * 255.0F / 20F);
                        }

                        j1 = Mth.clamp(j1, 0, 255);

                        if (j1 > 8) {
                            mc.font.drawShadow(mStack, ability.getTitle(), HUConfig.CLIENT.rightSideAbilityBar.get() ? x - 32 - ability.getTitle().getString().length() : x + 26, abilityY + 3, 16777215 | (j1 << 24 & -16777216));
                        }
                    }
                    int key = ability.getKey();
                    if (mc.screen instanceof ChatScreen && key != 0) {
                        if (key == -1) {
                            key = i + 1;
                        }
                        KeyMapping keyBinding = key < 6 ? ClientEventHandler.ABILITY_KEYS.get(key - 1) : key == 7 ? mc.options.keyJump : key == 8 ? mc.options.keyAttack : mc.options.keyUse;
                        mStack.pushPose();
                        mc.font.drawShadow(mStack, keyBinding.getKey().getDisplayName(), HUConfig.CLIENT.rightSideAbilityBar.get() ? x - 3 : x + 20, abilityY + 12, 0xdfdfdf);
                        mStack.popPose();
                    }
                }


                if (!NAMES_TIMER.isEmpty()) {
                    for (String s : NAMES_TIMER.keySet()) {
                        if (abilities.stream().noneMatch(ability -> ability.name.equals(s))) {
                            NAMES_TIMER.remove(s);
                        }
                    }
                }
            }
        }
    }


    public static List<Ability> getCurrentDisplayedAbilities(Player player) {
        List<Ability> abilities = AbilityOverlay.getAbilities(player), list = new ArrayList<>();

        if (abilities.isEmpty()) {
            return list;
        }

        if (INDEX >= abilities.size()) {
            INDEX = 0;
        } else if (INDEX < 0) {
            INDEX = abilities.size() - 1;
        }

        int i = INDEX, added = 0, maxIndex = 5;
        Superpower power = HUPackSuperpowers.getSuperpowerFrom(player);
        if (power != null && power.jsonObject.has("max_abilities_display")) {
            maxIndex = GsonHelper.getAsInt(power.jsonObject, "max_abilities_display");
        }

        while (list.size() < maxIndex && added < abilities.size()) {
            if (i >= abilities.size()) {
                i = 0;
            }
            if (abilities.get(i).getConditionManager().isEnabled(player, "visibleInOverlay")) {
                list.add(abilities.get(i));
            }
            i++;
            added++;
        }
        return list;
    }

    public static List<Ability> getAbilities(Player player) {
        return AbilityHelper.getAbilityMap(player).values().stream()
                .filter(a -> a != null && a.isVisible()
                        && (GsonHelper.getAsBoolean(a.getJsonObject(), "show_deactivated", false)
                        || a.getConditionManager().isEnabled(player, "canActivate") && a.getConditionManager().isEnabled(player, "canBeEnabled")))
                .collect(Collectors.toList());
    }
}

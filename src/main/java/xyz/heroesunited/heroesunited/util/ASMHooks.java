package xyz.heroesunited.heroesunited.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.HandSide;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

public class ASMHooks {

    public static void renderRightArm(PlayerRenderer playerRenderer, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity player) {
        if (MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Pre(player, playerRenderer, matrixStack, bufferIn, combinedLightIn, HandSide.RIGHT))) return;
        boolean renderArm = true;
        for (AbilityType type : AbilityHelper.getAbilities(player)) {
            type.create().renderFirstPersonArm(playerRenderer, matrixStack, bufferIn, combinedLightIn, player, HandSide.RIGHT);
            renderArm = type.create().renderFirstPersonArm(player);
            break;
        }
        if (renderArm) {
            playerRenderer.renderRightArm(matrixStack, bufferIn, combinedLightIn, player);
            if (Suit.getSuit(player) != null) {
                Suit.getSuit(player).renderFirstPersonArm(playerRenderer, matrixStack, bufferIn, combinedLightIn, player, HandSide.RIGHT);
            }
        }
        MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Post(player, playerRenderer, matrixStack, bufferIn, combinedLightIn, HandSide.RIGHT));
    }

    public static void renderLeftArm(PlayerRenderer playerRenderer, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity player) {
        if (MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Pre(player, playerRenderer, matrixStack, bufferIn, combinedLightIn, HandSide.LEFT))) return;

        boolean renderArm = true;
        for (AbilityType type : AbilityHelper.getAbilities(player)) {
            type.create().renderFirstPersonArm(playerRenderer, matrixStack, bufferIn, combinedLightIn, player, HandSide.LEFT);
            renderArm = type.create().renderFirstPersonArm(player);
            break;
        }
        if (renderArm) {
            playerRenderer.renderLeftArm(matrixStack, bufferIn, combinedLightIn, player);
            if (Suit.getSuit(player) != null) {
                Suit.getSuit(player).renderFirstPersonArm(playerRenderer, matrixStack, bufferIn, combinedLightIn, player, HandSide.LEFT);
            }
        }
        MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Post(player, playerRenderer, matrixStack, bufferIn, combinedLightIn, HandSide.LEFT));
    }
}

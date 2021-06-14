package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;

public interface IAlwaysRenderer {

    @OnlyIn(Dist.CLIENT)
    default void renderAlways(PlayerRenderer playerRenderer, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float v, float v1, float v2, float v3, float v4, float v5) {

    }

    @OnlyIn(Dist.CLIENT)
    default void setAlwaysRotationAngles(HUSetRotationAnglesEvent event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void renderPlayerPreAlways(RenderPlayerEvent.Pre event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void renderPlayerPostAlways(RenderPlayerEvent.Post event) {
    }

    @OnlyIn(Dist.CLIENT)
    default void renderAlwaysFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {

    }
}

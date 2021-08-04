package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;

public interface IAlwaysRenderer {

    @OnlyIn(Dist.CLIENT)
    default void renderAlways(PlayerEntityRenderer playerRenderer, MatrixStack matrixStack, VertexConsumerProvider iRenderTypeBuffer, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float v, float v1, float v2, float v3, float v4, float v5) {

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
    default void renderAlwaysFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, Arm side) {

    }
}

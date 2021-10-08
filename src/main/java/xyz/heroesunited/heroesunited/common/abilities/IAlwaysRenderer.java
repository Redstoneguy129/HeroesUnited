package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;

public interface IAlwaysRenderer {

    @OnlyIn(Dist.CLIENT)
    default void renderAlways(PlayerRenderer playerRenderer, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer, int i, AbstractClientPlayer AbstractClientPlayer, float v, float v1, float v2, float v3, float v4, float v5) {

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
    default void renderAlwaysFirstPersonArm(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {

    }
}

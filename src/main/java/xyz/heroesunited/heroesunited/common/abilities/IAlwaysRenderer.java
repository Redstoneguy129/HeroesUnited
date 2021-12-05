package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;

public interface IAlwaysRenderer {

    @OnlyIn(Dist.CLIENT)
    default void renderAlways(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @OnlyIn(Dist.CLIENT)
    default void setAlwaysRotationAngles(SetupAnimEvent event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void renderPlayerPreAlways(RenderPlayerEvent.Pre event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void renderPlayerPostAlways(RenderPlayerEvent.Post event) {
    }

    @OnlyIn(Dist.CLIENT)
    default void renderAlwaysFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {

    }
}

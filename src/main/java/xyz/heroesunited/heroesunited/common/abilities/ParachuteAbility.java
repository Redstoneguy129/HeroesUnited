package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.ParachuteModel;

import java.util.Map;

public class ParachuteAbility extends JSONAbility {

    public ParachuteAbility() {
        super(AbilityType.PARACHUTE);
    }

    @Override
    public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
        if(!getEnabled())
            super.onKeyInput(player, map);
    }

    @Override
    public void action(PlayerEntity player) {
        if (usingParachute(player)) {
            Vec3d vec = player.getVelocity();
            if(vec.y > -1){
                player.fallDistance = 0;
            }
            vec = vec.multiply(0.99F, 0.93F, 0.99F);
            player.setVelocity(vec.x, vec.y, vec.z);
            syncToAll(player);
        } else {
            setEnabled(player, false);
        }
    }

    public boolean usingParachute(PlayerEntity player) {
        return player.getVelocity().y < 0F && !player.abilities.flying && !player.isOnGround() && !player.isTouchingWater() && !player.isFallFlying() && getEnabled() && player.world.getRegistryKey() != HeroesUnited.SPACE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(usingParachute(player))
            new ParachuteModel().render(matrix, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(HeroesUnited.MODID, "textures/suits/parachute.png"))), packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

	@OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if(usingParachute(event.getPlayer())){
            event.getPlayerModel().leftLeg.pitch = 0;
            event.getPlayerModel().rightLeg.pitch = 0;
            event.getPlayerModel().leftArm.pitch = 0;
            event.getPlayerModel().rightArm.pitch = 0;
        }
    }
}

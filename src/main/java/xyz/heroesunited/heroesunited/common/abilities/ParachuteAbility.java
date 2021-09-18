package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.ParachuteModel;

import java.util.Map;

public class ParachuteAbility extends JSONAbility {

    public ParachuteAbility(AbilityType type) {
        super(type);
    }

    @Override
    public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
        if(!getEnabled())
            super.onKeyInput(player, map);
    }

    @Override
    public void action(PlayerEntity player) {
        if (usingParachute(player)) {
            Vector3d vec = player.getDeltaMovement();
            if(vec.y > -1){
                player.fallDistance = 0;
            }
            vec = vec.multiply(0.99F, 0.93F, 0.99F);
            player.setDeltaMovement(vec.x, vec.y, vec.z);
            syncToAll(player);
        } else {
            setEnabled(player, false);
        }
    }

    public boolean usingParachute(PlayerEntity player) {
        return player.getDeltaMovement().y < 0F && !player.abilities.flying && !player.isOnGround() && !player.isInWater() && !player.isFallFlying() && getEnabled() && player.level.dimension() != HeroesUnited.SPACE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(usingParachute(player))
            new ParachuteModel().renderToBuffer(matrix, bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(HeroesUnited.MODID, "textures/suits/parachute.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }

	@OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
        if(usingParachute(event.getPlayer())){
            event.getPlayerModel().leftLeg.xRot = 0;
            event.getPlayerModel().rightLeg.xRot = 0;
            event.getPlayerModel().leftArm.xRot = 0;
            event.getPlayerModel().rightArm.xRot = 0;
        }
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.client.render.model.ParachuteModel;
import xyz.heroesunited.heroesunited.util.HUModelLayers;

import java.util.Map;

public class ParachuteAbility extends JSONAbility {

    public ParachuteAbility() {
        super(AbilityType.PARACHUTE);
    }

    @Override
    public void onKeyInput(Player player, Map<Integer, Boolean> map) {
        if(!getEnabled())
            super.onKeyInput(player, map);
    }

    @Override
    public void action(Player player) {
        if (usingParachute(player)) {
            Vec3 vec = player.getDeltaMovement();
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

    public boolean usingParachute(Player player) {
        return player.getDeltaMovement().y < 0F && !player.getAbilities().flying && !player.isOnGround() && !player.isInWater() && !player.isFallFlying() && getEnabled() && player.level.dimension() != HeroesUnited.SPACE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(usingParachute(player))
            new ParachuteModel(Minecraft.getInstance().getEntityModels().bakeLayer(HUModelLayers.PARACHUTE)).renderToBuffer(matrix, bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(HeroesUnited.MODID, "textures/suits/parachute.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
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

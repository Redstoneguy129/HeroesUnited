package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.client.model.ParachuteModel;
import xyz.heroesunited.heroesunited.util.HUModelLayers;

import java.util.Map;
import java.util.function.Consumer;

public class ParachuteAbility extends JSONAbility {

    public ParachuteAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void onKeyInput(Player player, Map<Integer, Boolean> map) {
        if (!getEnabled())
            super.onKeyInput(player, map);
    }

    @Override
    public void action(Player player) {
        if (usingParachute(player)) {
            Vec3 vec = player.getDeltaMovement();
            if (vec.y > -1) {
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

    @Override
    public void initializeClient(Consumer<IAbilityClientProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IAbilityClientProperties() {
            private final ParachuteModel model = new ParachuteModel(Minecraft.getInstance().getEntityModels().bakeLayer(HUModelLayers.PARACHUTE));

            @Override
            public void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (usingParachute(player))
                    this.model.renderToBuffer(poseStack, bufferIn.getBuffer(RenderType.entityTranslucent(new ResourceLocation(HeroesUnited.MODID, "textures/suits/parachute.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
            }

            @Override
            public void setupAnim(SetupAnimEvent event) {
                if (usingParachute(event.getPlayer())) {
                    event.getPlayerModel().leftLeg.xRot = 0;
                    event.getPlayerModel().rightLeg.xRot = 0;
                    event.getPlayerModel().leftArm.xRot = 0;
                    event.getPlayerModel().rightArm.xRot = 0;
                }
            }
        });
    }
}

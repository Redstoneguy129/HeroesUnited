package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.BoneAnimation;
import software.bernie.geckolib.util.RenderUtils;
import xyz.heroesunited.heroesunited.client.events.RenderLayerEvent;
import xyz.heroesunited.heroesunited.client.events.RenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.client.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.PlayerGeoModel;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.Arrays;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("HEAD"), cancellable = true)
    private void renderHandPre(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer) (Object) this);
        if (MinecraftForge.EVENT_BUS.post(new RenderPlayerHandEvent.Pre(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? HumanoidArm.RIGHT : HumanoidArm.LEFT))) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderHand", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/geom/ModelPart;xRot:F", opcode = Opcodes.PUTFIELD))
    private void removeArmWearRotation(ModelPart instance, float value) {

    }

    @SuppressWarnings("unchecked")
    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private void renderHandPost(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer) (Object) this);
        HumanoidArm side = rendererArmIn == playerRenderer.getModel().rightArm ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
        boolean renderArm = true;
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();

        player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            cap.getAnimatedModel().getBakedModel(cap.getAnimatedModel().getModelResource(cap));

            AnimationState<IHUPlayer> animationState = new AnimationState<>(cap, 0, 0, Minecraft.getInstance().getFrameTime(), false);
            long instanceId = player.getUUID().hashCode();

            animationState.setData(DataTickets.TICK, cap.getTick(player));
            animationState.setData(DataTickets.ENTITY, player);
            animationState.setData(PlayerGeoModel.PLAYER_MODEL_DATA, new PlayerGeoModel.ModelData(playerRenderer.getModel()));
            cap.getAnimatedModel().addAdditionalStateData(cap, instanceId, animationState::setData);
            cap.getAnimatedModel().handleAnimations(cap, instanceId, animationState);

            for (AnimationController<?> controller : cap.getAnimatableInstanceCache().getManagerForId(instanceId).getAnimationControllers().values()) {
                if (controller.getCurrentAnimation() != null && controller.getAnimationState() != AnimationController.State.STOPPED) {
                    for (String s : Arrays.asList("player", "bipedRightArm", "bipedLeftArm")) {
                        cap.getAnimatedModel().getBone(s).ifPresent(bone -> {
                            for (BoneAnimation boneAnimation : controller.getCurrentAnimation().animation().boneAnimations()) {
                                if (boneAnimation.boneName().equals(s)) {
                                    if (s.equals("player")) {
                                        RenderUtils.prepMatrixForBone(matrixStackIn, bone);
                                        break;
                                    }
                                    HUClientUtil.setupPlayerBones(bone, HUClientUtil.getModelRendererById(playerRenderer.getModel(), s), false);
                                }
                            }
                        });
                    }

                }
            }
        });
        for (Ability ability : AbilityHelper.getAbilities(player)) {
            if (!ability.getClientProperties().renderFirstPersonArm(modelSet, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side)) {
                renderArm = false;
                break;
            }
        }

        if (Suit.getSuit(player) != null) {
            rendererArmwearIn.visible = false;
        }

        if (!renderArm) {
            rendererArmIn.visible = rendererArmwearIn.visible = false;
        }

        if (rendererArmIn.visible) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack stack = player.getItemBySlot(equipmentSlot);
                if (stack.getItem() instanceof SuitItem suitItem) {
                    if (suitItem.getSlot().equals(equipmentSlot)) {
                        suitItem.renderFirstPersonArm(modelSet, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side, stack);
                    }
                }
            }
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                for (int slot = 0; slot < cap.getInventory().getContainerSize(); ++slot) {
                    ItemStack stack = cap.getInventory().getItem(slot);
                    if (stack != null && stack.getItem() instanceof IAccessory accessory && !MinecraftForge.EVENT_BUS.post(new RenderLayerEvent.Accessories(Minecraft.getInstance().getEntityModels(), playerRenderer, player, matrixStackIn, bufferIn, combinedLightIn, 0, 0, 0, 0, 0, 0))) {
                        boolean shouldRender = true;
                        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                            SuitItem item = Suit.getSuitItem(equipmentSlot, player);
                            if (item != null && item.getSuit().getSlotForHide(equipmentSlot).contains(EquipmentAccessoriesSlot.getFromSlotIndex(slot))) {
                                shouldRender = false;
                            }
                        }
                        if (shouldRender) {
                            if (accessory.getHiddenParts(true) != null) {
                                for (PlayerPart part : accessory.getHiddenParts(true)) {
                                    part.setVisibility(playerRenderer.getModel(), false, accessory.getPlayerWearSize(stack));
                                }
                            }
                            if (accessory.renderDefaultModel()) {
                                SuitModel<AbstractClientPlayer> suitModel = new SuitModel<>(player, accessory.getScale(stack));
                                suitModel.renderArm(modelSet, side, matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(accessory.getTexture(stack, player, EquipmentAccessoriesSlot.getFromSlotIndex(slot)))), combinedLightIn, playerRenderer.getModel());
                            } else {
                                accessory.renderFirstPersonArm(modelSet, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side, stack, slot);
                            }
                        }
                    }
                }
            });
        }
        MinecraftForge.EVENT_BUS.post(new RenderPlayerHandEvent.Post(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, side));
        HUClientUtil.copyAnglesToWear(playerRenderer.getModel());
    }
}

package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("HEAD"), cancellable = true)
    private void renderItemPre(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer) (Object) this);
        if (MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Pre(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? HumanoidArm.RIGHT : HumanoidArm.LEFT))) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void renderItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        boolean renderArm = true;
        for (Ability ability : AbilityHelper.getAbilities(player)) {
            if (!ability.renderFirstPersonArm(player)) {
                renderArm = false;
                break;
            }
        }
        if (Suit.getSuit(player) != null) {
            rendererArmwearIn.visible = false;
        }

        if (!renderArm) {
            rendererArmIn.visible = false;
            rendererArmwearIn.visible = false;
        }
    }

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("TAIL"))
    private void renderItemPost(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer) (Object) this);
        MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Post(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? HumanoidArm.RIGHT : HumanoidArm.LEFT));
        HumanoidArm side = rendererArmIn == playerRenderer.getModel().rightArm ? HumanoidArm.RIGHT : HumanoidArm.LEFT;

        for (Ability ability : AbilityHelper.getAbilities(player)) {
            ability.renderFirstPersonArm(playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side);
        }

        if (rendererArmIn.visible) {
            if (Suit.getSuit(player) != null) {
                Suit.getSuit(player).renderFirstPersonArm(playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side);
            }
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                for (int slot = 0; slot < cap.getInventory().getContainerSize(); ++slot) {
                    ItemStack stack = cap.getInventory().getItem(slot);
                    if (stack != null && stack.getItem() instanceof IAccessory && !MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Accessories(playerRenderer, player, matrixStackIn, bufferIn, combinedLightIn, 0, 0, 0, 0, 0, 0))) {
                        IAccessory accessoire = ((IAccessory) stack.getItem());
                        boolean shouldRender = true;
                        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                            SuitItem item = Suit.getSuitItem(equipmentSlot, player);
                            if (item != null && item.getSuit().getSlotForHide(equipmentSlot).contains(EquipmentAccessoriesSlot.getFromSlotIndex(slot))) {
                                shouldRender = false;
                            }
                        }
                        if (shouldRender) {
                            if (accessoire.renderDefaultModel()) {
                                SuitModel suitModel = new SuitModel(HUClientUtil.getSuitModelPart(player), HUPlayerUtil.haveSmallArms(player));
                                suitModel.copyPropertiesFrom(playerRenderer.getModel());
                                suitModel.renderArm(side, matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(accessoire.getTexture(stack, player, EquipmentAccessoriesSlot.getFromSlotIndex(slot)))), combinedLightIn, player);
                            } else {
                                accessoire.renderFirstPersonArm(playerRenderer, matrixStackIn, bufferIn, combinedLightIn, player, side, stack, slot);
                            }
                        }
                    }
                }
            });
        }
    }
}

package xyz.heroesunited.heroesunited.mixin.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelSuit;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerRenderer {

    @Inject(method = "renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V", at = @At("HEAD"), cancellable = true)
    private void renderItemPre(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        PlayerEntityRenderer playerRenderer = ((PlayerEntityRenderer) (Object) this);
        if (MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Pre(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? Arm.RIGHT : Arm.LEFT))) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/renderer/model/ModelRenderer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;II)V"))
    private void renderItem(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
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

    @Inject(method = "renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V", at = @At("TAIL"))
    private void renderItemPost(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo ci) {
        PlayerEntityRenderer playerRenderer = ((PlayerEntityRenderer) (Object) this);
        MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Post(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? Arm.RIGHT : Arm.LEFT));
        Arm side = rendererArmIn == playerRenderer.getModel().rightArm ? Arm.RIGHT : Arm.LEFT;

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
                            if (accessoire.renderDefaultModel()) {//accessoire.getScale(stack)
                                SuitModel suitModel = new SuitModel(HUPlayerUtil.haveSmallArms(player));
                                suitModel.copyPropertiesFrom(playerRenderer.getModel());
                                suitModel.renderArm(side, matrixStackIn, bufferIn.getBuffer(RenderLayer.getEntityTranslucent(accessoire.getTexture(stack, player, EquipmentAccessoriesSlot.getFromSlotIndex(slot)))), combinedLightIn, player);
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

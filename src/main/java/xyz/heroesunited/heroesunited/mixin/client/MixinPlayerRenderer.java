package xyz.heroesunited.heroesunited.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerHandEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelSuit;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lnet/minecraft/client/renderer/model/ModelRenderer;Lnet/minecraft/client/renderer/model/ModelRenderer;)V", at = @At("HEAD"), cancellable = true)
    private void renderItemPre(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelRenderer rendererArmIn, ModelRenderer rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer) (Object) this);
        if (MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Pre(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? HandSide.RIGHT : HandSide.LEFT))) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lnet/minecraft/client/renderer/model/ModelRenderer;Lnet/minecraft/client/renderer/model/ModelRenderer;)V", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/renderer/model/ModelRenderer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;II)V"))
    private void renderItem(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelRenderer rendererArmIn, ModelRenderer rendererArmwearIn, CallbackInfo ci) {
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

    @Inject(method = "renderHand(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lnet/minecraft/client/renderer/model/ModelRenderer;Lnet/minecraft/client/renderer/model/ModelRenderer;)V", at = @At("TAIL"))
    private void renderItemPost(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity player, ModelRenderer rendererArmIn, ModelRenderer rendererArmwearIn, CallbackInfo ci) {
        PlayerRenderer playerRenderer = ((PlayerRenderer) (Object) this);
        MinecraftForge.EVENT_BUS.post(new HURenderPlayerHandEvent.Post(player, playerRenderer, matrixStackIn, bufferIn, combinedLightIn, rendererArmIn == playerRenderer.getModel().rightArm ? HandSide.RIGHT : HandSide.LEFT));
        HandSide side = rendererArmIn == playerRenderer.getModel().rightArm ? HandSide.RIGHT : HandSide.LEFT;

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
                        for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
                            SuitItem item = Suit.getSuitItem(equipmentSlot, player);
                            if (item != null && item.getSuit().getSlotForHide(equipmentSlot).contains(EquipmentAccessoriesSlot.getFromSlotIndex(slot))) {
                                shouldRender = false;
                            }
                        }
                        if (shouldRender) {
                            if (accessoire.renderDefaultModel()) {
                                ModelSuit suitModel = new ModelSuit(accessoire.getScale(stack), HUPlayerUtil.haveSmallArms(player));
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

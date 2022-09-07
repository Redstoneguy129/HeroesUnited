package xyz.heroesunited.heroesunited.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.RenderLayerEvent;
import xyz.heroesunited.heroesunited.client.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;

public class HULayerRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private final LivingEntityRenderer<T, M> renderer;
    private final EntityRendererProvider.Context context;

    public HULayerRenderer(EntityRendererProvider.Context context, LivingEntityRenderer<T, M> renderer) {
        super(renderer);
        this.context = context;
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (renderer instanceof PlayerRenderer playerRenderer && entity instanceof AbstractClientPlayer player) {
            AbilityHelper.getAbilities(player).forEach(ability -> ability.getClientProperties().render(this.context, playerRenderer, matrixStack, buffer, packedLight, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
            MinecraftForge.EVENT_BUS.post(new RenderLayerEvent.Player(this.context, playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
        }

        entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
            for (int slot = 0; slot < cap.getInventory().getContainerSize(); ++slot) {
                ItemStack stack = cap.getInventory().getItem(slot);
                if (stack != null && stack.getItem() instanceof IAccessory accessory && !MinecraftForge.EVENT_BUS.post(new RenderLayerEvent.Accessories<>(this.context.getModelSet(), renderer, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch))) {
                    boolean shouldRender = true;
                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        SuitItem item = Suit.getSuitItem(equipmentSlot, entity);
                        if (item != null && item.getSuit().getSlotForHide(equipmentSlot).contains(EquipmentAccessoriesSlot.getFromSlotIndex(slot))) {
                            shouldRender = false;
                        }
                    }
                    if (shouldRender) {
                        if (accessory.renderDefaultModel()) {
                            renderAccessories(new SuitModel<>(entity, accessory.getScale(stack)), matrixStack, buffer, packedLight, entity, renderer, accessory, stack, cap, EquipmentAccessoriesSlot.getFromSlotIndex(slot));
                        } else {
                            accessory.render(this.context, renderer, matrixStack, buffer, packedLight, entity, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, slot);
                        }
                    }
                }
            }
        });

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                renderSuit(matrixStack, buffer, entity, slot, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }

    private void renderAccessories(SuitModel<T> suitModel, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, LivingEntity entity, LivingEntityRenderer<T, M> livingRenderer, IAccessory accessoire, ItemStack stack, IHUPlayer cap, EquipmentAccessoriesSlot slot) {
        suitModel.setAllVisible(false);
        if (slot == EquipmentAccessoriesSlot.HELMET) {
            suitModel.hat.visible = suitModel.head.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.JACKET) {
            suitModel.hat.visible = suitModel.head.visible =
                    suitModel.body.visible = suitModel.jacket.visible =
                            suitModel.leftSleeve.visible = suitModel.leftArm.visible =
                                    suitModel.rightSleeve.visible = suitModel.rightArm.visible =
                                            suitModel.leftPants.visible = suitModel.leftLeg.visible =
                                    suitModel.rightPants.visible = suitModel.rightLeg.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.TSHIRT) {
            suitModel.hat.visible = suitModel.head.visible =
                    suitModel.body.visible = suitModel.jacket.visible =
                            suitModel.leftSleeve.visible = suitModel.leftArm.visible =
                                    suitModel.rightSleeve.visible = suitModel.rightArm.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.RIGHT_WRIST || slot == EquipmentAccessoriesSlot.LEFT_WRIST || slot == EquipmentAccessoriesSlot.GLOVES) {
            suitModel.body.visible = suitModel.jacket.visible =
                    suitModel.leftSleeve.visible = suitModel.leftArm.visible =
                            suitModel.rightSleeve.visible = suitModel.rightArm.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.PANTS || slot == EquipmentAccessoriesSlot.SHOES) {
            suitModel.leftPants.visible = suitModel.rightPants.visible = suitModel.leftLeg.visible =
                    suitModel.rightLeg.visible = cap.getInventory().haveStack(slot);
        }



        suitModel.copyPropertiesFrom(livingRenderer.getModel());
        suitModel.renderToBuffer(matrixStack, buffer.getBuffer(RenderType.entityTranslucent(accessoire.getTexture(stack, entity, slot))), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private void renderSuit(PoseStack stack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof SuitItem suitItem) {
            if (suitItem.getSlot() == slot) {
                suitItem.getSuit().renderLayer(this.context, this.renderer, entity, itemstack, slot, stack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }
}

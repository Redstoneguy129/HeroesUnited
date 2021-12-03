package xyz.heroesunited.heroesunited.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;

public class HULayerRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public LivingEntityRenderer<T, M> entityRendererIn;

    public HULayerRenderer(LivingEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
        this.entityRendererIn = entityRendererIn;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Pre(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch)))
            return;

        if (entityRendererIn instanceof PlayerRenderer && entity instanceof AbstractClientPlayer) {
            PlayerRenderer playerRenderer = (PlayerRenderer) entityRendererIn;
            AbstractClientPlayer player = (AbstractClientPlayer) entity;
            AbilityHelper.getAbilities(player).forEach(ability -> ability.render(playerRenderer, matrixStack, buffer, packedLight, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                for (int slot = 0; slot < cap.getInventory().getContainerSize(); ++slot) {
                    ItemStack stack = cap.getInventory().getItem(slot);
                    if (stack != null && stack.getItem() instanceof IAccessory && !MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Accessories(playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch))) {
                        IAccessory accessoire = ((IAccessory) stack.getItem());
                        SuitModel<AbstractClientPlayer> suitModel = new SuitModel<>(player);
                        boolean shouldRender = true;
                        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                            SuitItem item = Suit.getSuitItem(equipmentSlot, player);
                            if (item != null && item.getSuit().getSlotForHide(equipmentSlot).contains(EquipmentAccessoriesSlot.getFromSlotIndex(slot))) {
                                shouldRender = false;
                            }
                        }
                        if (shouldRender) {
                            if (accessoire.renderDefaultModel()) {
                                renderAccessories(suitModel, matrixStack, buffer, packedLight, player, playerRenderer, accessoire, stack, cap, EquipmentAccessoriesSlot.getFromSlotIndex(slot));
                            } else {
                                accessoire.render(playerRenderer, matrixStack, buffer, packedLight, player, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, slot);
                            }
                        }
                    }
                }
            });
            MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Player(playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                renderSuit(matrixStack, buffer, entity, slot, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Post(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
    }

    private void renderAccessories(SuitModel<AbstractClientPlayer> suitModel, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, Player player, PlayerRenderer playerRenderer, IAccessory accessoire, ItemStack stack, IHUPlayer cap, EquipmentAccessoriesSlot slot) {
        suitModel.setAllVisible(false);
        if (slot == EquipmentAccessoriesSlot.HELMET) {
            suitModel.hat.visible = suitModel.head.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.JACKET) {
            suitModel.hat.visible = suitModel.head.visible =
                    suitModel.body.visible = suitModel.jacket.visible =
                            suitModel.leftSleeve.visible = suitModel.leftArm.visible =
                                    suitModel.rightSleeve.visible = suitModel.rightArm.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.TSHIRT) {
            suitModel.hat.visible = suitModel.head.visible =
                    suitModel.body.visible = suitModel.jacket.visible =
                            suitModel.leftSleeve.visible = suitModel.leftArm.visible =
                                    suitModel.rightSleeve.visible = suitModel.rightArm.visible = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoriesSlot.RIGHT_WRIST || slot == EquipmentAccessoriesSlot.LEFT_WRIST || slot == EquipmentAccessoriesSlot.GLOVES) {
            suitModel.body.visible = suitModel.jacket.visible =
                    suitModel.leftSleeve.visible = suitModel.leftArm.visible =
                            suitModel.rightSleeve.visible = suitModel.rightArm.visible = cap.getInventory().haveStack(slot);
        }

        suitModel.leftPants.visible = suitModel.rightPants.visible = suitModel.leftLeg.visible =
                suitModel.rightLeg.visible = slot == EquipmentAccessoriesSlot.PANTS && cap.getInventory().haveStack(slot)
                        || slot == EquipmentAccessoriesSlot.SHOES && cap.getInventory().haveStack(EquipmentAccessoriesSlot.SHOES);

        suitModel.copyPropertiesFrom(playerRenderer.getModel());
        suitModel.renderToBuffer(matrixStack, buffer.getBuffer(RenderType.entityTranslucent(accessoire.getTexture(stack, player, slot))), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private void renderSuit(PoseStack stack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof SuitItem) {
            SuitItem suitItem = (SuitItem) itemstack.getItem();
            if (suitItem.getSlot() == slot) {
                suitItem.getSuit().renderLayer(entityRendererIn, entity, itemstack, slot, stack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }
}

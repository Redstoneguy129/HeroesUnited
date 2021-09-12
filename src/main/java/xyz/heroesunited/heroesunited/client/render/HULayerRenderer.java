package xyz.heroesunited.heroesunited.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelSuit;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessory;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class HULayerRenderer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    public LivingRenderer<T, M> entityRendererIn;

    public HULayerRenderer(LivingRenderer entityRendererIn) {
        super(entityRendererIn);
        this.entityRendererIn = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Pre(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch)))
            return;

        if (entityRendererIn instanceof PlayerRenderer && entity instanceof AbstractClientPlayerEntity) {
            PlayerRenderer playerRenderer = (PlayerRenderer) entityRendererIn;
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
            for (Ability ability : AbilityHelper.getAbilities(player)) {
                ability.render(playerRenderer, matrixStack, buffer, packedLight, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                for (int slot = 0; slot < cap.getInventory().getContainerSize(); ++slot) {
                    ItemStack stack = cap.getInventory().getItem(slot);
                    if (stack != null && stack.getItem() instanceof IAccessory && !MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Accessories(playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch))) {
                        IAccessory accessoire = ((IAccessory) stack.getItem());
                        ModelSuit suitModel = new ModelSuit(accessoire.getScale(stack), HUPlayerUtil.haveSmallArms(player));
                        boolean shouldRender = true;
                        for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
                            SuitItem item = Suit.getSuitItem(equipmentSlot,player);
                            if (item != null && item.getSuit().getSlotForHide(equipmentSlot).contains(EquipmentAccessoriesSlot.getFromSlotIndex(slot))) {
                                shouldRender = false;
                            }
                        }
                        if(shouldRender){
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

        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                renderSuit(matrixStack, buffer, entity, slot, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Post(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
    }

    private void renderAccessories(ModelSuit suitModel, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, PlayerEntity player, PlayerRenderer playerRenderer, IAccessory accessoire, ItemStack stack, IHUPlayer cap, EquipmentAccessoriesSlot slot) {
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

    private void renderSuit(MatrixStack stack, IRenderTypeBuffer buffer, T entity, EquipmentSlotType slot, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof SuitItem) {
            SuitItem suitItem = (SuitItem) itemstack.getItem();
            if (suitItem.getSlot() == slot) {
                suitItem.getSuit().renderLayer(entityRendererIn, entity, itemstack, slot, stack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }
}

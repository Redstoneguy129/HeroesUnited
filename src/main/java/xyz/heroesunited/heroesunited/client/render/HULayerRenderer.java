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
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.client.render.model.ModelSuit;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoireSlot;
import xyz.heroesunited.heroesunited.common.objects.items.IAccessoire;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class HULayerRenderer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    public LivingRenderer<T, M> entityRendererIn;

    public HULayerRenderer(LivingRenderer entityRendererIn) {
        super(entityRendererIn);
        this.entityRendererIn = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Pre(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch)) && entity.isChild() == true)
            return;

        if (Suit.getSuit(entity) != null) {
            Suit.getSuit(entity).renderLayer(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
        MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));

        if (entityRendererIn instanceof PlayerRenderer && entity instanceof AbstractClientPlayerEntity) {
            PlayerRenderer playerRenderer = (PlayerRenderer) entityRendererIn;
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
            for (AbilityType type : AbilityHelper.getAbilities(player)) {
                type.create().render(playerRenderer, matrixStack, buffer, packedLight, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                for (int slot = 0; slot < 8; ++slot) {
                    ItemStack stack = cap.getInventory().getStackInSlot(slot);
                    if (stack != null && stack.getItem() instanceof IAccessoire && !MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Accesoires(playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch))) {
                        IAccessoire accessoire = ((IAccessoire) stack.getItem());
                        ModelSuit suitModel = new ModelSuit(accessoire.getScale(stack), HUPlayerUtil.haveSmallArms(player));
                        if (accessoire.renderDefaultModel()) {
                            renderAccessoire(suitModel, matrixStack, buffer, packedLight, player, playerRenderer, accessoire, stack, cap, EquipmentAccessoireSlot.getFromSlotIndex(slot));
                        } else {
                            accessoire.render(playerRenderer, matrixStack, buffer, packedLight, player, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, slot);
                        }
                    }
                }
            });
            MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Player(playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
        }
    }

    private void renderAccessoire(ModelSuit suitModel, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, PlayerEntity player, PlayerRenderer playerRenderer, IAccessoire accessoire, ItemStack stack, IHUPlayer cap, EquipmentAccessoireSlot slot) {
        suitModel.setVisible(false);
        if (slot == EquipmentAccessoireSlot.HELMET) {
            suitModel.bipedHeadwear.showModel = suitModel.bipedHead.showModel = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoireSlot.JACKET) {
            suitModel.bipedHeadwear.showModel = suitModel.bipedHead.showModel =
                    suitModel.bipedBody.showModel = suitModel.bipedBodyWear.showModel =
                            suitModel.bipedLeftArmwear.showModel = suitModel.bipedLeftArm.showModel =
                                    suitModel.bipedRightArmwear.showModel = suitModel.bipedRightArm.showModel = cap.getInventory().haveStack(slot);
        } else if (slot == EquipmentAccessoireSlot.TSHIRT || slot == EquipmentAccessoireSlot.RIGHT_WRIST ||
                slot == EquipmentAccessoireSlot.LEFT_WRIST || slot == EquipmentAccessoireSlot.GLOVES) {
            suitModel.bipedBody.showModel = suitModel.bipedBodyWear.showModel =
                    suitModel.bipedLeftArmwear.showModel = suitModel.bipedLeftArm.showModel =
                            suitModel.bipedRightArmwear.showModel = suitModel.bipedRightArm.showModel = cap.getInventory().haveStack(slot);
        }

        suitModel.bipedLeftLegwear.showModel = suitModel.bipedRightLegwear.showModel = suitModel.bipedLeftLeg.showModel =
                suitModel.bipedRightLeg.showModel = slot == EquipmentAccessoireSlot.PANTS && cap.getInventory().haveStack(slot)
                        || slot == EquipmentAccessoireSlot.SHOES && cap.getInventory().haveStack(EquipmentAccessoireSlot.SHOES);

        playerRenderer.getEntityModel().setModelAttributes(suitModel);

        suitModel.render(matrixStack, buffer.getBuffer(RenderType.getEntityTranslucent(accessoire.getTexture(stack, player, slot))), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}

package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class TheOneRingAccessory extends DefaultAccessoryItem {
    public TheOneRingAccessory() {
        super(new Item.Properties(), EquipmentAccessoriesSlot.WRIST, "BlazeFire");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        HandSide side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? HandSide.LEFT : HandSide.RIGHT;
        ItemCameraTransforms.TransformType transformType = side == HandSide.LEFT ? ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;

        matrix.pushPose();
        renderer.getModel().translateToHand(side, matrix);
        matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrix.translate((side == HandSide.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(player, stack, transformType, side == HandSide.LEFT, matrix, bufferIn, packedLightIn);
        matrix.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, PlayerEntity entity, EquipmentAccessoriesSlot slot) {
        return null;
    }
}

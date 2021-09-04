package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class TheOneRingAccessory extends DefaultAccessoryItem {
    public TheOneRingAccessory() {
        super(new Item.Properties(), EquipmentAccessoriesSlot.WRIST, "BlazeFire");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerRenderer renderer, PoseStack matrix, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        HumanoidArm side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        ItemTransforms.TransformType transformType = side == HumanoidArm.LEFT ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;

        matrix.pushPose();
        renderer.getModel().translateToHand(side, matrix);
        matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrix.translate((side == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(player, stack, transformType, side == HumanoidArm.LEFT, matrix, bufferIn, packedLightIn);
        matrix.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, Player entity, EquipmentAccessoriesSlot slot) {
        return null;
    }
}

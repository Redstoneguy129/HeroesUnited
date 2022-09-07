package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class TheOneRingAccessory extends DefaultAccessoryItem {
    public TheOneRingAccessory() {
        super(new Item.Properties(), EquipmentAccessoriesSlot.WRIST);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(EntityRendererProvider.Context context, LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        HumanoidArm side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        ItemTransforms.TransformType transformType = side == HumanoidArm.LEFT ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;

        poseStack.pushPose();
        renderer.getModel().translateToHand(side, poseStack);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        poseStack.translate((side == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, stack, transformType, side == HumanoidArm.LEFT, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public ResourceLocation getTexture(ItemStack stack, LivingEntity livingEntity, EquipmentAccessoriesSlot slot) {
        return null;
    }
}

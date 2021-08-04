package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class TheOneRingAccessory extends DefaultAccessoryItem {
    public TheOneRingAccessory() {
        super(new Item.Settings(), EquipmentAccessoriesSlot.WRIST, "BlazeFire");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        Arm side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? Arm.LEFT : Arm.RIGHT;
        ModelTransformation.Mode transformType = side == Arm.LEFT ? ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND : ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;

        matrix.push();
        renderer.getModel().setArmAngle(side, matrix);
        matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
        matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrix.translate((side == Arm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(player, stack, transformType, side == Arm.LEFT, matrix, bufferIn, packedLightIn);
        matrix.pop();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean renderDefaultModel() {
        return false;
    }

    @Override
    public Identifier getTexture(ItemStack stack, PlayerEntity entity, EquipmentAccessoriesSlot slot) {
        return null;
    }
}

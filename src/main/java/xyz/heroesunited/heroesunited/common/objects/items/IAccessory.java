package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.List;

public interface IAccessory {

    @OnlyIn(Dist.CLIENT)
    default boolean renderDefaultModel() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    default void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
    }

    @OnlyIn(Dist.CLIENT)
    default void renderFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, Arm side, ItemStack stack, int slot) {
    }

    @OnlyIn(Dist.CLIENT)
    Identifier getTexture(ItemStack stack, PlayerEntity entity, EquipmentAccessoriesSlot slot);

    EquipmentAccessoriesSlot getSlot();

    default boolean canTakeStack(PlayerEntity player, ItemStack stack) {
        return stack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(stack);
    }

    default boolean dropAfterDeath(PlayerEntity player, ItemStack stack) {
        return true;
    }

    default List<PlayerPart> getHiddenParts() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    default float getScale(ItemStack stack) {
        return 0.08F;
    }

    static EquipmentAccessoriesSlot getEquipmentSlotForItem(ItemStack stack) {
        if (stack.getItem() instanceof IAccessory) {
            return ((IAccessory) stack.getItem()).getSlot();
        }
        return null;
    }
}

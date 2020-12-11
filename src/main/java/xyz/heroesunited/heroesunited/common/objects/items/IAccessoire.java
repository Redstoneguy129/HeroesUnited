package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoireSlot;

@OnlyIn(Dist.CLIENT)
public interface IAccessoire {

    default boolean renderDefaultModel() {
        return true;
    }

    default void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {}

    ResourceLocation getTexture(ItemStack stack, PlayerEntity entity, EquipmentAccessoireSlot slot);

    EquipmentAccessoireSlot getSlot();

    default boolean canTakeStack(PlayerEntity player, ItemStack stack) {
        return stack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(stack);
    }

    default boolean dropAfterDeath(PlayerEntity player, ItemStack stack) {
        return true;
    }

    default float getScale(ItemStack stack){
        return 0.08F;
    }
}

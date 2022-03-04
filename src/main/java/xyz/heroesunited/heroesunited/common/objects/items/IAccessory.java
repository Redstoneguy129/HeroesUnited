package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
    default void render(EntityRendererProvider.Context context, LivingEntityRenderer<? extends LivingEntity, ? extends HumanoidModel<?>> renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity livingEntity, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
    }

    @OnlyIn(Dist.CLIENT)
    default void renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side, ItemStack stack, int slot) {
    }

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getTexture(ItemStack stack, LivingEntity livingEntity, EquipmentAccessoriesSlot slot);

    EquipmentAccessoriesSlot getSlot();

    default boolean canTakeStack(Player player, ItemStack stack) {
        return stack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(stack);
    }

    default boolean dropAfterDeath(Player player, ItemStack stack) {
        return true;
    }

    default List<PlayerPart> getHiddenParts(boolean firstPerson) {
        return null;
    }

    default float getPlayerWearSize(ItemStack stack) {
        return 1.0F;
    }

    default float getScale(ItemStack stack) {
        return 0.0F;
    }

    static EquipmentAccessoriesSlot getEquipmentSlotForItem(ItemStack stack) {
        if (stack.getItem() instanceof IAccessory) {
            return ((IAccessory) stack.getItem()).getSlot();
        }
        return null;
    }
}

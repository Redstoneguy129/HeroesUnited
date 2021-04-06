package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.client.render.renderer.BoboAccessoryRenderer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.List;

public class BoboAccessory extends Item implements IAccessory, IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public BoboAccessory() {
        super(new Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT).setISTER(() -> BoboAccessoryRenderer::new));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("Made For Chappie").withStyle(TextFormatting.ITALIC));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        matrix.pushPose();
        renderer.getModel().head.translateAndRotate(matrix);
        matrix.translate(0.0D, -0.25D, 0.0D);
        matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrix.scale(0.625F, -0.625F, -0.625F);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.HEAD, packedLightIn, OverlayTexture.NO_OVERLAY, matrix, bufferIn);
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

    @Override
    public EquipmentAccessoriesSlot getSlot() {
        return EquipmentAccessoriesSlot.HELMET;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 20.0F, this::predicate));
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(itemStack, world, entity, itemSlot, isSelected);
        if (entity instanceof PlayerEntity) {
            entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                ItemStack stack = cap.getInventory().getItem(EquipmentAccessoriesSlot.HELMET.getSlot());
                AnimationController controller = GeckoLibUtil.getControllerForStack(this.factory, stack, "controller");
                if (controller.getAnimationState() == AnimationState.Stopped) {
                    controller.markNeedsReload();
                    controller.setAnimation((new AnimationBuilder()).addAnimation("animation.bobo", true));
                }
            });
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

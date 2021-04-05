package xyz.heroesunited.heroesunited.common.objects.items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.render.renderer.ArcReactorRenderer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class ArcReactorAccessory extends Item implements IAccessory, IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public ArcReactorAccessory() {
        super(new Properties().stacksTo(1).tab(ItemGroup.TAB_COMBAT).setISTER(() -> ArcReactorRenderer::new));
    }

    @Override
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        matrix.pushPose();
        renderer.getModel().body.translateAndRotate(matrix);
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
        return EquipmentAccessoriesSlot.TSHIRT;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

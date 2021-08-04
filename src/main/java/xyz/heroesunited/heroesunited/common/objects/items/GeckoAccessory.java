package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import xyz.heroesunited.heroesunited.client.render.renderer.GeckoAccessoryRenderer;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class GeckoAccessory extends DefaultAccessoryItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public GeckoAccessory(EquipmentAccessoriesSlot accessorySlot) {
        super(new Settings().setISTER(() -> GeckoAccessoryRenderer::new), accessorySlot);
    }

    public GeckoAccessory(EquipmentAccessoriesSlot accessorySlot, String name) {
        super(new Settings().setISTER(() -> GeckoAccessoryRenderer::new), accessorySlot, name);
    }

    public Identifier getTextureFile() {
        return new Identifier(this.getRegistryName().getNamespace(), String.format("textures/accessories/%s.png", this.getRegistryName().getPath()));
    }

    public Identifier getModelFile() {
        return new Identifier(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath()));
    }

    public Identifier getAnimationFile() {
        return new Identifier(this.getRegistryName().getNamespace(), String.format("animations/%s.animation.json", this.getRegistryName().getPath()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, ItemStack stack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, int slot) {
        if (EquipmentAccessoriesSlot.getWristAccessories().contains(accessorySlot)) {
            Arm side = slot == EquipmentAccessoriesSlot.LEFT_WRIST.getSlot() ? Arm.LEFT : Arm.RIGHT;
            ModelTransformation.Mode transformType = side == Arm.LEFT ? ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND : ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
            if (stack.getItem() == HUItems.FINN_ARM) {
                transformType = ModelTransformation.Mode.HEAD;
            }

            matrix.push();
            renderer.getModel().setArmAngle(side, matrix);
            matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            matrix.translate((side == Arm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(player, stack, transformType, side == Arm.LEFT, matrix, bufferIn, packedLightIn);
            matrix.pop();
        }
        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.GLOVES)) {
            for (Arm side : Arm.values()) {
                ModelTransformation.Mode transformType = side == Arm.LEFT ? ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND : ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
                matrix.push();
                renderer.getModel().setArmAngle(side, matrix);
                if (this.name.equals("Gillygogs")) {
                    matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                    matrix.scale(0.625F, -0.625F, -0.625F);
                    matrix.translate(side == Arm.LEFT ? -0.6 : -0.4, -0.35D, -0.625D);
                    Identifier modelFile = new Identifier(this.getRegistryName().getNamespace(), String.format("geo/%s.geo.json", this.getRegistryName().getPath() + (side == Arm.LEFT ? "" : "_v2")));
                    new GeckoAccessoryRenderer(modelFile).render(this, matrix, bufferIn, packedLightIn, this.getDefaultStack());
                } else {
                    matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                    matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                    matrix.translate((side == Arm.LEFT ? -1 : 1) / 16.0F, 0.125D, -0.625D);
                    MinecraftClient.getInstance().getHeldItemRenderer().renderItem(player, stack, transformType, side == Arm.LEFT, matrix, bufferIn, packedLightIn);
                }
                matrix.pop();
            }
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.TSHIRT) || this.accessorySlot.equals(EquipmentAccessoriesSlot.JACKET) || this.accessorySlot.equals(EquipmentAccessoriesSlot.BELT)) {
            matrix.push();
            renderer.getModel().body.rotate(matrix);
            matrix.translate(0.0D, -0.25D, 0.0D);
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            matrix.scale(0.625F, -0.625F, -0.625F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.HEAD, packedLightIn, OverlayTexture.DEFAULT_UV, matrix, bufferIn);
            matrix.pop();
        }

        if (this.accessorySlot.equals(EquipmentAccessoriesSlot.HELMET)) {
            matrix.push();
            renderer.getModel().head.rotate(matrix);
            matrix.translate(0.0D, -0.25D, 0.0D);
            matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            matrix.scale(0.625F, -0.625F, -0.625F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.HEAD, packedLightIn, OverlayTexture.DEFAULT_UV, matrix, bufferIn);
            matrix.pop();
        }
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

    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

package xyz.heroesunited.heroesunited.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.HandSide;

public class ModelSuit<T extends LivingEntity> extends BipedModel<T> {

    public final ModelRenderer bipedLeftArmwear;
    public final ModelRenderer bipedRightArmwear;
    public final ModelRenderer bipedLeftLegwear;
    public final ModelRenderer bipedRightLegwear;
    public final ModelRenderer bipedBodyWear;

    public ModelSuit(float scale, boolean smallArms) {
        this(scale, smallArms, 64, 64);
    }

    public ModelSuit(float scale, boolean smallArms, int textureWidth, int textureHeight) {
        super(0F, 0.0F, textureWidth, textureHeight);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);

        if (smallArms) {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);

            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(0F, 0F, 0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.bipedRightArmwear.setRotationPoint(0F, 0F, 0F);
        } else {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.bipedLeftArmwear.setRotationPoint(0F, 0F, 0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.bipedRightArmwear.setRotationPoint(0F, 0F, 0F);
        }

        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.bipedLeftLegwear.setRotationPoint(0F, 0F, 0F);
        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.bipedRightLegwear.setRotationPoint(0F, 0F, 0F);
        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
        this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.bipedBody.addChild(bipedBodyWear);
        this.bipedRightArm.addChild(bipedRightArmwear);
        this.bipedLeftArm.addChild(bipedLeftArmwear);
        this.bipedRightLeg.addChild(bipedRightLegwear);
        this.bipedLeftLeg.addChild(bipedLeftLegwear);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn instanceof ArmorStandEntity) {
            ArmorStandEntity armorStand = (ArmorStandEntity) entityIn;
            this.bipedHead.rotateAngleX = 0.017453292F * armorStand.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = 0.017453292F * armorStand.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = 0.017453292F * armorStand.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            this.bipedBody.rotateAngleX = 0.019F * armorStand.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = 0.019F * armorStand.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = 0.019F * armorStand.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = 0.019F * armorStand.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = 0.019F * armorStand.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = 0.019F * armorStand.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = 0.019F * armorStand.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = 0.019F * armorStand.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = 0.019F * armorStand.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = 0.019F * armorStand.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = 0.019F * armorStand.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = 0.019F * armorStand.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            this.bipedRightLeg.rotateAngleX = 0.019F * armorStand.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = 0.019F * armorStand.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = 0.019F * armorStand.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            this.bipedHeadwear.copyModelAngles(this.bipedHead);
        }
    }

    public void renderArm(HandSide handSide, MatrixStack matrixStack, IVertexBuilder vertexBuilder, int combinedLight, T entity) {
        this.swingProgress = 0.0F;
        this.isSneak = false;
        this.swimAnimation = 0.0F;
        this.setRotationAngles(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        if (handSide == HandSide.RIGHT) {
            this.bipedRightArm.rotateAngleX = 0.0F;
            this.bipedRightArmwear.rotateAngleX = 0.0F;
            this.bipedRightArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        } else {
            this.bipedLeftArm.rotateAngleX = 0.0F;
            this.bipedLeftArmwear.rotateAngleX = 0.0F;
            this.bipedLeftArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
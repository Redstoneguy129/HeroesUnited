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
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.hat = new ModelRenderer(this, 32, 0);
        this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale + 0.5F);
        this.hat.setPos(0.0F, 0.0F, 0.0F);
        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.rightLeg = new ModelRenderer(this, 0, 16);
        this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.rightLeg.setPos(-1.9F, 12.0F, 0.0F);

        if (smallArms) {
            this.leftArm = new ModelRenderer(this, 32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.leftArm.setPos(5.0F, 2.5F, 0.0F);

            this.rightArm = new ModelRenderer(this, 40, 16);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.rightArm.setPos(-5.0F, 2.5F, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.bipedLeftArmwear.setPos(0F, 0F, 0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.bipedRightArmwear.setPos(0F, 0F, 0F);
        } else {
            this.leftArm = new ModelRenderer(this, 32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.leftArm.setPos(5.0F, 2.0F, 0.0F);

            this.rightArm = new ModelRenderer(this, 40, 16);
            this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.rightArm.setPos(-5.0F, 2.0F, 0.0F);

            this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.bipedLeftArmwear.setPos(0F, 0F, 0F);

            this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.bipedRightArmwear.setPos(0F, 0F, 0F);
        }

        this.leftLeg = new ModelRenderer(this, 16, 48);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.bipedLeftLegwear.setPos(0F, 0F, 0F);
        this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.bipedRightLegwear.setPos(0F, 0F, 0F);
        this.bipedBodyWear = new ModelRenderer(this, 16, 32);
        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
        this.bipedBodyWear.setPos(0.0F, 0.0F, 0.0F);

        this.body.addChild(bipedBodyWear);
        this.rightArm.addChild(bipedRightArmwear);
        this.leftArm.addChild(bipedLeftArmwear);
        this.rightLeg.addChild(bipedRightLegwear);
        this.leftLeg.addChild(bipedLeftLegwear);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn instanceof ArmorStandEntity) {
            ArmorStandEntity armorStand = (ArmorStandEntity) entityIn;
            this.head.xRot = 0.017453292F * armorStand.getHeadPose().getX();
            this.head.yRot = 0.017453292F * armorStand.getHeadPose().getY();
            this.head.zRot = 0.017453292F * armorStand.getHeadPose().getZ();
            this.head.setPos(0.0F, 1.0F, 0.0F);
            this.body.xRot = 0.019F * armorStand.getBodyPose().getX();
            this.body.yRot = 0.019F * armorStand.getBodyPose().getY();
            this.body.zRot = 0.019F * armorStand.getBodyPose().getZ();
            this.leftArm.xRot = 0.019F * armorStand.getLeftArmPose().getX();
            this.leftArm.yRot = 0.019F * armorStand.getLeftArmPose().getY();
            this.leftArm.zRot = 0.019F * armorStand.getLeftArmPose().getZ();
            this.rightArm.xRot = 0.019F * armorStand.getRightArmPose().getX();
            this.rightArm.yRot = 0.019F * armorStand.getRightArmPose().getY();
            this.rightArm.zRot = 0.019F * armorStand.getRightArmPose().getZ();
            this.leftLeg.xRot = 0.019F * armorStand.getLeftLegPose().getX();
            this.leftLeg.yRot = 0.019F * armorStand.getLeftLegPose().getY();
            this.leftLeg.zRot = 0.019F * armorStand.getLeftLegPose().getZ();
            this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
            this.rightLeg.xRot = 0.019F * armorStand.getRightLegPose().getX();
            this.rightLeg.yRot = 0.019F * armorStand.getRightLegPose().getY();
            this.rightLeg.yRot = 0.019F * armorStand.getRightLegPose().getZ();
            this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
            this.hat.copyFrom(this.head);
        }
    }

    public void renderArm(HandSide handSide, MatrixStack matrixStack, IVertexBuilder vertexBuilder, int combinedLight, T entity) {
        this.attackTime = 0.0F;
        this.crouching = false;
        this.swimAmount = 0.0F;
        this.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        if (handSide == HandSide.RIGHT) {
            this.rightArm.xRot = 0.0F;
            this.bipedRightArmwear.xRot = 0.0F;
            this.rightArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        } else {
            this.leftArm.xRot = 0.0F;
            this.bipedLeftArmwear.xRot = 0.0F;
            this.leftArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
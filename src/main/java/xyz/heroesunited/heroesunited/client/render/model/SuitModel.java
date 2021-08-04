package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import xyz.heroesunited.heroesunited.HUClientListener;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class SuitModel<T extends LivingEntity> extends BipedEntityModel<T> {

    public final ModelPart leftSleeve = this.leftArm.getChild("left_sleeve");
    public final ModelPart rightSleeve = this.rightArm.getChild("right_sleeve");
    public final ModelPart leftPants = this.leftLeg.getChild("left_pants");
    public final ModelPart rightPants = this.rightLeg.getChild("right_pants");
    public final ModelPart jacket = this.body.getChild("jacket");

    public SuitModel(Entity entity) {
        this(HUPlayerUtil.haveSmallArms(entity));
    }

    public SuitModel(boolean slim) {
        super(HUClientUtil.getSuitModelPart(slim), RenderLayer::getEntityTranslucent);
    }

    public static ModelData createMesh(Dilation size, boolean slim) {
        ModelData mesh = BipedEntityModel.getModelData(size, 0.0F);
        ModelPartData parts = mesh.getRoot();
        if (slim) {
            ModelPartData leftArm = parts.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size), ModelTransform.pivot(5.0F, 2.5F, 0.0F));
            ModelPartData rightArm = parts.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size), ModelTransform.pivot(-5.0F, 2.5F, 0.0F));
            leftArm.addChild("left_sleeve", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.pivot(5.0F, 2.5F, 0.0F));
            rightArm.addChild("right_sleeve", ModelPartBuilder.create().uv(40, 32).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.pivot(-5.0F, 2.5F, 0.0F));
        } else {
            ModelPartData leftArm = parts.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, size), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
            leftArm.addChild("left_sleeve", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
            parts.getChild("right_arm").addChild("right_sleeve", ModelPartBuilder.create().uv(40, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
        }

        ModelPartData leftLeg = parts.addChild("left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, size), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        leftLeg.addChild("left_pants", ModelPartBuilder.create().uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
        parts.getChild("right_leg").addChild("right_pants", ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));
        parts.getChild("body").addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, size.add(0.25F)), ModelTransform.NONE);
        return mesh;
    }


/*
    public SuitModel(float scale, boolean slim) {
        super(RenderType::entityTranslucent, scale, 0.0F, 64, 64);
        if (slim) {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale);
            this.leftArm.setPos(5.0F, 2.5F, 0.0F);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale);
            this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale + 0.25F);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale + 0.25F);
        } else {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
            this.leftArm.setPos(5.0F, 2.0F, 0.0F);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
        }

        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        this.leftPants = new ModelPart(this, 0, 48);
        this.leftPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
        this.rightPants = new ModelPart(this, 0, 32);
        this.rightPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
        this.jacket = new ModelPart(this, 16, 32);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale + 0.25F);
        this.body.addChild(jacket);
        this.rightArm.addChild(rightSleeve);
        this.leftArm.addChild(leftSleeve);
        this.leftLeg.addChild(leftPants);
        this.rightLeg.addChild(rightPants);
    }*/

    public void setAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn instanceof ArmorStandEntity) {
            ArmorStandEntity armorStand = (ArmorStandEntity)entityIn;
            this.head.pitch = 0.017453292F * armorStand.getHeadRotation().getPitch();
            this.head.yaw = 0.017453292F * armorStand.getHeadRotation().getYaw();
            this.head.roll = 0.017453292F * armorStand.getHeadRotation().getRoll();
            this.head.setPivot(0.0F, 1.0F, 0.0F);
            this.body.pitch = 0.019F * armorStand.getBodyRotation().getPitch();
            this.body.yaw = 0.019F * armorStand.getBodyRotation().getYaw();
            this.body.roll = 0.019F * armorStand.getBodyRotation().getRoll();
            this.leftArm.pitch = 0.019F * armorStand.getLeftArmRotation().getPitch();
            this.leftArm.yaw = 0.019F * armorStand.getLeftArmRotation().getYaw();
            this.leftArm.roll = 0.019F * armorStand.getLeftArmRotation().getRoll();
            this.rightArm.pitch = 0.019F * armorStand.getRightArmRotation().getPitch();
            this.rightArm.yaw = 0.019F * armorStand.getRightArmRotation().getYaw();
            this.rightArm.roll = 0.019F * armorStand.getRightArmRotation().getRoll();
            this.leftLeg.pitch = 0.019F * armorStand.getLeftLegRotation().getPitch();
            this.leftLeg.yaw = 0.019F * armorStand.getLeftLegRotation().getYaw();
            this.leftLeg.roll = 0.019F * armorStand.getLeftLegRotation().getRoll();
            this.leftLeg.setPivot(1.9F, 11.0F, 0.0F);
            this.rightLeg.pitch = 0.019F * armorStand.getRightLegRotation().getPitch();
            this.rightLeg.yaw = 0.019F * armorStand.getRightLegRotation().getYaw();
            this.rightLeg.yaw = 0.019F * armorStand.getRightLegRotation().getRoll();
            this.rightLeg.setPivot(-1.9F, 11.0F, 0.0F);
            this.hat.copyTransform(this.head);
        }

    }

    public void copyPropertiesTo(SuitModel<T> model) {
        super.setAttributes(model);
        model.leftPants.copyTransform(this.leftLeg);
        model.rightPants.copyTransform(this.rightLeg);
        model.leftSleeve.copyTransform(this.leftArm);
        model.rightSleeve.copyTransform(this.rightArm);
        model.jacket.copyTransform(this.body);
    }

    public void copyPropertiesFrom(BipedEntityModel<T> model) {
        this.handSwingProgress = model.handSwingProgress;
        this.riding = model.riding;
        this.child = model.child;
        this.leftArmPose = model.leftArmPose;
        this.rightArmPose = model.rightArmPose;
        this.sneaking = model.sneaking;
        this.head.copyTransform(model.head);
        this.hat.copyTransform(model.hat);
        this.body.copyTransform(model.body);
        this.rightArm.copyTransform(model.rightArm);
        this.leftArm.copyTransform(model.leftArm);
        this.rightLeg.copyTransform(model.rightLeg);
        this.leftLeg.copyTransform(model.leftLeg);
    }

    public void renderArm(Arm handSide, MatrixStack matrixStack, VertexConsumer vertexBuilder, int combinedLight, T entity) {
        this.handSwingProgress = 0.0F;
        this.sneaking = false;
        this.leaningPitch = 0.0F;
        this.setAngles(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        if (handSide == Arm.RIGHT) {
            this.rightArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.DEFAULT_UV);
        } else {
            this.leftArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.DEFAULT_UV);
        }

    }
}
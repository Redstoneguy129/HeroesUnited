package xyz.heroesunited.heroesunited.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import xyz.heroesunited.heroesunited.client.renderer.IHUModelPart;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

public class SuitModel<T extends LivingEntity> extends HumanoidModel<T> {

    public final ModelPart leftSleeve = this.leftArm.getChild("left_sleeve");
    public final ModelPart rightSleeve = this.rightArm.getChild("right_sleeve");
    public final ModelPart leftPants = this.leftLeg.getChild("left_pants");
    public final ModelPart rightPants = this.rightLeg.getChild("right_pants");
    public final ModelPart jacket = this.body.getChild("jacket");

    public SuitModel(Entity entity, float size) {
        this(HUClientUtil.getSuitModelPart(entity), size);
    }

    public SuitModel(ModelPart mainPart, float size) {
        super(mainPart, RenderType::entityTranslucent);
        mainPart.getAllParts().filter((p_170824_) -> !p_170824_.isEmpty()).forEach(part -> ((IHUModelPart) (Object) part).setSize(new CubeDeformation(size)));
    }

    public static MeshDefinition createMesh(CubeDeformation size, boolean slim) {
        MeshDefinition mesh = HumanoidModel.createMesh(size, 0.0F);
        PartDefinition parts = mesh.getRoot();
        if (slim) {
            parts.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size), PartPose.offset(5.0F, 2.5F, 0.0F));
            parts.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size), PartPose.offset(-5.0F, 2.5F, 0.0F));
            parts.getChild("left_arm").addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
            parts.getChild("right_arm").addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
        } else {
            parts.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, size), PartPose.offset(5.0F, 2.0F, 0.0F));
            parts.getChild("left_arm").addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
            parts.getChild("right_arm").addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
        }

        parts.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, size), PartPose.offset(1.9F, 12.0F, 0.0F));
        parts.getChild("left_leg").addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
        parts.getChild("right_leg").addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
        parts.getChild("body").addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, size.extend(0.25F)), PartPose.ZERO);
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

    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn instanceof ArmorStand armorStand) {
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

    public void copyPropertiesTo(SuitModel<T> model) {
        super.copyPropertiesTo(model);
        model.leftPants.copyFrom(this.leftLeg);
        model.rightPants.copyFrom(this.rightLeg);
        model.leftSleeve.copyFrom(this.leftArm);
        model.rightSleeve.copyFrom(this.rightArm);
        model.jacket.copyFrom(this.body);
    }

    public void copyPropertiesFrom(HumanoidModel<T> model) {
        this.attackTime = model.attackTime;
        this.riding = model.riding;
        this.young = model.young;
        this.leftArmPose = model.leftArmPose;
        this.rightArmPose = model.rightArmPose;
        this.crouching = model.crouching;
        this.head.copyFrom(model.head);
        this.hat.copyFrom(model.hat);
        this.body.copyFrom(model.body);
        this.rightArm.copyFrom(model.rightArm);
        this.leftArm.copyFrom(model.leftArm);
        this.rightLeg.copyFrom(model.rightLeg);
        this.leftLeg.copyFrom(model.leftLeg);
    }

    public void renderArm(EntityModelSet modelSet, HumanoidArm handSide, PoseStack matrixStack, VertexConsumer vertexBuilder, int combinedLight, HumanoidModel<T> model) {
        this.copyPropertiesFrom(model);
        if (handSide == HumanoidArm.RIGHT) {
            this.rightArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        } else {
            this.leftArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        }

    }
}
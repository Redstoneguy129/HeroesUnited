package xyz.heroesunited.heroesunited.client.render.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.HandSide;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class ModelSuit<T extends LivingEntity> extends BipedModel<T> {

    public final ModelRenderer leftSleeve;
    public final ModelRenderer rightSleeve;
    public final ModelRenderer leftPants;
    public final ModelRenderer rightPants;
    public final ModelRenderer jacket;

    public ModelSuit(Entity entity) {
        this(0, HUPlayerUtil.haveSmallArms(entity));
    }

    public ModelSuit(float scale, boolean slim) {
        super(RenderType::entityTranslucent, scale, 0.0F, 64, 64);
        if (slim) {
            this.leftSleeve = new ModelRenderer(this, 48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale + 0.25F);
            this.leftArm.addChild(leftSleeve);
            this.rightSleeve = new ModelRenderer(this, 40, 32);
            this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, scale + 0.25F);
            this.rightArm.addChild(rightSleeve);
        } else {
            this.leftSleeve = new ModelRenderer(this, 48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
            this.leftArm.addChild(leftSleeve);
            this.rightSleeve = new ModelRenderer(this, 40, 32);
            this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
            this.rightArm.addChild(rightSleeve);
        }

        this.leftLeg = new ModelRenderer(this, 16, 48);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        this.leftPants = new ModelRenderer(this, 0, 48);
        this.leftPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
        this.leftLeg.addChild(leftPants);
        this.rightPants = new ModelRenderer(this, 0, 32);
        this.rightPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale + 0.25F);
        this.rightLeg.addChild(rightPants);
        this.jacket = new ModelRenderer(this, 16, 32);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale + 0.25F);
        this.body.addChild(jacket);
    }

    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        if (entityIn instanceof ArmorStandEntity) {
            ArmorStandEntity armorStand = (ArmorStandEntity)entityIn;
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

    public void copyPropertiesTo(ModelSuit<T> model) {
        super.copyPropertiesTo(model);
        model.leftPants.copyFrom(this.leftLeg);
        model.rightPants.copyFrom(this.rightLeg);
        model.leftSleeve.copyFrom(this.leftArm);
        model.rightSleeve.copyFrom(this.rightArm);
        model.jacket.copyFrom(this.body);
    }

    public void copyPropertiesFrom(BipedModel<T> model) {
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
        this.leftPants.copyFrom(model.leftLeg);
        this.rightPants.copyFrom(model.rightLeg);
        this.leftSleeve.copyFrom(model.leftArm);
        this.rightSleeve.copyFrom(model.rightArm);
        this.jacket.copyFrom(model.body);
    }

    public void renderArm(HandSide handSide, MatrixStack matrixStack, IVertexBuilder vertexBuilder, int combinedLight, T entity) {
        if (handSide == HandSide.RIGHT) {
            this.rightArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        } else {
            this.leftArm.render(matrixStack, vertexBuilder, combinedLight, OverlayTexture.NO_OVERLAY);
        }

    }
}
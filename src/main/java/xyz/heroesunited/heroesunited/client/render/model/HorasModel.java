package xyz.heroesunited.heroesunited.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

public class HorasModel extends HumanoidModel<Horas> {

    public HorasModel(ModelPart root) {
        super(root, RenderType::entityTranslucent);
    }

    public static LayerDefinition createLayerDefinition() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition parts = mesh.getRoot();

        PartDefinition head = parts.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(55, 20)
                        .mirror(false)
                        .addBox(-5.0F, -34.25F, -7.75F, 10.0F, 7.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(52, 68)
                        .mirror(false)
                        .addBox(-6.5F, -32.75F, -8.25F, 5.0F, 4.0F, 4.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(94, 81)
                        .mirror(false)
                        .addBox(-6.766F, -30.3928F, -9.25F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(57, 90)
                        .mirror(false)
                        .addBox(-6.766F, -33.1072F, -9.25F, 1.0F, 3.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(30, 11)
                        .mirror(false)
                        .addBox(-2.234F, -33.1072F, -9.25F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(27, 27)
                        .mirror(false)
                        .addBox(1.234F, -33.1072F, -9.25F, 1.0F, 3.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 31)
                        .mirror(false)
                        .addBox(-2.234F, -30.3928F, -9.25F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 17)
                        .mirror(false)
                        .addBox(1.234F, -30.3928F, -9.25F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(92, 49)
                        .mirror(false)
                        .addBox(5.766F, -30.3928F, -9.25F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(72, 62)
                        .mirror(false)
                        .addBox(5.766F, -33.1072F, -9.25F, 1.0F, 3.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(87, 30)
                        .mirror(false)
                        .addBox(-6.0F, -33.75F, -9.25F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(86, 9)
                        .mirror(false)
                        .addBox(-6.0F, -28.75F, -9.25F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(87, 24)
                        .mirror(false)
                        .addBox(2.0F, -33.75F, -9.25F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(18, 47)
                        .mirror(false)
                        .addBox(2.0F, -28.75F, -9.25F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(10, 68)
                        .mirror(false)
                        .addBox(1.5F, -32.75F, -8.25F, 5.0F, 4.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 39)
                        .mirror(false)
                        .addBox(-5.0F, -27.25F, -6.75F, 10.0F, 1.0F, 7.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(34, 0)
                        .mirror(false)
                        .addBox(-5.0F, -34.75F, -6.75F, 10.0F, 1.0F, 7.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(52, 52)
                        .mirror(false)
                        .addBox(-5.0F, -34.25F, 6.6423F, 10.0F, 7.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(30, 11)
                        .mirror(false)
                        .addBox(-5.0F, -34.75F, -0.0897F, 10.0F, 1.0F, 8.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 0)
                        .mirror(false)
                        .addBox(-6.0F, -35.0F, -4.5897F, 12.0F, 1.0F, 10.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(55, 29)
                        .mirror(false)
                        .addBox(-3.0F, -36.0F, -1.5897F, 6.0F, 1.0F, 4.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(6, 0)
                        .mirror(false)
                        .addBox(-0.5F, -44.0F, -0.0897F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(120, 0)
                        .mirror(false)
                        .addBox(-1.5F, -41.75F, -0.0897F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE.extend(-0.25F))
                        .texOffs(116, 0)
                        .mirror(false)
                        .addBox(-2.5F, -40.0F, -0.0897F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE.extend(-0.25F))
                        .texOffs(40, 20)
                        .mirror(false)
                        .addBox(2.5F, -45.5F, -0.0897F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(47, 20)
                        .mirror(false)
                        .addBox(-4.0F, -36.5F, -0.3218F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 42)
                        .mirror(false)
                        .addBox(-4.0F, -36.5F, -0.8577F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(27, 38)
                        .mirror(false)
                        .addBox(3.0F, -36.5F, -0.8577F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 47)
                        .mirror(false)
                        .addBox(3.0F, -36.5F, -0.3218F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 81)
                        .mirror(false)
                        .addBox(-3.0F, -36.5F, -2.5897F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(44, 8)
                        .mirror(false)
                        .addBox(-3.0F, -36.5F, 2.4103F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(27, 26)
                        .mirror(false)
                        .addBox(-5.0F, -27.25F, -0.3577F, 10.0F, 1.0F, 8.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 68)
                        .mirror(false)
                        .addBox(6.0F, -34.25F, -2.5538F, 2.0F, 7.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(16, 67)
                        .mirror(false)
                        .addBox(-8.0F, -34.25F, -2.5538F, 2.0F, 7.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(64, 70)
                        .mirror(false)
                        .addBox(-8.5F, -33.25F, -2.5538F, 1.0F, 5.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(69, 29)
                        .mirror(false)
                        .addBox(7.5F, -33.25F, -2.5538F, 1.0F, 5.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(9, 89)
                        .mirror(false)
                        .addBox(8.0F, -32.75F, -2.0538F, 1.0F, 4.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(69, 86)
                        .mirror(false)
                        .addBox(-9.0F, -32.75F, -2.0538F, 1.0F, 4.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(90, 56)
                        .mirror(false)
                        .addBox(-7.5F, -27.25F, 0.3122F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(90, 14)
                        .mirror(false)
                        .addBox(-7.5F, -34.75F, 0.5801F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(90, 45)
                        .mirror(false)
                        .addBox(-7.5F, -27.25F, -2.4199F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(90, 41)
                        .mirror(false)
                        .addBox(-7.5F, -34.75F, -2.4199F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(84, 77)
                        .mirror(false)
                        .addBox(1.5F, -27.25F, 0.3122F, 6.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(58, 15)
                        .mirror(false)
                        .addBox(1.5F, -34.75F, 0.5801F, 6.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(80, 37)
                        .mirror(false)
                        .addBox(1.5F, -27.25F, -2.4199F, 6.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(79, 20)
                        .mirror(false)
                        .addBox(1.5F, -34.75F, -2.4199F, 6.0F, 1.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("bone20", CubeListBuilder.create()
                        .texOffs(52, 38)
                        .mirror(false)
                        .addBox(-23.2262F, -24.5684F, -9.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(46, 46)
                        .mirror(false)
                        .addBox(-17.0978F, -29.7107F, -9.25F, 1.0F, 1.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(50, 61)
                        .mirror(false)
                        .addBox(-23.4336F, -18.401F, -9.25F, 1.0F, 1.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(34, 4)
                        .mirror(false)
                        .addBox(-17.3053F, -23.5433F, -9.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        head.addOrReplaceChild("bone19", CubeListBuilder.create()
                        .texOffs(26, 67)
                        .mirror(false)
                        .addBox(16.0978F, -29.7107F, -9.25F, 1.0F, 1.0F, 5.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(30, 16)
                        .mirror(false)
                        .addBox(22.2262F, -24.5684F, -9.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(52, 35)
                        .mirror(false)
                        .addBox(16.3053F, -23.5433F, -9.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(40, 20)
                        .mirror(false)
                        .addBox(22.4336F, -18.401F, -9.25F, 1.0F, 1.0F, 5.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        head.addOrReplaceChild("bone18", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror(false)
                        .addBox(39.3997F, -21.8657F, -0.5897F, 1.0F, 8.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 14)
                        .mirror(false)
                        .addBox(38.3997F, -22.8657F, -0.5897F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 11)
                        .mirror(false)
                        .addBox(38.3997F, -25.8657F, -0.5897F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 38)
                        .mirror(false)
                        .addBox(40.3997F, -24.8657F, -0.5897F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(34, 0)
                        .mirror(false)
                        .addBox(38.3997F, -24.8657F, -0.5897F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        head.addOrReplaceChild("bone17", CubeListBuilder.create()
                        .texOffs(76, 50)
                        .mirror(false)
                        .addBox(-4.2117F, -34.25F, -8.2051F, 6.0F, 7.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(61, 0)
                        .mirror(false)
                        .addBox(-3.3457F, -27.25F, -7.7051F, 5.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(18, 60)
                        .mirror(false)
                        .addBox(-3.3457F, -34.75F, -7.7051F, 5.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(74, 41)
                        .mirror(false)
                        .addBox(-1.0155F, -34.25F, 6.6513F, 6.0F, 7.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(53, 43)
                        .mirror(false)
                        .addBox(-0.8816F, -27.25F, 1.1513F, 5.0F, 1.0F, 7.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(52, 35)
                        .mirror(false)
                        .addBox(-0.6495F, -34.75F, 1.2853F, 5.0F, 1.0F, 7.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(34, 60)
                        .mirror(false)
                        .addBox(-0.5466F, -36.5F, 3.3032F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(58, 8)
                        .mirror(false)
                        .addBox(-0.7428F, -36.5F, -3.8929F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        head.addOrReplaceChild("bone16", CubeListBuilder.create()
                        .texOffs(48, 76)
                        .mirror(false)
                        .addBox(-1.7883F, -34.25F, -8.2051F, 6.0F, 7.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(34, 61)
                        .mirror(false)
                        .addBox(-1.6543F, -27.25F, -7.7051F, 5.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 61)
                        .mirror(false)
                        .addBox(-1.6543F, -34.75F, -7.7051F, 5.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(32, 75)
                        .mirror(false)
                        .addBox(-4.9845F, -34.25F, 6.6513F, 6.0F, 7.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(56, 61)
                        .mirror(false)
                        .addBox(-4.1184F, -27.25F, 2.1513F, 5.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(58, 8)
                        .mirror(false)
                        .addBox(-4.3505F, -34.75F, 2.2853F, 5.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(58, 11)
                        .mirror(false)
                        .addBox(-1.4534F, -36.5F, 3.3032F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(53, 46)
                        .mirror(false)
                        .addBox(-1.2572F, -36.5F, -3.8929F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        PartDefinition body = parts.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 11)
                        .mirror(false)
                        .addBox(-5.0F, -24.0F, -5.0F, 10.0F, 5.0F, 10.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 26)
                        .mirror(false)
                        .addBox(-5.0F, -19.0F, -5.0F, 9.0F, 3.0F, 9.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(28, 35)
                        .mirror(false)
                        .addBox(-5.0F, -16.0F, -5.0F, 8.0F, 3.0F, 8.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition bone = body.addOrReplaceChild("bone", CubeListBuilder.create()
                        .texOffs(32, 68)
                        .mirror(false)
                        .addBox(-1.7929F, -24.25F, -2.7929F, 4.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 26)
                        .mirror(false)
                        .addBox(-0.7929F, -27.0F, -0.7929F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(28, 46)
                        .mirror(false)
                        .addBox(2.2071F, -23.0F, -2.7929F, 6.0F, 8.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(0, 47)
                        .mirror(false)
                        .addBox(-7.7929F, -23.0F, -2.7929F, 6.0F, 8.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(75, 89)
                        .mirror(false)
                        .addBox(-8.7929F, -22.0F, -2.7929F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F))
                        .texOffs(48, 88)
                        .mirror(false)
                        .addBox(8.2071F, -22.0F, -2.7929F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(-0.1768F, 0.0F, 0.1768F, 0.0F, 0.7854F, 0.0F));

        bone.addOrReplaceChild(
                "bone21",
                CubeListBuilder.create()
                        .texOffs(97, 0)
                        .mirror(false)
                        .addBox(-20.8892F, -12.4962F, -2.6161F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.1768F, 0.0F, -0.1768F, 0.0F, 0.0F, 0.6981F));

        bone.addOrReplaceChild("bone22", CubeListBuilder.create()
                        .texOffs(114, 20)
                        .mirror(false)
                        .addBox(19.9357F, -12.4572F, -2.6161F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.1768F, 0.0F, -0.1768F, 0.0F, 0.0F, -0.6981F));

        PartDefinition rightArm = parts.addOrReplaceChild("rightArm", CubeListBuilder.create()
                        .texOffs(0, 85)
                        .mirror(false)
                        .addBox(19.4634F, -7.0412F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(-0.25F, 24.0F, 0.0F, 0.0F, 0.0F, -1.8326F));

        PartDefinition bone2 = rightArm.addOrReplaceChild("bone2", CubeListBuilder.create()
                        .texOffs(84, 84)
                        .mirror(false)
                        .addBox(13.3352F, -20.8296F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition bone4 = bone2.addOrReplaceChild("bone4", CubeListBuilder.create()
                        .texOffs(83, 0)
                        .mirror(false)
                        .addBox(-3.1737F, -29.5281F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        PartDefinition bone6 = bone4.addOrReplaceChild("bone6", CubeListBuilder.create()
                        .texOffs(60, 81)
                        .mirror(false)
                        .addBox(-24.6598F, -21.5491F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

        PartDefinition bone8 = bone6.addOrReplaceChild("bone8", CubeListBuilder.create()
                        .texOffs(12, 80)
                        .mirror(false)
                        .addBox(-32.742F, -5.6566F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        bone8.addOrReplaceChild("bone10", CubeListBuilder.create()
                        .texOffs(74, 77)
                        .mirror(false)
                        .addBox(-23.4136F, 18.5762F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));

        PartDefinition leftArm = parts.addOrReplaceChild("leftArm", CubeListBuilder.create()
                        .texOffs(40, 85)
                        .mirror(false)
                        .addBox(-22.5281F, -7.2827F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 1.8326F));

        PartDefinition bone3 = leftArm.addOrReplaceChild("bone3", CubeListBuilder.create()
                        .texOffs(26, 84)
                        .mirror(false)
                        .addBox(-16.2705F, -21.0711F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition bone5 = bone3.addOrReplaceChild("bone5", CubeListBuilder.create()
                        .texOffs(82, 68)
                        .mirror(false)
                        .addBox(0.3784F, -29.6715F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        PartDefinition bone7 = bone5.addOrReplaceChild("bone7", CubeListBuilder.create()
                        .texOffs(80, 59)
                        .mirror(false)
                        .addBox(21.9013F, -21.4844F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.8727F));

        PartDefinition bone9 = bone7.addOrReplaceChild("bone9", CubeListBuilder.create()
                        .texOffs(77, 25)
                        .mirror(false)
                        .addBox(29.8854F, -5.4518F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        bone9.addOrReplaceChild("bone11", CubeListBuilder.create()
                        .texOffs(76, 11)
                        .mirror(false)
                        .addBox(20.3281F, 18.8112F, -2.0F, 3.0F, 5.0F, 4.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.9599F));

        PartDefinition rightLeg = parts.addOrReplaceChild("rightLeg", CubeListBuilder.create()
                        .texOffs(0, 95)
                        .mirror(false)
                        .addBox(-7.75F, -15.0F, -0.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition bone12 = rightLeg.addOrReplaceChild("bone12", CubeListBuilder.create()
                        .texOffs(94, 60)
                        .mirror(false)
                        .addBox(-4.8892F, -11.7298F, -0.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        bone12.addOrReplaceChild("bone14", CubeListBuilder.create()
                        .texOffs(86, 93)
                        .mirror(false)
                        .addBox(-2.9723F, -7.8306F, -0.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition leftLeg = parts.addOrReplaceChild("leftLeg", CubeListBuilder.create()
                        .texOffs(45, 95)
                        .mirror(false)
                        .addBox(4.75F, -15.0F, -0.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition bone13 = leftLeg.addOrReplaceChild("bone13", CubeListBuilder.create()
                        .texOffs(33, 94)
                        .mirror(false)
                        .addBox(1.8977F, -11.6651F, -0.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        bone13.addOrReplaceChild("bone15", CubeListBuilder.create()
                        .texOffs(21, 93)
                        .mirror(false)
                        .addBox(0.0058F, -7.7056F, -0.5F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE.extend(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(Horas entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.hat.visible = false;
        this.head.render(matrixStack, buffer, packedLight, packedOverlay);
        this.body.render(matrixStack, buffer, packedLight, packedOverlay);
        this.leftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        this.rightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        this.leftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        this.rightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
    }


}
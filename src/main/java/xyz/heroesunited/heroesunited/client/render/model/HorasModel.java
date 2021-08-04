package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

public class HorasModel extends BipedEntityModel<Horas> {

    public HorasModel(ModelPart root) {
        super(root, RenderLayer::getEntityTranslucent);
    }

    public static TexturedModelData createLayerDefinition() {
        ModelData mesh = new ModelData();
        ModelPartData parts = mesh.getRoot();

        ModelPartData head = parts.addChild("head", ModelPartBuilder.create()
                        .uv(55, 20)
                        .mirrored(false)
                        .cuboid(-5.0F, -34.25F, -7.75F, 10.0F, 7.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(52, 68)
                        .mirrored(false)
                        .cuboid(-6.5F, -32.75F, -8.25F, 5.0F, 4.0F, 4.0F, Dilation.NONE.add(0.0F))
                        .uv(94, 81)
                        .mirrored(false)
                        .cuboid(-6.766F, -30.3928F, -9.25F, 1.0F, 2.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(57, 90)
                        .mirrored(false)
                        .cuboid(-6.766F, -33.1072F, -9.25F, 1.0F, 3.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(30, 11)
                        .mirrored(false)
                        .cuboid(-2.234F, -33.1072F, -9.25F, 1.0F, 3.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(27, 27)
                        .mirrored(false)
                        .cuboid(1.234F, -33.1072F, -9.25F, 1.0F, 3.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 31)
                        .mirrored(false)
                        .cuboid(-2.234F, -30.3928F, -9.25F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 17)
                        .mirrored(false)
                        .cuboid(1.234F, -30.3928F, -9.25F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(92, 49)
                        .mirrored(false)
                        .cuboid(5.766F, -30.3928F, -9.25F, 1.0F, 2.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(72, 62)
                        .mirrored(false)
                        .cuboid(5.766F, -33.1072F, -9.25F, 1.0F, 3.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(87, 30)
                        .mirrored(false)
                        .cuboid(-6.0F, -33.75F, -9.25F, 4.0F, 1.0F, 4.0F, Dilation.NONE.add(0.0F))
                        .uv(86, 9)
                        .mirrored(false)
                        .cuboid(-6.0F, -28.75F, -9.25F, 4.0F, 1.0F, 4.0F, Dilation.NONE.add(0.0F))
                        .uv(87, 24)
                        .mirrored(false)
                        .cuboid(2.0F, -33.75F, -9.25F, 4.0F, 1.0F, 4.0F, Dilation.NONE.add(0.0F))
                        .uv(18, 47)
                        .mirrored(false)
                        .cuboid(2.0F, -28.75F, -9.25F, 4.0F, 1.0F, 4.0F, Dilation.NONE.add(0.0F))
                        .uv(10, 68)
                        .mirrored(false)
                        .cuboid(1.5F, -32.75F, -8.25F, 5.0F, 4.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 39)
                        .mirrored(false)
                        .cuboid(-5.0F, -27.25F, -6.75F, 10.0F, 1.0F, 7.0F, Dilation.NONE.add(0.0F))
                        .uv(34, 0)
                        .mirrored(false)
                        .cuboid(-5.0F, -34.75F, -6.75F, 10.0F, 1.0F, 7.0F, Dilation.NONE.add(0.0F))
                        .uv(52, 52)
                        .mirrored(false)
                        .cuboid(-5.0F, -34.25F, 6.6423F, 10.0F, 7.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(30, 11)
                        .mirrored(false)
                        .cuboid(-5.0F, -34.75F, -0.0897F, 10.0F, 1.0F, 8.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 0)
                        .mirrored(false)
                        .cuboid(-6.0F, -35.0F, -4.5897F, 12.0F, 1.0F, 10.0F, Dilation.NONE.add(0.0F))
                        .uv(55, 29)
                        .mirrored(false)
                        .cuboid(-3.0F, -36.0F, -1.5897F, 6.0F, 1.0F, 4.0F, Dilation.NONE.add(0.0F))
                        .uv(6, 0)
                        .mirrored(false)
                        .cuboid(-0.5F, -44.0F, -0.0897F, 1.0F, 8.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(120, 0)
                        .mirrored(false)
                        .cuboid(-1.5F, -41.75F, -0.0897F, 3.0F, 1.0F, 1.0F, Dilation.NONE.add(-0.25F))
                        .uv(116, 0)
                        .mirrored(false)
                        .cuboid(-2.5F, -40.0F, -0.0897F, 5.0F, 1.0F, 1.0F, Dilation.NONE.add(-0.25F))
                        .uv(40, 20)
                        .mirrored(false)
                        .cuboid(2.5F, -45.5F, -0.0897F, 1.0F, 4.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(47, 20)
                        .mirrored(false)
                        .cuboid(-4.0F, -36.5F, -0.3218F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 42)
                        .mirrored(false)
                        .cuboid(-4.0F, -36.5F, -0.8577F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(27, 38)
                        .mirrored(false)
                        .cuboid(3.0F, -36.5F, -0.8577F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 47)
                        .mirrored(false)
                        .cuboid(3.0F, -36.5F, -0.3218F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 81)
                        .mirrored(false)
                        .cuboid(-3.0F, -36.5F, -2.5897F, 6.0F, 2.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(44, 8)
                        .mirrored(false)
                        .cuboid(-3.0F, -36.5F, 2.4103F, 6.0F, 2.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(27, 26)
                        .mirrored(false)
                        .cuboid(-5.0F, -27.25F, -0.3577F, 10.0F, 1.0F, 8.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 68)
                        .mirrored(false)
                        .cuboid(6.0F, -34.25F, -2.5538F, 2.0F, 7.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(16, 67)
                        .mirrored(false)
                        .cuboid(-8.0F, -34.25F, -2.5538F, 2.0F, 7.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(64, 70)
                        .mirrored(false)
                        .cuboid(-8.5F, -33.25F, -2.5538F, 1.0F, 5.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(69, 29)
                        .mirrored(false)
                        .cuboid(7.5F, -33.25F, -2.5538F, 1.0F, 5.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(9, 89)
                        .mirrored(false)
                        .cuboid(8.0F, -32.75F, -2.0538F, 1.0F, 4.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(69, 86)
                        .mirrored(false)
                        .cuboid(-9.0F, -32.75F, -2.0538F, 1.0F, 4.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(90, 56)
                        .mirrored(false)
                        .cuboid(-7.5F, -27.25F, 0.3122F, 5.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(90, 14)
                        .mirrored(false)
                        .cuboid(-7.5F, -34.75F, 0.5801F, 5.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(90, 45)
                        .mirrored(false)
                        .cuboid(-7.5F, -27.25F, -2.4199F, 5.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(90, 41)
                        .mirrored(false)
                        .cuboid(-7.5F, -34.75F, -2.4199F, 5.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(84, 77)
                        .mirrored(false)
                        .cuboid(1.5F, -27.25F, 0.3122F, 6.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(58, 15)
                        .mirrored(false)
                        .cuboid(1.5F, -34.75F, 0.5801F, 6.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(80, 37)
                        .mirrored(false)
                        .cuboid(1.5F, -27.25F, -2.4199F, 6.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F))
                        .uv(79, 20)
                        .mirrored(false)
                        .cuboid(1.5F, -34.75F, -2.4199F, 6.0F, 1.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        head.addChild("bone20", ModelPartBuilder.create()
                        .uv(52, 38)
                        .mirrored(false)
                        .cuboid(-23.2262F, -24.5684F, -9.25F, 1.0F, 1.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(46, 46)
                        .mirrored(false)
                        .cuboid(-17.0978F, -29.7107F, -9.25F, 1.0F, 1.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(50, 61)
                        .mirrored(false)
                        .cuboid(-23.4336F, -18.401F, -9.25F, 1.0F, 1.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(34, 4)
                        .mirrored(false)
                        .cuboid(-17.3053F, -23.5433F, -9.25F, 1.0F, 1.0F, 2.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        head.addChild("bone19", ModelPartBuilder.create()
                        .uv(26, 67)
                        .mirrored(false)
                        .cuboid(16.0978F, -29.7107F, -9.25F, 1.0F, 1.0F, 5.0F, Dilation.NONE.add(0.0F))
                        .uv(30, 16)
                        .mirrored(false)
                        .cuboid(22.2262F, -24.5684F, -9.25F, 1.0F, 1.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(52, 35)
                        .mirrored(false)
                        .cuboid(16.3053F, -23.5433F, -9.25F, 1.0F, 1.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(40, 20)
                        .mirrored(false)
                        .cuboid(22.4336F, -18.401F, -9.25F, 1.0F, 1.0F, 5.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        head.addChild("bone18", ModelPartBuilder.create()
                        .uv(0, 0)
                        .mirrored(false)
                        .cuboid(39.3997F, -21.8657F, -0.5897F, 1.0F, 8.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 14)
                        .mirrored(false)
                        .cuboid(38.3997F, -22.8657F, -0.5897F, 3.0F, 1.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 11)
                        .mirrored(false)
                        .cuboid(38.3997F, -25.8657F, -0.5897F, 3.0F, 1.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 38)
                        .mirrored(false)
                        .cuboid(40.3997F, -24.8657F, -0.5897F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(34, 0)
                        .mirrored(false)
                        .cuboid(38.3997F, -24.8657F, -0.5897F, 1.0F, 2.0F, 2.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

        head.addChild("bone17", ModelPartBuilder.create()
                        .uv(76, 50)
                        .mirrored(false)
                        .cuboid(-4.2117F, -34.25F, -8.2051F, 6.0F, 7.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(61, 0)
                        .mirrored(false)
                        .cuboid(-3.3457F, -27.25F, -7.7051F, 5.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(18, 60)
                        .mirrored(false)
                        .cuboid(-3.3457F, -34.75F, -7.7051F, 5.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(74, 41)
                        .mirrored(false)
                        .cuboid(-1.0155F, -34.25F, 6.6513F, 6.0F, 7.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(53, 43)
                        .mirrored(false)
                        .cuboid(-0.8816F, -27.25F, 1.1513F, 5.0F, 1.0F, 7.0F, Dilation.NONE.add(0.0F))
                        .uv(52, 35)
                        .mirrored(false)
                        .cuboid(-0.6495F, -34.75F, 1.2853F, 5.0F, 1.0F, 7.0F, Dilation.NONE.add(0.0F))
                        .uv(34, 60)
                        .mirrored(false)
                        .cuboid(-0.5466F, -36.5F, 3.3032F, 2.0F, 2.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(58, 8)
                        .mirrored(false)
                        .cuboid(-0.7428F, -36.5F, -3.8929F, 2.0F, 2.0F, 1.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        head.addChild("bone16", ModelPartBuilder.create()
                        .uv(48, 76)
                        .mirrored(false)
                        .cuboid(-1.7883F, -34.25F, -8.2051F, 6.0F, 7.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(34, 61)
                        .mirrored(false)
                        .cuboid(-1.6543F, -27.25F, -7.7051F, 5.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 61)
                        .mirrored(false)
                        .cuboid(-1.6543F, -34.75F, -7.7051F, 5.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(32, 75)
                        .mirrored(false)
                        .cuboid(-4.9845F, -34.25F, 6.6513F, 6.0F, 7.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(56, 61)
                        .mirrored(false)
                        .cuboid(-4.1184F, -27.25F, 2.1513F, 5.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(58, 8)
                        .mirrored(false)
                        .cuboid(-4.3505F, -34.75F, 2.2853F, 5.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(58, 11)
                        .mirrored(false)
                        .cuboid(-1.4534F, -36.5F, 3.3032F, 2.0F, 2.0F, 1.0F, Dilation.NONE.add(0.0F))
                        .uv(53, 46)
                        .mirrored(false)
                        .cuboid(-1.2572F, -36.5F, -3.8929F, 2.0F, 2.0F, 1.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData body = parts.addChild("body", ModelPartBuilder.create()
                        .uv(0, 11)
                        .mirrored(false)
                        .cuboid(-5.0F, -24.0F, -5.0F, 10.0F, 5.0F, 10.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 26)
                        .mirrored(false)
                        .cuboid(-5.0F, -19.0F, -5.0F, 9.0F, 3.0F, 9.0F, Dilation.NONE.add(0.0F))
                        .uv(28, 35)
                        .mirrored(false)
                        .cuboid(-5.0F, -16.0F, -5.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone = body.addChild("bone", ModelPartBuilder.create()
                        .uv(32, 68)
                        .mirrored(false)
                        .cuboid(-1.7929F, -24.25F, -2.7929F, 4.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 26)
                        .mirrored(false)
                        .cuboid(-0.7929F, -27.0F, -0.7929F, 2.0F, 3.0F, 2.0F, Dilation.NONE.add(0.0F))
                        .uv(28, 46)
                        .mirrored(false)
                        .cuboid(2.2071F, -23.0F, -2.7929F, 6.0F, 8.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(0, 47)
                        .mirrored(false)
                        .cuboid(-7.7929F, -23.0F, -2.7929F, 6.0F, 8.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(75, 89)
                        .mirrored(false)
                        .cuboid(-8.7929F, -22.0F, -2.7929F, 1.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F))
                        .uv(48, 88)
                        .mirrored(false)
                        .cuboid(8.2071F, -22.0F, -2.7929F, 1.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(-0.1768F, 0.0F, 0.1768F, 0.0F, 0.7854F, 0.0F));

        bone.addChild(
                "bone21",
                ModelPartBuilder.create()
                        .uv(97, 0)
                        .mirrored(false)
                        .cuboid(-20.8892F, -12.4962F, -2.6161F, 1.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.1768F, 0.0F, -0.1768F, 0.0F, 0.0F, 0.6981F));

        bone.addChild("bone22", ModelPartBuilder.create()
                        .uv(114, 20)
                        .mirrored(false)
                        .cuboid(19.9357F, -12.4572F, -2.6161F, 1.0F, 1.0F, 6.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.1768F, 0.0F, -0.1768F, 0.0F, 0.0F, -0.6981F));

        ModelPartData rightArm = parts.addChild("rightArm", ModelPartBuilder.create()
                        .uv(0, 85)
                        .mirrored(false)
                        .cuboid(19.4634F, -7.0412F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(-0.25F, 24.0F, 0.0F, 0.0F, 0.0F, -1.8326F));

        ModelPartData bone2 = rightArm.addChild("bone2", ModelPartBuilder.create()
                        .uv(84, 84)
                        .mirrored(false)
                        .cuboid(13.3352F, -20.8296F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        ModelPartData bone4 = bone2.addChild("bone4", ModelPartBuilder.create()
                        .uv(83, 0)
                        .mirrored(false)
                        .cuboid(-3.1737F, -29.5281F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        ModelPartData bone6 = bone4.addChild("bone6", ModelPartBuilder.create()
                        .uv(60, 81)
                        .mirrored(false)
                        .cuboid(-24.6598F, -21.5491F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

        ModelPartData bone8 = bone6.addChild("bone8", ModelPartBuilder.create()
                        .uv(12, 80)
                        .mirrored(false)
                        .cuboid(-32.742F, -5.6566F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        bone8.addChild("bone10", ModelPartBuilder.create()
                        .uv(74, 77)
                        .mirrored(false)
                        .cuboid(-23.4136F, 18.5762F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));

        ModelPartData leftArm = parts.addChild("leftArm", ModelPartBuilder.create()
                        .uv(40, 85)
                        .mirrored(false)
                        .cuboid(-22.5281F, -7.2827F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 1.8326F));

        ModelPartData bone3 = leftArm.addChild("bone3", ModelPartBuilder.create()
                        .uv(26, 84)
                        .mirrored(false)
                        .cuboid(-16.2705F, -21.0711F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData bone5 = bone3.addChild("bone5", ModelPartBuilder.create()
                        .uv(82, 68)
                        .mirrored(false)
                        .cuboid(0.3784F, -29.6715F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        ModelPartData bone7 = bone5.addChild("bone7", ModelPartBuilder.create()
                        .uv(80, 59)
                        .mirrored(false)
                        .cuboid(21.9013F, -21.4844F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.8727F));

        ModelPartData bone9 = bone7.addChild("bone9", ModelPartBuilder.create()
                        .uv(77, 25)
                        .mirrored(false)
                        .cuboid(29.8854F, -5.4518F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        bone9.addChild("bone11", ModelPartBuilder.create()
                        .uv(76, 11)
                        .mirrored(false)
                        .cuboid(20.3281F, 18.8112F, -2.0F, 3.0F, 5.0F, 4.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.9599F));

        ModelPartData rightLeg = parts.addChild("rightLeg", ModelPartBuilder.create()
                        .uv(0, 95)
                        .mirrored(false)
                        .cuboid(-7.75F, -15.0F, -0.5F, 3.0F, 5.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone12 = rightLeg.addChild("bone12", ModelPartBuilder.create()
                        .uv(94, 60)
                        .mirrored(false)
                        .cuboid(-4.8892F, -11.7298F, -0.5F, 3.0F, 5.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        bone12.addChild("bone14", ModelPartBuilder.create()
                        .uv(86, 93)
                        .mirrored(false)
                        .cuboid(-2.9723F, -7.8306F, -0.5F, 3.0F, 5.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData leftLeg = parts.addChild("leftLeg", ModelPartBuilder.create()
                        .uv(45, 95)
                        .mirrored(false)
                        .cuboid(4.75F, -15.0F, -0.5F, 3.0F, 5.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone13 = leftLeg.addChild("bone13", ModelPartBuilder.create()
                        .uv(33, 94)
                        .mirrored(false)
                        .cuboid(1.8977F, -11.6651F, -0.5F, 3.0F, 5.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        bone13.addChild("bone15", ModelPartBuilder.create()
                        .uv(21, 93)
                        .mirrored(false)
                        .cuboid(0.0058F, -7.7056F, -0.5F, 3.0F, 5.0F, 3.0F, Dilation.NONE.add(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));
        return TexturedModelData.of(mesh, 128, 128);
    }

    @Override
    public void setupAnim(Horas entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.hat.visible = false;
        this.head.render(matrixStack, buffer, packedLight, packedOverlay);
        this.body.render(matrixStack, buffer, packedLight, packedOverlay);
        this.leftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        this.rightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        this.leftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        this.rightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
    }


}
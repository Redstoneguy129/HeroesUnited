package xyz.heroesunited.heroesunited.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;

@OnlyIn(Dist.CLIENT)
public class ModelHoras extends BipedModel<Horas> {
    private final ModelRenderer bone20;
    private final ModelRenderer bone19;
    private final ModelRenderer bone18;
    private final ModelRenderer bone17;
    private final ModelRenderer bone16;
    private final ModelRenderer bone;
    private final ModelRenderer bone21;
    private final ModelRenderer bone22;
    private final ModelRenderer bone3;
    private final ModelRenderer bone5;
    private final ModelRenderer bone7;
    private final ModelRenderer bone9;
    private final ModelRenderer bone11;
    private final ModelRenderer bone2;
    private final ModelRenderer bone4;
    private final ModelRenderer bone6;
    private final ModelRenderer bone8;
    private final ModelRenderer bone10;
    private final ModelRenderer bone13;
    private final ModelRenderer bone15;
    private final ModelRenderer bone12;
    private final ModelRenderer bone14;

    public ModelHoras() {
        super(1.0F);
        this.texHeight = 128;
        this.texWidth = 128;

        this.head = new ModelRenderer(this);
        this.head.setPos(0.0F, 24.0F, 0.0F);
        this.head.texOffs(55, 20).addBox(-5.0F, -34.25F, -7.75F, 10.0F, 7.0F, 2.0F, 0.0F, false);
        this.head.texOffs(52, 68).addBox(-6.5F, -32.75F, -8.25F, 5.0F, 4.0F, 4.0F, 0.0F, false);
        this.head.texOffs(94, 81).addBox(-6.766F, -30.3928F, -9.25F, 1.0F, 2.0F, 5.0F, 0.0F, false);
        this.head.texOffs(57, 90).addBox(-6.766F, -33.1072F, -9.25F, 1.0F, 3.0F, 5.0F, 0.0F, false);
        this.head.texOffs(30, 11).addBox(-2.234F, -33.1072F, -9.25F, 1.0F, 3.0F, 2.0F, 0.0F, false);
        this.head.texOffs(27, 27).addBox(1.234F, -33.1072F, -9.25F, 1.0F, 3.0F, 2.0F, 0.0F, false);
        this.head.texOffs(0, 31).addBox(-2.234F, -30.3928F, -9.25F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        this.head.texOffs(0, 17).addBox(1.234F, -30.3928F, -9.25F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        this.head.texOffs(92, 49).addBox(5.766F, -30.3928F, -9.25F, 1.0F, 2.0F, 5.0F, 0.0F, false);
        this.head.texOffs(72, 62).addBox(5.766F, -33.1072F, -9.25F, 1.0F, 3.0F, 6.0F, 0.0F, false);
        this.head.texOffs(87, 30).addBox(-6.0F, -33.75F, -9.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        this.head.texOffs(86, 9).addBox(-6.0F, -28.75F, -9.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        this.head.texOffs(87, 24).addBox(2.0F, -33.75F, -9.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        this.head.texOffs(18, 47).addBox(2.0F, -28.75F, -9.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        this.head.texOffs(10, 68).addBox(1.5F, -32.75F, -8.25F, 5.0F, 4.0F, 1.0F, 0.0F, false);
        this.head.texOffs(0, 39).addBox(-5.0F, -27.25F, -6.75F, 10.0F, 1.0F, 7.0F, 0.0F, false);
        this.head.texOffs(34, 0).addBox(-5.0F, -34.75F, -6.75F, 10.0F, 1.0F, 7.0F, 0.0F, false);
        this.head.texOffs(52, 52).addBox(-5.0F, -34.25F, 6.6423F, 10.0F, 7.0F, 2.0F, 0.0F, false);
        this.head.texOffs(30, 11).addBox(-5.0F, -34.75F, -0.0897F, 10.0F, 1.0F, 8.0F, 0.0F, false);
        this.head.texOffs(0, 0).addBox(-6.0F, -35.0F, -4.5897F, 12.0F, 1.0F, 10.0F, 0.0F, false);
        this.head.texOffs(55, 29).addBox(-3.0F, -36.0F, -1.5897F, 6.0F, 1.0F, 4.0F, 0.0F, false);
        this.head.texOffs(6, 0).addBox(-0.5F, -44.0F, -0.0897F, 1.0F, 8.0F, 1.0F, 0.0F, false);
        this.head.texOffs(120, 0).addBox(-1.5F, -41.75F, -0.0897F, 3.0F, 1.0F, 1.0F, -0.25F, false);
        this.head.texOffs(116, 0).addBox(-2.5F, -40.0F, -0.0897F, 5.0F, 1.0F, 1.0F, -0.25F, false);
        this.head.texOffs(40, 20).addBox(2.5F, -45.5F, -0.0897F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        this.head.texOffs(47, 20).addBox(-4.0F, -36.5F, -0.3218F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        this.head.texOffs(0, 42).addBox(-4.0F, -36.5F, -0.8577F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        this.head.texOffs(27, 38).addBox(3.0F, -36.5F, -0.8577F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        this.head.texOffs(0, 47).addBox(3.0F, -36.5F, -0.3218F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        this.head.texOffs(0, 81).addBox(-3.0F, -36.5F, -2.5897F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        this.head.texOffs(44, 8).addBox(-3.0F, -36.5F, 2.4103F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        this.head.texOffs(27, 26).addBox(-5.0F, -27.25F, -0.3577F, 10.0F, 1.0F, 8.0F, 0.0F, false);
        this.head.texOffs(0, 68).addBox(6.0F, -34.25F, -2.5538F, 2.0F, 7.0F, 6.0F, 0.0F, false);
        this.head.texOffs(16, 67).addBox(-8.0F, -34.25F, -2.5538F, 2.0F, 7.0F, 6.0F, 0.0F, false);
        this.head.texOffs(64, 70).addBox(-8.5F, -33.25F, -2.5538F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        this.head.texOffs(69, 29).addBox(7.5F, -33.25F, -2.5538F, 1.0F, 5.0F, 6.0F, 0.0F, false);
        this.head.texOffs(9, 89).addBox(8.0F, -32.75F, -2.0538F, 1.0F, 4.0F, 5.0F, 0.0F, false);
        this.head.texOffs(69, 86).addBox(-9.0F, -32.75F, -2.0538F, 1.0F, 4.0F, 5.0F, 0.0F, false);
        this.head.texOffs(90, 56).addBox(-7.5F, -27.25F, 0.3122F, 5.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(90, 14).addBox(-7.5F, -34.75F, 0.5801F, 5.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(90, 45).addBox(-7.5F, -27.25F, -2.4199F, 5.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(90, 41).addBox(-7.5F, -34.75F, -2.4199F, 5.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(84, 77).addBox(1.5F, -27.25F, 0.3122F, 6.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(58, 15).addBox(1.5F, -34.75F, 0.5801F, 6.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(80, 37).addBox(1.5F, -27.25F, -2.4199F, 6.0F, 1.0F, 3.0F, 0.0F, false);
        this.head.texOffs(79, 20).addBox(1.5F, -34.75F, -2.4199F, 6.0F, 1.0F, 3.0F, 0.0F, false);

        bone20 = new ModelRenderer(this);
        bone20.setPos(0.0F, 0.0F, 0.0F);
        this.head.addChild(bone20);
        setRotationAngle(bone20, 0.0F, 0.0F, 0.6981F);
        bone20.texOffs(52, 38).addBox(-23.2262F, -24.5684F, -9.25F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone20.texOffs(46, 46).addBox(-17.0978F, -29.7107F, -9.25F, 1.0F, 1.0F, 5.0F, 0.0F, false);
        bone20.texOffs(50, 61).addBox(-23.4336F, -18.401F, -9.25F, 1.0F, 1.0F, 5.0F, 0.0F, false);
        bone20.texOffs(34, 4).addBox(-17.3053F, -23.5433F, -9.25F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        bone19 = new ModelRenderer(this);
        bone19.setPos(0.0F, 0.0F, 0.0F);
        this.head.addChild(bone19);
        setRotationAngle(bone19, 0.0F, 0.0F, -0.6981F);
        bone19.texOffs(26, 67).addBox(16.0978F, -29.7107F, -9.25F, 1.0F, 1.0F, 5.0F, 0.0F, false);
        bone19.texOffs(30, 16).addBox(22.2262F, -24.5684F, -9.25F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone19.texOffs(52, 35).addBox(16.3053F, -23.5433F, -9.25F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        bone19.texOffs(40, 20).addBox(22.4336F, -18.401F, -9.25F, 1.0F, 1.0F, 5.0F, 0.0F, false);

        bone18 = new ModelRenderer(this);
        bone18.setPos(0.0F, 0.0F, 0.0F);
        this.head.addChild(bone18);
        setRotationAngle(bone18, 0.0F, 0.0F, -1.1345F);
        bone18.texOffs(0, 0).addBox(39.3997F, -21.8657F, -0.5897F, 1.0F, 8.0F, 2.0F, 0.0F, false);
        bone18.texOffs(0, 14).addBox(38.3997F, -22.8657F, -0.5897F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        bone18.texOffs(0, 11).addBox(38.3997F, -25.8657F, -0.5897F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        bone18.texOffs(0, 38).addBox(40.3997F, -24.8657F, -0.5897F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        bone18.texOffs(34, 0).addBox(38.3997F, -24.8657F, -0.5897F, 1.0F, 2.0F, 2.0F, 0.0F, false);

        bone17 = new ModelRenderer(this);
        bone17.setPos(0.0F, 0.0F, 0.0F);
        this.head.addChild(bone17);
        setRotationAngle(bone17, 0.0F, -1.0472F, 0.0F);
        bone17.texOffs(76, 50).addBox(-4.2117F, -34.25F, -8.2051F, 6.0F, 7.0F, 2.0F, 0.0F, false);
        bone17.texOffs(61, 0).addBox(-3.3457F, -27.25F, -7.7051F, 5.0F, 1.0F, 6.0F, 0.0F, false);
        bone17.texOffs(18, 60).addBox(-3.3457F, -34.75F, -7.7051F, 5.0F, 1.0F, 6.0F, 0.0F, false);
        bone17.texOffs(74, 41).addBox(-1.0155F, -34.25F, 6.6513F, 6.0F, 7.0F, 2.0F, 0.0F, false);
        bone17.texOffs(53, 43).addBox(-0.8816F, -27.25F, 1.1513F, 5.0F, 1.0F, 7.0F, 0.0F, false);
        bone17.texOffs(52, 35).addBox(-0.6495F, -34.75F, 1.2853F, 5.0F, 1.0F, 7.0F, 0.0F, false);
        bone17.texOffs(34, 60).addBox(-0.5466F, -36.5F, 3.3032F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        bone17.texOffs(58, 8).addBox(-0.7428F, -36.5F, -3.8929F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        bone16 = new ModelRenderer(this);
        bone16.setPos(0.0F, 0.0F, 0.0F);
        this.head.addChild(bone16);
        setRotationAngle(bone16, 0.0F, 1.0472F, 0.0F);
        bone16.texOffs(48, 76).addBox(-1.7883F, -34.25F, -8.2051F, 6.0F, 7.0F, 2.0F, 0.0F, false);
        bone16.texOffs(34, 61).addBox(-1.6543F, -27.25F, -7.7051F, 5.0F, 1.0F, 6.0F, 0.0F, false);
        bone16.texOffs(0, 61).addBox(-1.6543F, -34.75F, -7.7051F, 5.0F, 1.0F, 6.0F, 0.0F, false);
        bone16.texOffs(32, 75).addBox(-4.9845F, -34.25F, 6.6513F, 6.0F, 7.0F, 2.0F, 0.0F, false);
        bone16.texOffs(56, 61).addBox(-4.1184F, -27.25F, 2.1513F, 5.0F, 1.0F, 6.0F, 0.0F, false);
        bone16.texOffs(58, 8).addBox(-4.3505F, -34.75F, 2.2853F, 5.0F, 1.0F, 6.0F, 0.0F, false);
        bone16.texOffs(58, 11).addBox(-1.4534F, -36.5F, 3.3032F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        bone16.texOffs(53, 46).addBox(-1.2572F, -36.5F, -3.8929F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        this.body = new ModelRenderer(this);
        this.body.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(this.body, 0.0F, -0.7854F, 0.0F);
        this.body.texOffs(0, 11).addBox(-5.0F, -24.0F, -5.0F, 10.0F, 5.0F, 10.0F, 0.0F, false);
        this.body.texOffs(0, 26).addBox(-5.0F, -19.0F, -5.0F, 9.0F, 3.0F, 9.0F, 0.0F, false);
        this.body.texOffs(28, 35).addBox(-5.0F, -16.0F, -5.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);

        bone = new ModelRenderer(this);
        bone.setPos(-0.1768F, 0.0F, 0.1768F);
        this.body.addChild(bone);
        setRotationAngle(bone, 0.0F, 0.7854F, 0.0F);
        bone.texOffs(32, 68).addBox(-1.7929F, -24.25F, -2.7929F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        bone.texOffs(0, 26).addBox(-0.7929F, -27.0F, -0.7929F, 2.0F, 3.0F, 2.0F, 0.0F, false);
        bone.texOffs(28, 46).addBox(2.2071F, -23.0F, -2.7929F, 6.0F, 8.0F, 6.0F, 0.0F, false);
        bone.texOffs(0, 47).addBox(-7.7929F, -23.0F, -2.7929F, 6.0F, 8.0F, 6.0F, 0.0F, false);
        bone.texOffs(75, 89).addBox(-8.7929F, -22.0F, -2.7929F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        bone.texOffs(48, 88).addBox(8.2071F, -22.0F, -2.7929F, 1.0F, 1.0F, 6.0F, 0.0F, false);

        bone21 = new ModelRenderer(this);
        bone21.setPos(0.1768F, 0.0F, -0.1768F);
        bone.addChild(bone21);
        setRotationAngle(bone21, 0.0F, 0.0F, 0.6981F);
        bone21.texOffs(97, 0).addBox(-20.8892F, -12.4962F, -2.6161F, 1.0F, 1.0F, 6.0F, 0.0F, false);

        bone22 = new ModelRenderer(this);
        bone22.setPos(0.1768F, 0.0F, -0.1768F);
        bone.addChild(bone22);
        setRotationAngle(bone22, 0.0F, 0.0F, -0.6981F);
        bone22.texOffs(114, 20).addBox(19.9357F, -12.4572F, -2.6161F, 1.0F, 1.0F, 6.0F, 0.0F, false);

        this.leftArm = new ModelRenderer(this);
        this.leftArm.setPos(0.0F, 24.0F, 0.0F);
        setRotationAngle(this.leftArm, 0.0F, 0.0F, 1.8326F);
        this.leftArm.texOffs(40, 85).addBox(-22.5281F, -7.2827F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setPos(0.0F, 0.0F, 0.0F);
        this.leftArm.addChild(bone3);
        setRotationAngle(bone3, 0.0F, 0.0F, -0.5236F);
        bone3.texOffs(26, 84).addBox(-16.2705F, -21.0711F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone5 = new ModelRenderer(this);
        bone5.setPos(0.0F, 0.0F, 0.0F);
        bone3.addChild(bone5);
        setRotationAngle(bone5, 0.0F, 0.0F, -0.6981F);
        bone5.texOffs(82, 68).addBox(0.3784F, -29.6715F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone7 = new ModelRenderer(this);
        bone7.setPos(0.0F, 0.0F, 0.0F);
        bone5.addChild(bone7);
        setRotationAngle(bone7, 0.0F, 0.0F, -0.8727F);
        bone7.texOffs(80, 59).addBox(21.9013F, -21.4844F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone9 = new ModelRenderer(this);
        bone9.setPos(0.0F, 0.0F, 0.0F);
        bone7.addChild(bone9);
        setRotationAngle(bone9, 0.0F, 0.0F, -0.6981F);
        bone9.texOffs(77, 25).addBox(29.8854F, -5.4518F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone11 = new ModelRenderer(this);
        bone11.setPos(0.0F, 0.0F, 0.0F);
        bone9.addChild(bone11);
        setRotationAngle(bone11, 0.0F, 0.0F, -0.9599F);
        bone11.texOffs(76, 11).addBox(20.3281F, 18.8112F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        this.rightArm = new ModelRenderer(this);
        this.rightArm.setPos(-0.25F, 24.0F, 0.0F);
        setRotationAngle(this.rightArm, 0.0F, 0.0F, -1.8326F);
        this.rightArm.texOffs(0, 85).addBox(19.4634F, -7.0412F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setPos(0.0F, 0.0F, 0.0F);
        this.rightArm.addChild(bone2);
        setRotationAngle(bone2, 0.0F, 0.0F, 0.5236F);
        bone2.texOffs(84, 84).addBox(13.3352F, -20.8296F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone4 = new ModelRenderer(this);
        bone4.setPos(0.0F, 0.0F, 0.0F);
        bone2.addChild(bone4);
        setRotationAngle(bone4, 0.0F, 0.0F, 0.6981F);
        bone4.texOffs(83, 0).addBox(-3.1737F, -29.5281F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone6 = new ModelRenderer(this);
        bone6.setPos(0.0F, 0.0F, 0.0F);
        bone4.addChild(bone6);
        setRotationAngle(bone6, 0.0F, 0.0F, 0.8727F);
        bone6.texOffs(60, 81).addBox(-24.6598F, -21.5491F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone8 = new ModelRenderer(this);
        bone8.setPos(0.0F, 0.0F, 0.0F);
        bone6.addChild(bone8);
        setRotationAngle(bone8, 0.0F, 0.0F, 0.6981F);
        bone8.texOffs(12, 80).addBox(-32.742F, -5.6566F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        bone10 = new ModelRenderer(this);
        bone10.setPos(0.0F, 0.0F, 0.0F);
        bone8.addChild(bone10);
        setRotationAngle(bone10, 0.0F, 0.0F, 0.9599F);
        bone10.texOffs(74, 77).addBox(-23.4136F, 18.5762F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, false);

        this.leftLeg = new ModelRenderer(this);
        this.leftLeg.setPos(0.0F, 24.0F, 0.0F);
        this.leftLeg.texOffs(45, 95).addBox(4.75F, -15.0F, -0.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        bone13 = new ModelRenderer(this);
        bone13.setPos(0.0F, 0.0F, 0.0F);
        this.leftLeg.addChild(bone13);
        setRotationAngle(bone13, 0.0F, 0.0F, 0.2618F);
        bone13.texOffs(33, 94).addBox(1.8977F, -11.6651F, -0.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        bone15 = new ModelRenderer(this);
        bone15.setPos(0.0F, 0.0F, 0.0F);
        bone13.addChild(bone15);
        setRotationAngle(bone15, 0.0F, 0.0F, 0.2618F);
        bone15.texOffs(21, 93).addBox(0.0058F, -7.7056F, -0.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        this.rightLeg = new ModelRenderer(this);
        this.rightLeg.setPos(0.0F, 24.0F, 0.0F);
        this.rightLeg.texOffs(0, 95).addBox(-7.75F, -15.0F, -0.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        bone12 = new ModelRenderer(this);
        bone12.setPos(0.0F, 0.0F, 0.0F);
        this.rightLeg.addChild(bone12);
        setRotationAngle(bone12, 0.0F, 0.0F, -0.2618F);
        bone12.texOffs(94, 60).addBox(-4.8892F, -11.7298F, -0.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

        bone14 = new ModelRenderer(this);
        bone14.setPos(0.0F, 0.0F, 0.0F);
        bone12.addChild(bone14);
        setRotationAngle(bone14, 0.0F, 0.0F, -0.2618F);
        bone14.texOffs(86, 93).addBox(-2.9723F, -7.8306F, -0.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(Horas entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(matrixStack, buffer, packedLight, packedOverlay);
        this.body.render(matrixStack, buffer, packedLight, packedOverlay);
        this.leftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        this.rightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        this.leftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        this.rightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
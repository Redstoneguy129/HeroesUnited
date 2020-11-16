package xyz.heroesunited.heroesunited.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import xyz.heroesunited.heroesunited.client.events.HURenderLayerEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

public class HULayerRenderer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    public LivingRenderer<T, M> entityRendererIn;

    public HULayerRenderer(LivingRenderer entityRendererIn) {
        super(entityRendererIn);
        this.entityRendererIn = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (Suit.getSuit(entity) != null && entity.isChild() == false) {
            Suit.getSuit(entity).renderLayer(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
        MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent(entityRendererIn, entity, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));

        if (entityRendererIn instanceof PlayerRenderer && entity instanceof AbstractClientPlayerEntity) {
            PlayerRenderer playerRenderer = (PlayerRenderer) entityRendererIn;
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
            for (AbilityType type : AbilityHelper.getAbilities(player)) {
                type.create().render(playerRenderer, matrixStack, buffer, packedLight, player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
            MinecraftForge.EVENT_BUS.post(new HURenderLayerEvent.Player(playerRenderer, player, matrixStack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
        }
    }
}

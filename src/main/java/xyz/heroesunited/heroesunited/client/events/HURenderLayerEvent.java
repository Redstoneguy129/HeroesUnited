package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class HURenderLayerEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event {

    private final T livingEntity;
    private final LivingRenderer<T, M> renderer;
    private final MatrixStack matrixStack;
    private final IRenderTypeBuffer bufferIn;
    private final int packedLightIn;
    private float limbSwing;
    private float limbSwingAmount;
    private float partialTicks;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;

    public HURenderLayerEvent(LivingRenderer<T, M> renderer, T livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.livingEntity = livingEntity;
        this.renderer = renderer;
        this.matrixStack = matrixStack;
        this.bufferIn = bufferIn;
        this.packedLightIn = packedLightIn;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.partialTicks = partialTicks;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public IRenderTypeBuffer getBuffers() {
        return bufferIn;
    }

    public int getLight() {
        return packedLightIn;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public float getNetHeadYaw() {
        return netHeadYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public static class Player extends HURenderLayerEvent {

        private final PlayerRenderer renderer;
        private final AbstractClientPlayerEntity player;

        public Player(PlayerRenderer renderer, AbstractClientPlayerEntity player, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            this.player = player;
            this.renderer = renderer;
        }

        public AbstractClientPlayerEntity getPlayer() {
            return player;
        }

        public PlayerRenderer getRenderer() {
            return renderer;
        }
    }

    public static class Armor extends HURenderLayerEvent {

        public Armor(LivingRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Cancelable
        public static class Pre extends Armor {

            public Pre(LivingRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        public static class Post extends Armor {

            public Post(LivingRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }
    }
}

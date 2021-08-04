package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Just event.
 */
public abstract class HURenderLayerEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event {

    private final T livingEntity;
    private final LivingEntityRenderer<T, M> renderer;
    private final MatrixStack matrixStack;
    private final VertexConsumerProvider bufferIn;
    private final int packedLightIn;
    private float limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch;

    public HURenderLayerEvent(LivingEntityRenderer<T, M> renderer, T livingEntity, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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

    public T getLivingEntity() {
        return livingEntity;
    }

    public LivingEntityRenderer<T, M> getRenderer() {
        return renderer;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public VertexConsumerProvider getBuffers() {
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

    @Cancelable
    public static class Pre extends HURenderLayerEvent {
        public Pre(LivingEntityRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public static class Post extends HURenderLayerEvent {
        public Post(LivingEntityRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Cancelable
    public static class Accessories extends Player {
        public Accessories(PlayerEntityRenderer renderer, AbstractClientPlayerEntity player, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public static class Player extends HURenderLayerEvent {

        private final PlayerEntityRenderer renderer;
        private final AbstractClientPlayerEntity player;

        public Player(PlayerEntityRenderer renderer, AbstractClientPlayerEntity player, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            this.player = player;
            this.renderer = renderer;
        }

        public AbstractClientPlayerEntity getPlayer() {
            return player;
        }

        public PlayerEntityRenderer getRenderer() {
            return renderer;
        }
    }

    public static class Armor extends HURenderLayerEvent {

        public Armor(LivingEntityRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Cancelable
        public static class Pre extends Armor {

            public Pre(LivingEntityRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        public static class Post extends Armor {

            public Post(LivingEntityRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        public static class HUSetArmorPartVisibility extends Event {

            private BipedEntityModel armorModel;
            private EquipmentSlot slot;

            public HUSetArmorPartVisibility(BipedEntityModel modelIn, EquipmentSlot slotIn) {
                this.armorModel = modelIn;
                this.slot = slotIn;
            }

            public BipedEntityModel getArmorModel() {
                return armorModel;
            }

            public EquipmentSlot getSlot() {
                return slot;
            }

        }
    }
}

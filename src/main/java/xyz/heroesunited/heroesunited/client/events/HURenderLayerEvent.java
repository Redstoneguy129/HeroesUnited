package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Just event.
 * @TO-DO remake this, or make clear
 */
public abstract class HURenderLayerEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event {

    protected final T livingEntity;
    protected final LivingRenderer<T, M> renderer;
    protected final MatrixStack matrixStack;
    protected final IRenderTypeBuffer bufferIn;
    protected final int packedLightIn;
    protected float limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch;

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

    public T getLivingEntity() {
        return livingEntity;
    }

    public LivingRenderer<T, M> getRenderer() {
        return renderer;
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

    @Cancelable
    public static class Pre extends HURenderLayerEvent {
        public Pre(LivingRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public static class Post extends HURenderLayerEvent {
        public Post(LivingRenderer renderer, LivingEntity livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Cancelable
    public static class Accessories extends HURenderLayerEvent<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

        public Accessories(PlayerRenderer renderer, AbstractClientPlayerEntity player, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Override
        public PlayerRenderer getRenderer() {
            return (PlayerRenderer) renderer;
        }

        public AbstractClientPlayerEntity getPlayer() {
            return livingEntity;
        }
    }

    public static class Player extends HURenderLayerEvent<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

        public Player(PlayerRenderer renderer, AbstractClientPlayerEntity player, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Override
        public PlayerRenderer getRenderer() {
            return (PlayerRenderer) renderer;
        }

        public AbstractClientPlayerEntity getPlayer() {
            return livingEntity;
        }
    }

    public static class Armor<T extends LivingEntity> extends HURenderLayerEvent<T, BipedModel<T>> {

        public Armor(LivingRenderer renderer, T livingEntity, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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

        public static class HUSetArmorPartVisibility extends Event {

            private BipedModel armorModel;
            private EquipmentSlotType slot;

            public HUSetArmorPartVisibility(BipedModel modelIn, EquipmentSlotType slotIn) {
                this.armorModel = modelIn;
                this.slot = slotIn;
            }

            public BipedModel getArmorModel() {
                return armorModel;
            }

            public EquipmentSlotType getSlot() {
                return slot;
            }

        }
    }
}

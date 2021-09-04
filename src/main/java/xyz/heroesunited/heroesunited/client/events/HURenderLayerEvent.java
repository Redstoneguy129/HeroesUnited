package xyz.heroesunited.heroesunited.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Just event.
 * @TO-DO remake this, or make clear
 */
public abstract class HURenderLayerEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event {

    protected final T livingEntity;
    protected final LivingEntityRenderer<T, M> renderer;
    protected final PoseStack matrixStack;
    protected final MultiBufferSource bufferIn;
    protected final int packedLightIn;
    protected float limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch;

    public HURenderLayerEvent(LivingEntityRenderer<T, M> renderer, T livingEntity, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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

    public PoseStack getMatrixStack() {
        return matrixStack;
    }

    public MultiBufferSource getBuffers() {
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
        public Pre(LivingEntityRenderer renderer, LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public static class Post extends HURenderLayerEvent {
        public Post(LivingEntityRenderer renderer, LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Cancelable
    public static class Accessories extends HURenderLayerEvent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

        public Accessories(PlayerRenderer renderer, AbstractClientPlayer player, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Override
        public PlayerRenderer getRenderer() {
            return (PlayerRenderer) renderer;
        }

        public AbstractClientPlayer getPlayer() {
            return livingEntity;
        }
    }

    public static class Player extends HURenderLayerEvent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

        public Player(PlayerRenderer renderer, AbstractClientPlayer player, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, player, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Override
        public PlayerRenderer getRenderer() {
            return (PlayerRenderer) renderer;
        }

        public AbstractClientPlayer getPlayer() {
            return livingEntity;
        }
    }

    public static class Armor<T extends LivingEntity> extends HURenderLayerEvent<T, HumanoidModel<T>> {

        public Armor(LivingEntityRenderer renderer, T livingEntity, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Cancelable
        public static class Pre extends Armor {

            public Pre(LivingEntityRenderer renderer, LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        public static class Post extends Armor {

            public Post(LivingEntityRenderer renderer, LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                super(renderer, livingEntity, matrixStack, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }

        public static class HUSetArmorPartVisibility extends Event {

            private HumanoidModel armorModel;
            private EquipmentSlot slot;

            public HUSetArmorPartVisibility(HumanoidModel modelIn, EquipmentSlot slotIn) {
                this.armorModel = modelIn;
                this.slot = slotIn;
            }

            public HumanoidModel getArmorModel() {
                return armorModel;
            }

            public EquipmentSlot getSlot() {
                return slot;
            }

        }
    }
}

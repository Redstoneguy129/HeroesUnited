package xyz.heroesunited.heroesunited.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import xyz.heroesunited.heroesunited.client.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class GeckoSuitRenderer<T extends SuitItem> extends GeoArmorRenderer<T> {

    public GeckoSuitRenderer() {
        this(new GeckoSuitModel<>());
    }

    public GeckoSuitRenderer(GeoModel<T> model) {
        super(model);
        this.addRenderLayer(new GeoRenderLayer<T>(this) {
            @Override
            public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if (this.getRenderer() instanceof GeckoSuitRenderer<T> renderer && renderer.currentEntity instanceof LivingEntity livingEntity) {
                    HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(animatable.getSuit().getRegistryName());
                    if (layer != null) {
                        if (layer.getTexture("cape") != null && renderer.currentSlot.equals(EquipmentSlot.CHEST)) {
                            HUClientUtil.renderCape(renderer.baseModel, livingEntity, poseStack, bufferSource, packedLight, partialTick, layer.getTexture("cape"));
                        }
                        if (layer.getTexture("lights") != null) {
                            RenderType type = HUClientUtil.HURenderTypes.getLight(layer.getTexture("lights"));

                            renderer.reRender(bakedModel, poseStack, bufferSource, animatable, type,
                                    bufferSource.getBuffer(type), partialTick, packedLight, packedOverlay,
                                    1, 1, 1, 1);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void prepForRender(@Nullable Entity entity, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable HumanoidModel<?> baseModel) {
        super.prepForRender(entity, stack, slot, baseModel);
        if (this.getGeoModel() instanceof GeckoSuitModel<T> suitModel) {
            suitModel.setSlim(HUPlayerUtil.haveSmallArms(this.currentEntity));
        }
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public T getAnimatable() {
        return super.getAnimatable();
    }
}
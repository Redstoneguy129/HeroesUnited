package xyz.heroesunited.heroesunited.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import xyz.heroesunited.heroesunited.client.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class GeckoSuitRenderer<T extends SuitItem> extends GeoArmorRenderer<T> {

    public GeckoSuitRenderer() {
        this(new GeckoSuitModel<>());
    }

    public GeckoSuitRenderer(GeoModel<T> model) {
        super(model);
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
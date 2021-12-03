package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.HorasModel;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.util.HUCalendarHelper;
import xyz.heroesunited.heroesunited.util.HUModelLayers;

@OnlyIn(Dist.CLIENT)
public class HorasRenderer extends HumanoidMobRenderer<Horas, HorasModel> {

    public HorasRenderer(EntityRendererProvider.Context manager) {
        super(manager, new HorasModel(manager.bakeLayer(HUModelLayers.HORAS)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Horas entity) {
        return new ResourceLocation(HeroesUnited.MODID, "textures/entity/horas" + (HUCalendarHelper.isSnowTime() ? "_new_year.png" : ".png"));
    }
}

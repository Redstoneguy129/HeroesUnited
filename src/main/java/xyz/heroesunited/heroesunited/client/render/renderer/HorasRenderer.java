package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.ModelHoras;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.util.HUCalendarHelper;

@OnlyIn(Dist.CLIENT)
public class HorasRenderer extends BipedRenderer<Horas, ModelHoras> {

    public HorasRenderer(EntityRendererManager manager) {
        super(manager, new ModelHoras(), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Horas entity) {
        return new ResourceLocation(HeroesUnited.MODID, "textures/entity/horas" + (HUCalendarHelper.isSnowTime() ? "_new_year.png" : ".png"));
    }
}

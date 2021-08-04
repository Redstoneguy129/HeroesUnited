package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.HUClientListener;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.HorasModel;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.util.HUCalendarHelper;

@OnlyIn(Dist.CLIENT)
public class HorasRenderer extends BipedEntityRenderer<Horas, HorasModel> {

    public HorasRenderer(EntityRendererFactory.Context context) {
        super(context, new HorasModel(context.getPart(HUClientListener.HORAS)), 0.0F);
    }

    @Override
    public Identifier getTextureLocation(Horas entity) {
        return new Identifier(HeroesUnited.MODID, "textures/entity/horas" + (HUCalendarHelper.isSnowTime() ? "_new_year.png" : ".png"));
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.events.HURenderPlayerEvent;

public interface IHUPlayerRenderer {

    @OnlyIn(Dist.CLIENT)
    default void huRenderPlayerPre(HURenderPlayerEvent.Pre event) {

    }

    @OnlyIn(Dist.CLIENT)
    default void huRenderPlayerPost(HURenderPlayerEvent.Post event) {
    }
}

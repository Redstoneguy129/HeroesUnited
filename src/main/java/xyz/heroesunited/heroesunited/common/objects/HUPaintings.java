package xyz.heroesunited.heroesunited.common.objects;

import net.minecraft.world.entity.decoration.Motive;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUPaintings {

    public static final DeferredRegister<Motive> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, HeroesUnited.MODID);

    public static final Motive HORAS = register("horas", 32, 32);

    private static Motive register(String name, int width, int height) {
        Motive type = new Motive(width, height);
        PAINTINGS.register(name, () -> type);
        return type;
    }
}

package xyz.heroesunited.heroesunited.common.objects;

import net.minecraft.entity.item.PaintingType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUPaintings {

    public static final DeferredRegister<PaintingType> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, HeroesUnited.MODID);

    public static final PaintingType HORAS = register("horas", 32, 32);

    private static PaintingType register(String name, int width, int height) {
        PaintingType type = new PaintingType(width, height);
        PAINTINGS.register(name, () -> type);
        return type;
    }
}

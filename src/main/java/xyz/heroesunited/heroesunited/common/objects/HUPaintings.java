package xyz.heroesunited.heroesunited.common.objects;

import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUPaintings {

    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, HeroesUnited.MODID);

    public static final PaintingVariant HORAS = register("horas", 32, 32);

    private static PaintingVariant register(String name, int width, int height) {
        PaintingVariant type = new PaintingVariant(width, height);
        PAINTINGS.register(name, () -> type);
        return type;
    }
}

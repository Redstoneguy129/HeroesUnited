package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Condition;

import java.util.Random;
import java.util.function.Supplier;

public class CelestialBodies {

    public static final DeferredRegister<CelestialBody> CELESTIAL_BODIES = DeferredRegister.create(new ResourceLocation(HeroesUnited.MODID, "celestial_bodies"), HeroesUnited.MODID);
    public static final Supplier<IForgeRegistry<CelestialBody>> REGISTRY = CELESTIAL_BODIES.makeRegistry(RegistryBuilder::new);

    public static final Star SUN = register("sun", new Star(new Vec3(0, 0, 0), 12.5F));

    public static final Planet MERCURY = register("mercury", new Planet(null, new Vec3(25, 0, 0).yRot(new Random().nextInt(360)), 0.85F, 0.005F, SUN));
    public static final Planet VENUS = register("venus", new Planet(null, new Vec3(45, 0, 0).yRot(new Random().nextInt(360)), 1.1F, 0.004F, SUN));
    public static final Planet EARTH = register("earth", new Planet(Level.OVERWORLD, new Vec3(55, 0, 0).yRot(new Random().nextInt(360)), 1.1F, 0.003F, SUN));
    public static final Satellite MOON = register("moon", new Satellite(null, new Vec3(2, 0, 0).yRot(new Random().nextInt(360)), 0.05F, 0.001F, EARTH));
    public static final Planet MARS = register("mars", new Planet(HeroesUnited.MARS, new Vec3(75, 0, 0).yRot(new Random().nextInt(360)), 2, 0.001F, SUN));

    public static final CelestialBody ASTEROIDS_BELT = register("asteroid_belt", new CelestialBody(new Vec3(0, 0, 0)));
    public static final Planet JUPITER = register("jupiter", new Planet(null, new Vec3(125, 0, 0).yRot(new Random().nextInt(360)), 5, 0.0005F, SUN));
    public static final Planet SATURN = register("saturn", new Planet(null, new Vec3(150, 0, 0).yRot(new Random().nextInt(360)), 4.5F, 0.00025F, SUN));
    public static final Planet URANUS = register("uranus", new Planet(null, new Vec3(175, 0, 0).yRot(new Random().nextInt(360)), 3, 0.0001F, SUN));
    public static final Planet NEPTUNE = register("neptune", new Planet(null, new Vec3(200, 0, 0).yRot(new Random().nextInt(360)), 3, 0.00005F, SUN));
    public static final CelestialBody KUIPER_BELT = register("kuiper_belt", new CelestialBody(new Vec3(0, 0, 0)));

    private static <T extends CelestialBody> T register(String name, T celestialBody) {
        CELESTIAL_BODIES.register(name, () -> celestialBody);
        return celestialBody;
    }
}

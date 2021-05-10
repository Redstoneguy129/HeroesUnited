package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CelestialBodies {

    private static final HashMap<String, CelestialBody> CELESTIAL_BODIES = new HashMap<>();

    public static Star SUN = register("sun", new Star(new Vector3d(0,0,0),12.5F));

    public static Planet MERCURY = register("mercury", new Planet(null, new Vector3d(25,0,0),0.85F,0.005F, SUN));
    public static Planet VENUS = register("venus", new Planet(null, new Vector3d(45,0,0),1.1F,0.004F, SUN));
    public static Planet EARTH = register("earth", new Planet(World.OVERWORLD, new Vector3d(55,0,0),1.1F,0.003F, SUN));
    public static Satellite MOON = register("moon", new Satellite(null,new Vector3d(2,0,0),0.05F, 0.001F, EARTH));
    public static Planet MARS = register("mars", new Planet(HeroesUnited.MARS, new Vector3d(75,0,0),2,0.001F, SUN));

    public static CelestialBody ASTEROIDS_BELT = register("asteroid_belt", new CelestialBody(new Vector3d(0,0,0)));
    public static CelestialBody KUIPER_BELT = register("kuiper_belt", new CelestialBody(new Vector3d(0,0,0)));



    private static <T extends CelestialBody> T register(String name, T celestialBody) {
        CELESTIAL_BODIES.put( name, celestialBody);
        return celestialBody;
    }


    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<CelestialBody> e) {
        CELESTIAL_BODIES.forEach((name, celestialBody)->{
            celestialBody.setRegistryName(HeroesUnited.MODID, name);
            e.getRegistry().register(celestialBody);
        });
    }
}

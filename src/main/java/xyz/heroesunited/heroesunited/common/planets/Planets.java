package xyz.heroesunited.heroesunited.common.planets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;

import java.util.ArrayList;
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Planets {

    private static final HashMap<String, Planet> PLANETS = new HashMap<>();

    public static Planet EARTH = register("earth", new Planet(World.OVERWORLD, new Vector3d(500,0,0),25,new Vector3d(526,0,0)));

    private static Planet register(String name, Planet planet) {
        PLANETS.put( name, planet);
        return planet;
    }


    @SubscribeEvent
    public static void registerAbilityTypes(RegistryEvent.Register<Planet> e) {
        PLANETS.forEach((name, planet)->{
            planet.setRegistryName(HeroesUnited.MODID, name);
            e.getRegistry().register(planet);
        });
    }
}

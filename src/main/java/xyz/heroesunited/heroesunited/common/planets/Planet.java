package xyz.heroesunited.heroesunited.common.planets;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Planet extends ForgeRegistryEntry<Planet> {

    public static IForgeRegistry<Planet> PLANETS;

    private RegistryKey<Dimension> dimension;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        PLANETS = new RegistryBuilder<Planet>().setName(new ResourceLocation(HeroesUnited.MODID, "planets")).setType(Planet.class).setIDRange(0, Integer.MAX_VALUE).create();
    }
}

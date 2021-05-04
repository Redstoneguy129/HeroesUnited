package xyz.heroesunited.heroesunited.common.planets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

import java.awt.*;
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = HeroesUnited.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Planet extends ForgeRegistryEntry<Planet> {

    public static final HashMap<RegistryKey<World>, Planet> PLANETS_MAP = new HashMap<>();
    public static IForgeRegistry<Planet> PLANETS;

    private RegistryKey<World> dimension;

    private AxisAlignedBB hitbox;

    private Vector3d outCordinates;

    public Planet(RegistryKey<World> dimension, Vector3d coordinates, float scale, Vector3d outCordinates) {
        this.dimension = dimension;
        this.outCordinates = outCordinates;
        PLANETS_MAP.put(dimension,this);
        hitbox = new AxisAlignedBB(coordinates.x-scale/2,coordinates.y-scale/2,coordinates.z-scale/2,coordinates.x+scale/2,coordinates.y+scale/2,coordinates.z+scale/2);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        PLANETS = new RegistryBuilder<Planet>().setName(new ResourceLocation(HeroesUnited.MODID, "planets")).setType(Planet.class).setIDRange(0, Integer.MAX_VALUE).create();
    }

    public AxisAlignedBB getHitbox() {
        return hitbox;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public Vector3d getOutCordinates() {
        return outCordinates;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers){

        IVertexBuilder buffer = buffers.getBuffer(HUClientUtil.HURenderTypes.LASER);
        HUClientUtil.renderFilledBox(matrixStack, buffer, hitbox, Color.RED.getRed() / 255F, Color.RED.getGreen() / 255F, Color.RED.getBlue() / 255F, 1, Integer.MAX_VALUE);

    }
}

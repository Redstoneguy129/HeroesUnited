package xyz.heroesunited.heroesunited;

import net.arikia.dev.drpc.DiscordRPC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import xyz.heroesunited.heroesunited.client.HUClientEventHandler;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.client.SpaceDimensionRenderInfo;
import xyz.heroesunited.heroesunited.client.gui.AccessoriesScreen;
import xyz.heroesunited.heroesunited.client.render.model.CapeModel;
import xyz.heroesunited.heroesunited.client.render.model.HorasModel;
import xyz.heroesunited.heroesunited.client.render.model.ParachuteModel;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.client.render.model.space.EarthModel;
import xyz.heroesunited.heroesunited.client.render.model.space.SunModel;
import xyz.heroesunited.heroesunited.client.render.renderer.EnergyBlastRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.GeckoSuitRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.HorasRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.SpaceshipRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.space.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.space.CelestialBodies;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.mixin.client.AccessorDimensionRenderInfo;
import xyz.heroesunited.heroesunited.mixin.client.AccessorModelBakery;
import xyz.heroesunited.heroesunited.util.HURichPresence;
import xyz.heroesunited.heroesunited.util.compat.ObfuscateHandler;

import static xyz.heroesunited.heroesunited.HeroesUnited.*;

public class HUClientListener  implements ClientModInitializer {
    public static final EntityModelLayer HORAS = new EntityModelLayer(new Identifier(MODID, "horas"), "main");
    public static final EntityModelLayer CAPE = new EntityModelLayer(new Identifier(MODID, "cape"), "main");
    public static final EntityModelLayer PARACHUTE = new EntityModelLayer(new Identifier(MODID, "parachute"), "main");
    public static final EntityModelLayer SUIT = new EntityModelLayer(new Identifier(MODID, "suit"), "main");
    public static final EntityModelLayer SUIT_SLIM = new EntityModelLayer(new Identifier(MODID, "suit_slim"), "main");

    @Override
    public void onInitializeClient() {
        GeckoLib.initialize();

        MinecraftForge.EVENT_BUS.register(new HUClientEventHandler());

        EntityModelLayerRegistry.registerModelLayer(CAPE, CapeModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(PARACHUTE, ParachuteModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(HORAS, HorasModel::createLayerDefinition);
        EntityModelLayerRegistry.registerModelLayer(SUIT, () -> TexturedModelData.of(SuitModel.createMesh(Dilation.NONE, false), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(SUIT_SLIM, () -> TexturedModelData.of(SuitModel.createMesh(Dilation.NONE, true), 64, 64));

        EntityRendererRegistry.INSTANCE.register(HUEntities.HORAS, HorasRenderer::new);
        EntityRendererRegistry.INSTANCE.register(HUEntities.ENERGY_BLAST, EnergyBlastRenderer::new);
        EntityRendererRegistry.INSTANCE.register(HUEntities.SPACESHIP, SpaceshipRenderer::new);


        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
        HUPacks.HUPackFinder.createFoldersAndLoadThemes();
        GeckoSuitRenderer.registerArmorRenderer(SuitItem.class, new GeckoSuitRenderer());
        ScreenRegistry.register(HeroesUnited.ACCESSORIES_SCREEN_HANDLER, AccessoriesScreen::new);

        new HorasInfo.DimensionInfo("Overworld", "Default      Dimension", new Identifier("overworld"), new Identifier(MODID, "textures/gui/horas/dimensions/overworld.png"));
        new HorasInfo.DimensionInfo("Nether", "Default      Dimension", new Identifier("the_nether"), new Identifier(MODID, "textures/gui/horas/dimensions/the_nether.png"));
        new HorasInfo.DimensionInfo("End", "Default      Dimension", new Identifier("the_end"), new Identifier(MODID, "textures/gui/horas/dimensions/the_end.png"));

        if (!HURichPresence.isHiddenRPC()) {
            HURichPresence.getPresence().setDiscordRichPresence("In the Menus", null, HURichPresence.MiniLogos.NONE, null);
        }

        CelestialBodyRenderer.registerRenderer(new SunRenderer(), CelestialBodies.SUN);
        CelestialBodyRenderer.registerRenderer(new MercuryRenderer(), CelestialBodies.MERCURY);
        CelestialBodyRenderer.registerRenderer(new VenusRenderer(), CelestialBodies.VENUS);
        CelestialBodyRenderer.registerRenderer(new EarthRenderer(), CelestialBodies.EARTH);
        CelestialBodyRenderer.registerRenderer(new MoonRenderer(), CelestialBodies.MOON);
        CelestialBodyRenderer.registerRenderer(new MarsRenderer(), CelestialBodies.MARS);
        CelestialBodyRenderer.registerRenderer(new AsteroidsBeltRenderer(), CelestialBodies.ASTEROIDS_BELT);
        CelestialBodyRenderer.registerRenderer(new JupiterRenderer(), CelestialBodies.JUPITER);
        CelestialBodyRenderer.registerRenderer(new SaturnRenderer(), CelestialBodies.SATURN);
        CelestialBodyRenderer.registerRenderer(new UranusRenderer(), CelestialBodies.URANUS);
        CelestialBodyRenderer.registerRenderer(new NeptuneRenderer(), CelestialBodies.NEPTUNE);
        CelestialBodyRenderer.registerRenderer(new KuiperBeltRenderer(), CelestialBodies.KUIPER_BELT);
        AccessorModelBakery.getUnreferencedTex().add(SunModel.SUN_TEXTURE_MATERIAL);
        AccessorModelBakery.getUnreferencedTex().add(EarthModel.EARTH_TEXTURE_MATERIAL);
        AccessorDimensionRenderInfo.getEffects().put(new Identifier(MODID,"space"), new SpaceDimensionRenderInfo());


        LOGGER.info(MODID + ": client is ready!");
    }
}

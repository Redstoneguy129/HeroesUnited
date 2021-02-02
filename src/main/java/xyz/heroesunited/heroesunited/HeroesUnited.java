package xyz.heroesunited.heroesunited;

import net.arikia.dev.drpc.DiscordRPC;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import xyz.heroesunited.heroesunited.client.HUClientEventHandler;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.client.gui.AccessoriesScreen;
import xyz.heroesunited.heroesunited.client.render.renderer.GeckoSuitRenderer;
import xyz.heroesunited.heroesunited.client.render.renderer.IGeoAbility;
import xyz.heroesunited.heroesunited.client.render.renderer.RendererHoras;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.HUEventHandler;
import xyz.heroesunited.heroesunited.common.abilities.suit.GeckoSuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerEvent;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerStorage;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUPaintings;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.container.HUContainers;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.util.HURichPresence;

@Mod(HeroesUnited.MODID)
public class HeroesUnited {

    public static final String MODID = "heroesunited";
    public static final Logger LOGGER = LogManager.getLogger();

    public HeroesUnited() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            GeckoLib.initialize();
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(new HUClientEventHandler());
        });

        HUPacks.init();

        HUAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
        HUSounds.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        HUEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        HUBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        HUItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        HUPaintings.PAINTINGS.register(FMLJavaModLoadingContext.get().getModEventBus());
        HUContainers.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(new HUEventHandler());
        MinecraftForge.EVENT_BUS.register(new HUPlayerEvent());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HUConfig.CLIENT_SPEC);
    }

    static {
        AnimationController.addModelFetcher((IAnimatable object) -> {
            if (object instanceof IHUPlayer) {
                return ((IHUPlayer) object).getAnimatedModel();
            }
            return null;
        });
        AnimationController.addModelFetcher((IAnimatable object) -> {
            if (object instanceof IGeoAbility) {
                return ((IGeoAbility) object).getGeoRenderer().getGeoModelProvider();
            }
            return null;
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IHUPlayer.class, new HUPlayerStorage(), () -> new HUPlayer(null));
        HUNetworking.registerMessages();
        event.enqueueWork(HUAttributes::registerAttributes);
        LOGGER.info(MODID + ": common is ready!");
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
        HUPacks.HUPackFinder.createFoldersAndLoadThemes();
        ClientRegistry.registerEntityShader(Horas.class, new ResourceLocation(MODID, "shaders/post/horas.json"));
        RenderingRegistry.registerEntityRenderingHandler(HUEntities.HORAS, RendererHoras::new);
        GeoArmorRenderer.registerArmorRenderer(GeckoSuitItem.class, new GeckoSuitRenderer());
        ScreenManager.registerFactory(HUContainers.ACCESSORIES, AccessoriesScreen::new);
        new HorasInfo.DimensionInfo("Overworld", "Default      Dimension", new ResourceLocation("overworld"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/overworld.png"));
        new HorasInfo.DimensionInfo("Nether", "Default      Dimension", new ResourceLocation("the_nether"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_nether.png"));
        new HorasInfo.DimensionInfo("End", "Default      Dimension", new ResourceLocation("the_end"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_end.png"));
        HURichPresence.getPresence().setDiscordRichPresence("In the Menus", null, HURichPresence.MiniLogos.NONE, null);
        LOGGER.info(MODID + ": client is ready!");
    }
}
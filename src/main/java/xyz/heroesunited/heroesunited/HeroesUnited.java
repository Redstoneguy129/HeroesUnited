package xyz.heroesunited.heroesunited;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;
import xyz.heroesunited.heroesunited.client.HUClientEventHandler;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.client.gui.AccessoireScreen;
import xyz.heroesunited.heroesunited.client.render.renderer.RendererHoras;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.HUEventHandler;
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

import java.util.Objects;

@Mod(HeroesUnited.MODID)
public class HeroesUnited {

    public static final String MODID = "heroesunited";
    public static final Logger LOGGER = LogManager.getLogger();

    public HeroesUnited() {
        this.registerObjects(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            GeckoLib.initialize();
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(new HUClientEventHandler());
        });
        GeckoLibMod.DISABLE_IN_DEV = true;
        HUPacks.init();
        MinecraftForge.EVENT_BUS.register(new HUEventHandler());
        MinecraftForge.EVENT_BUS.register(new HUPlayerEvent());
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HUConfig.CLIENT_SPEC);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    private void registerObjects(IEventBus eventBus) {
        HUAttributes.ATTRIBUTES.register(eventBus);
        HUSounds.SOUNDS.register(eventBus);
        HUEntities.ENTITIES.register(eventBus);
        HUBlocks.BLOCKS.register(eventBus);
        HUItems.ITEMS.register(eventBus);
        HUPaintings.PAINTINGS.register(eventBus);
        HUContainers.CONTAINERS.register(eventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IHUPlayer.class, new HUPlayerStorage(), () -> new HUPlayer(null));
        HUNetworking.registerMessages();
        event.enqueueWork(HUAttributes::registerAttributes);
        event.enqueueWork(() -> GlobalEntityTypeAttributes.put(HUEntities.HORAS, Horas.func_234225_eI_().create()));
        LOGGER.info(MODID+": common is ready!");
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        HURichPresence.getPresence().setDiscordRichPresence("In the Menus", null, HURichPresence.MiniLogos.NONE, null);
        RenderingRegistry.registerEntityRenderingHandler(HUEntities.HORAS, RendererHoras::new);
        ClientRegistry.registerEntityShader(Horas.class, new ResourceLocation(MODID, "shaders/post/horas.json"));
        new HorasInfo.DimensionInfo("Overworld", "Default      Dimension", new ResourceLocation("overworld"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/overworld.png"));
        new HorasInfo.DimensionInfo("Nether", "Default      Dimension", new ResourceLocation("the_nether"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_nether.png"));
        new HorasInfo.DimensionInfo("End", "Default      Dimension", new ResourceLocation("the_end"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_end.png"));
        Runtime.getRuntime().addShutdownHook(new HURichPresence.CloseRichPresence());
        HUPacks.HUPackFinder.createFoldersAndLoadThemes();
        ScreenManager.registerFactory(HUContainers.ACCESSOIRE, AccessoireScreen::new);
        LOGGER.info(MODID+": client is ready!");
    }

    @SubscribeEvent
    public void biomeLoading(BiomeLoadingEvent event) {
        RegistryKey<Biome> biome = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(event.getName()));
        if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OVERWORLD)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                    Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                            HUBlocks.TITANIUM_ORE.getDefaultState(), 4)).range(32).square().func_242731_b(2));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void runSecurity(EntityJoinWorldEvent event) {
        if(!event.getEntity().world.isRemote || !(event.getEntity() instanceof PlayerEntity) || Minecraft.getInstance().player == null) return;
        if(Minecraft.getInstance().player.getUniqueID() == event.getEntity().getUniqueID()) {
            HURichPresence.getPresence().setDiscordRichPresence("Playing Heroes United", null, HURichPresence.MiniLogos.NONE, null);
        }
    }
}
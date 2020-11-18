package xyz.heroesunited.heroesunited;

import net.minecraft.client.Minecraft;
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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.heroesunited.heroesunited.client.HUClientEventHandler;
import xyz.heroesunited.heroesunited.client.HorasInfo;
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
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.security.SecurityHelper;
import xyz.heroesunited.heroesunited.util.HUClientUtil;
import xyz.heroesunited.heroesunited.util.HURichPresence;

import java.util.Objects;

@Mod(HeroesUnited.MODID)
public class HeroesUnited {

    public static final String MODID = "heroesunited";
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean hasALPHA = false;
    public static boolean getHasAlpha() {
        return hasALPHA;
    }
    public static Logger getLogger() { return LOGGER; }

    public HeroesUnited() {
        this.registerObjects(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(new HUClientEventHandler());
        });
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
        if(FMLEnvironment.production) {
            ModList.get().getMods().forEach(modInfo -> {
                if(!hasALPHA) {
                    if(modInfo.getModId().equals("huben10") || modInfo.getModId().equals("hugeneratorrex") || modInfo.getModId().equals("hudannyphantom")) {
                        if(modInfo.getVersion().toString().split("\\.").length >= 3) {
                            if(Integer.parseInt(modInfo.getVersion().toString().split("\\.")[2]) > 0) {
                                LOGGER.warn("Found an Alpha!");
                                hasALPHA = true;
                            }
                        }
                    }
                }
            });
        } else {
            LOGGER.error("Development Environment");
        }
        RenderingRegistry.registerEntityRenderingHandler(HUEntities.HORAS, RendererHoras::new);
        ClientRegistry.registerEntityShader(Horas.class, new ResourceLocation(MODID, "shaders/post/horas.json"));
        new HorasInfo.DimensionInfo("Overworld", "Default      Dimension", new ResourceLocation("overworld"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/overworld.png"));
        new HorasInfo.DimensionInfo("Nether", "Default      Dimension", new ResourceLocation("the_nether"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_nether.png"));
        new HorasInfo.DimensionInfo("End", "Default      Dimension", new ResourceLocation("the_end"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_end.png"));
        Runtime.getRuntime().addShutdownHook(new HURichPresence.CloseRichPresence());
        HUClientUtil.createFoldersAndLoadThemes();
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

    /*
    This Basically adds a Donation check to see if player is a donor or not.
    This only runs if an Alpha dependant is being used.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void runSecurity(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof PlayerEntity) {
            assert Minecraft.getInstance().player != null;
            if (Minecraft.getInstance().player.getUniqueID() != event.getEntity().getUniqueID()) return;
            HURichPresence.getPresence().setDiscordRichPresence("Playing Heroes United", null, HURichPresence.MiniLogos.NONE, null);
            if (!HeroesUnited.getHasAlpha()) return;
            String UUID = Minecraft.getInstance().player.getUniqueID().toString().replace("-", "");
            SecurityHelper securityHelper = new SecurityHelper();
            if (securityHelper.shouldContinue(UUID)) return;
            Minecraft.getInstance().shutdown();
        }
    }
}
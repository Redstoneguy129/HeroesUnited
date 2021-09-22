package xyz.heroesunited.heroesunited;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import xyz.heroesunited.heroesunited.client.HUClientEventHandler;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.client.SpaceDimensionRenderInfo;
import xyz.heroesunited.heroesunited.client.gui.AccessoriesScreen;
import xyz.heroesunited.heroesunited.client.render.HULayerRenderer;
import xyz.heroesunited.heroesunited.client.render.model.CapeModel;
import xyz.heroesunited.heroesunited.client.render.model.HorasModel;
import xyz.heroesunited.heroesunited.client.render.model.ParachuteModel;
import xyz.heroesunited.heroesunited.client.render.model.SuitModel;
import xyz.heroesunited.heroesunited.client.render.model.space.*;
import xyz.heroesunited.heroesunited.client.render.renderer.*;
import xyz.heroesunited.heroesunited.client.render.renderer.space.*;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.HUEventHandler;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Condition;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerEvent;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.ability.IHUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.IHUDataCap;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUPaintings;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.container.HUContainers;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.common.space.CelestialBodies;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.common.structures.HUConfiguredStructures;
import xyz.heroesunited.heroesunited.common.structures.HUStructures;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.mixin.client.AccessorDimensionRenderInfo;
import xyz.heroesunited.heroesunited.util.HUModelLayers;
import xyz.heroesunited.heroesunited.util.HURichPresence;
import xyz.heroesunited.heroesunited.util.compat.ObfuscateHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.FALL_RESISTANCE;
import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

@Mod(HeroesUnited.MODID)
public class HeroesUnited {

    public static final String MODID = "heroesunited";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceKey<Level> SPACE = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(HeroesUnited.MODID,"space"));
    public static final ResourceKey<Level> MARS = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(HeroesUnited.MODID,"mars"));

    public HeroesUnited() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);
        GeckoLib.initialize();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(new HUClientEventHandler());
        });

        HUPacks.init();

        HUAttributes.ATTRIBUTES.register(bus);
        HUSounds.SOUNDS.register(bus);
        HUEntities.ENTITIES.register(bus);
        HUBlocks.BLOCKS.register(bus);
        HUItems.ITEMS.register(bus);
        HUPaintings.PAINTINGS.register(bus);
        HUContainers.CONTAINERS.register(bus);
        AbilityType.ABILITY_TYPES.register(bus);
        Condition.CONDITIONS.register(bus);
        HUStructures.STRUCTURES.register(bus);

        MinecraftForge.EVENT_BUS.register(new HUEventHandler());
        MinecraftForge.EVENT_BUS.register(new HUPlayerEvent());
        bus.addListener(this::onRegisterNewRegistries);
        MinecraftForge.EVENT_BUS.addListener(this::addDimensionalSpacing);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HUConfig.CLIENT_SPEC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> AccessorDimensionRenderInfo.getEffects().put(new ResourceLocation(MODID,"space"), new SpaceDimensionRenderInfo()));
    }

    static {
        AnimationController.addModelFetcher((IAnimatable o) -> {
            if (o instanceof IHUPlayer) {
                return (IAnimatableModel<Object>) ((IHUPlayer) o).getAnimatedModel();
            }
            return null;
        });
        AnimationController.addModelFetcher((IAnimatable o) -> {
            if (o instanceof IGeoAbility) {
                return (IAnimatableModel<Object>) ((IGeoAbility) o).getGeoModel();
            }
            return null;
        });
    }

    /**
     * Will go into the world's chunkgenerator and manually add our structure spacing.
     * If the spacing is not added, the structure doesn't spawn.
     *
     * Use this for dimension blacklists for your structure.
     * (Don't forget to attempt to remove your structure too from the map if you are blacklisting that dimension!)
     * (It might have your structure in it already.)
     *
     * Basically use this to make absolutely sure the chunkgenerator can or cannot spawn your structure.
     */
    private static Method GETCODEC_METHOD;
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerLevel){
            ServerLevel serverWorld = (ServerLevel)event.getWorld();

            /*
             * Skip Terraforged's chunk generator as they are a special case of a mod locking down their chunkgenerator.
             * They will handle your structure spacing for your if you add to WorldGenRegistries.NOISE_GENERATOR_SETTINGS in your structure's registration.
             * This here is done with reflection as this tutorial is not about setting up and using Mixins.
             * If you are using mixins, you can call the codec method with an invoker mixin instead of using reflection.
             */
            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                HeroesUnited.LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            /*
             * Prevent spawning our structure in Vanilla's superflat world as
             * people seem to want their superflat worlds free of modded structures.
             * Also that vanilla superflat is really tricky and buggy to work with in my experience.
             */
            if(serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource &&
                    serverWorld.dimension().equals(Level.OVERWORLD)){
                return;
            }

            /*
             * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
             * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
             *
             * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as WorldGenRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
             * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
             * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
             */
            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(HUStructures.CITY.get(), StructureSettings.DEFAULTS.get(HUStructures.CITY.get()));
            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
        }
    }

    public void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        CelestialBody.CELESTIAL_BODIES = new RegistryBuilder<CelestialBody>().setName(new ResourceLocation(HeroesUnited.MODID, "celestial_bodies")).setType(CelestialBody.class).setIDRange(0, Integer.MAX_VALUE).create();
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            HUStructures.setupStructures();
            HUConfiguredStructures.registerConfiguredStructures();
        });
        HUNetworking.registerMessages();
        LOGGER.info(MODID + ": common is ready!");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void textureStitchPre(TextureStitchEvent.Pre e) {
        e.addSprite(SunModel.SUN_TEXTURE_MATERIAL.texture());
        e.addSprite(EarthModel.EARTH_TEXTURE_MATERIAL.texture());
    }

    @SubscribeEvent
    public void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IHUPlayer.class);
        event.register(IHUDataCap.class);
        event.register(IHUAbilityCap.class);
        LOGGER.info(MODID + ": capabilities is registered!");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HUModelLayers.CAPE, CapeModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.PARACHUTE, ParachuteModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.HORAS, HorasModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.SUIT, () -> LayerDefinition.create(SuitModel.createMesh(CubeDeformation.NONE, false), 64, 64));
        event.registerLayerDefinition(HUModelLayers.SUIT_SLIM, () -> LayerDefinition.create(SuitModel.createMesh(CubeDeformation.NONE, true), 64, 64));

        event.registerLayerDefinition(HUModelLayers.ASTEROIDS_BELT, AsteroidsBeltRenderer::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.EARTH, EarthModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.PLANET, PlanetModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.KUIPER, KuiperBeltRenderer::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.MOON, MoonModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.SATURN, SaturnModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.SUN, SunModel::createLayerDefinition);
        event.registerLayerDefinition(HUModelLayers.VENUS, VenusModel::createLayerDefinition);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HUEntities.HORAS, HorasRenderer::new);
        event.registerEntityRenderer(HUEntities.ENERGY_BLAST, EnergyBlastRenderer::new);
        event.registerEntityRenderer(HUEntities.SPACESHIP, SpaceshipRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerLayers(final EntityRenderersEvent.AddLayers event) {
        GeckoSuitRenderer.registerArmorRenderer(SuitItem.class, new GeckoSuitRenderer());

        CelestialBodyRenderer.registerRenderer(new SunRenderer(event.getEntityModels().bakeLayer(HUModelLayers.SUN)), CelestialBodies.SUN);
        CelestialBodyRenderer.registerRenderer(new MercuryRenderer(event.getEntityModels().bakeLayer(HUModelLayers.PLANET)), CelestialBodies.MERCURY);
        CelestialBodyRenderer.registerRenderer(new VenusRenderer(event.getEntityModels().bakeLayer(HUModelLayers.VENUS)), CelestialBodies.VENUS);
        CelestialBodyRenderer.registerRenderer(new EarthRenderer(event.getEntityModels().bakeLayer(HUModelLayers.EARTH)), CelestialBodies.EARTH);
        CelestialBodyRenderer.registerRenderer(new MoonRenderer(event.getEntityModels().bakeLayer(HUModelLayers.MOON)), CelestialBodies.MOON);
        CelestialBodyRenderer.registerRenderer(new MarsRenderer(event.getEntityModels().bakeLayer(HUModelLayers.PLANET)), CelestialBodies.MARS);
        CelestialBodyRenderer.registerRenderer(new AsteroidsBeltRenderer(event.getEntityModels().bakeLayer(HUModelLayers.ASTEROIDS_BELT)), CelestialBodies.ASTEROIDS_BELT);
        CelestialBodyRenderer.registerRenderer(new JupiterRenderer(event.getEntityModels().bakeLayer(HUModelLayers.PLANET)), CelestialBodies.JUPITER);
        CelestialBodyRenderer.registerRenderer(new SaturnRenderer(event.getEntityModels().bakeLayer(HUModelLayers.SATURN)), CelestialBodies.SATURN);
        CelestialBodyRenderer.registerRenderer(new UranusRenderer(event.getEntityModels().bakeLayer(HUModelLayers.PLANET)), CelestialBodies.URANUS);
        CelestialBodyRenderer.registerRenderer(new NeptuneRenderer(event.getEntityModels().bakeLayer(HUModelLayers.PLANET)), CelestialBodies.NEPTUNE);
        CelestialBodyRenderer.registerRenderer(new KuiperBeltRenderer(event.getEntityModels().bakeLayer(HUModelLayers.KUIPER)), CelestialBodies.KUIPER_BELT);

        for (EntityType<? extends LivingEntity> type : ForgeRegistries.ENTITIES.getValues().stream().filter(DefaultAttributes::hasSupplier).map(entityType -> (EntityType<? extends LivingEntity>) entityType).collect(Collectors.toList())) {
            if (Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(type) instanceof LivingEntityRenderer) {
                LivingEntityRenderer entityRenderer = event.getRenderer(type);
                if (entityRenderer != null && entityRenderer.getModel() instanceof HumanoidModel) {
                    entityRenderer.addLayer(new HULayerRenderer(entityRenderer, event.getEntityModels()));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        HUPacks.HUPackFinder.createFoldersAndLoadThemes();
        MenuScreens.register(HUContainers.ACCESSORIES, AccessoriesScreen::new);

        new HorasInfo.DimensionInfo("Overworld", "Default      Dimension", new ResourceLocation("overworld"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/overworld.png"));
        new HorasInfo.DimensionInfo("Nether", "Default      Dimension", new ResourceLocation("the_nether"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_nether.png"));
        new HorasInfo.DimensionInfo("End", "Default      Dimension", new ResourceLocation("the_end"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_end.png"));


        if (ModList.get().isLoaded("obfuscate")) {
            MinecraftForge.EVENT_BUS.register(new ObfuscateHandler());
        }

        HURichPresence.getPresence().setDiscordRichPresence("In the Menus", null, HURichPresence.MiniLogos.NONE, null);

        LOGGER.info(MODID + ": client is ready!");
    }

    @SubscribeEvent
    public void entityAttribute(final EntityAttributeCreationEvent event) {
        event.put(HUEntities.HORAS, Horas.createMobAttributes().build());
    }

    @SubscribeEvent
    public void entityAttributeModification(final EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            if (type == EntityType.PLAYER) {
                if (!event.has(type, FALL_RESISTANCE)) {
                    event.add(type, FALL_RESISTANCE, 0);
                }
                if (!event.has(type, JUMP_BOOST)) {
                    event.add(type, JUMP_BOOST, 0);
                }
            }
        }
    }

    public static final CreativeModeTab ACCESSORIES = new CreativeModeTab(CreativeModeTab.TABS.length, "accessories") {

        @Override
        public ItemStack makeIcon() {
            return HUItems.BOBO_ACCESSORY.getDefaultInstance();
        }
    };
}
package xyz.heroesunited.heroesunited;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.GeckoLib;
import xyz.heroesunited.heroesunited.client.AbilityOverlay;
import xyz.heroesunited.heroesunited.client.ClientEventHandler;
import xyz.heroesunited.heroesunited.client.HorasInfo;
import xyz.heroesunited.heroesunited.client.SpaceDimensionRenderInfo;
import xyz.heroesunited.heroesunited.client.gui.AccessoriesScreen;
import xyz.heroesunited.heroesunited.client.model.CapeModel;
import xyz.heroesunited.heroesunited.client.model.HorasModel;
import xyz.heroesunited.heroesunited.client.model.ParachuteModel;
import xyz.heroesunited.heroesunited.client.model.SuitModel;
import xyz.heroesunited.heroesunited.client.model.space.*;
import xyz.heroesunited.heroesunited.client.renderer.EnergyBlastRenderer;
import xyz.heroesunited.heroesunited.client.renderer.HorasRenderer;
import xyz.heroesunited.heroesunited.client.renderer.SpaceshipRenderer;
import xyz.heroesunited.heroesunited.client.renderer.space.*;
import xyz.heroesunited.heroesunited.common.EventHandler;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.Condition;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
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
import xyz.heroesunited.heroesunited.common.objects.entities.HorasEntity;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.common.space.CelestialBodies;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.util.HUModelLayers;
import xyz.heroesunited.heroesunited.util.HURichPresence;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.FALL_RESISTANCE;
import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

/**
 * TODO change mars.json file, because rn dimension looks like a mesa biome
 */
@Mod(HeroesUnited.MODID)
public class HeroesUnited {

    public static final ResourceKey<Level> SPACE = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(HeroesUnited.MODID, "space"));
    public static final ResourceKey<Level> MARS = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(HeroesUnited.MODID, "mars"));
    public static final String MODID = "heroesunited";
    public static final Logger LOGGER = LogManager.getLogger();

    public HeroesUnited() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(this);
        GeckoLib.initialize();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
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
        CelestialBodies.CELESTIAL_BODIES.register(bus);
        //HUStructures.STRUCTURES.register(bus);

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new HUPlayerEvent());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HUConfig.CLIENT_SPEC);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerKeyBinds(RegisterDimensionSpecialEffectsEvent e) {
        e.register(new ResourceLocation(MODID, "space"), new SpaceDimensionRenderInfo());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerKeyBinds(RegisterKeyMappingsEvent e) {
        e.register(ClientEventHandler.ABILITIES_SCREEN);
        e.register(ClientEventHandler.ACCESSORIES_SCREEN);
        for (int i = 1; i < 6; i++) {
            int key = switch (i) {
                case 1 -> GLFW.GLFW_KEY_Z;
                case 2 -> GLFW.GLFW_KEY_R;
                case 3 -> GLFW.GLFW_KEY_G;
                case 4 -> GLFW.GLFW_KEY_V;
                default -> GLFW.GLFW_KEY_B;
            };
            ClientEventHandler.AbilityKeyBinding keyBinding = new ClientEventHandler.AbilityKeyBinding(HeroesUnited.MODID + ".key.ability_" + i, key, i);
            e.register(keyBinding);
            ClientEventHandler.ABILITY_KEYS.add(keyBinding);
        }
    }

    /**
     * Now using an atlases/ folder
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void textureStitchPre(TextureStitchEvent e) {
        //e.addSprite(SunModel.SUN_TEXTURE_MATERIAL.texture());
        //e.addSprite(EarthModel.EARTH_TEXTURE_MATERIAL.texture());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerClientReloadListeners(RegisterClientReloadListenersEvent e) {
        e.registerReloadListener(new HUPackLayers());
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
    public void registerGuiOverlay(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "ability_overlay", new AbilityOverlay());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HUEntities.HORAS.get(), HorasRenderer::new);
        event.registerEntityRenderer(HUEntities.ENERGY_BLAST.get(), EnergyBlastRenderer::new);
        event.registerEntityRenderer(HUEntities.SPACESHIP.get(), SpaceshipRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerLayers(final EntityRenderersEvent.AddLayers event) {
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
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        Runtime.getRuntime().addShutdownHook(new Thread(HURichPresence::close));
        HUPacks.createFoldersAndLoadThemes();
        MenuScreens.register(HUContainers.ACCESSORIES.get(), AccessoriesScreen::new);

        new HorasInfo.DimensionInfo("Overworld", "Default      Dimension", new ResourceLocation("overworld"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/overworld.png"));
        new HorasInfo.DimensionInfo("Nether", "Default      Dimension", new ResourceLocation("the_nether"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_nether.png"));
        new HorasInfo.DimensionInfo("End", "Default      Dimension", new ResourceLocation("the_end"), new ResourceLocation(MODID, "textures/gui/horas/dimensions/the_end.png"));

        HURichPresence.getPresence().setDiscordRichPresence("In the Menus");

        LOGGER.info(MODID + ": client is ready!");
    }

    @SubscribeEvent
    public void addPackFinders(final AddPackFindersEvent event) {
        event.addRepositorySource(new HUPacks.HUPackFinder());
    }

    @SubscribeEvent
    public void registerSuitItems(final RegisterEvent event) {
        event.register(ForgeRegistries.ITEMS.getRegistryKey(), helper -> {
            for (Suit suit : Suit.SUITS.values()) {
                for (Pair<ResourceLocation, SuitItem> pair : suit.createItems().values()) {
                    helper.register(pair.getA(), pair.getB());
                }
            }
        });

    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        /*event.enqueueWork(() -> {
            HUStructures.setupStructures();
            HUConfiguredStructures.registerConfiguredStructures();
        });*/
        HUNetworking.registerMessages();
        LOGGER.info(MODID + ": common is ready!");
    }

    @SubscribeEvent
    public void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IHUPlayer.class);
        event.register(IHUDataCap.class);
        event.register(IHUAbilityCap.class);
        LOGGER.info(MODID + ": capabilities is registered!");
    }

    @SubscribeEvent
    public void entityAttribute(final EntityAttributeCreationEvent event) {
        event.put(HUEntities.HORAS.get(), HorasEntity.createMobAttributes().build());
    }

    @SubscribeEvent
    public void entityAttributeModification(final EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            if (type == EntityType.PLAYER) {
                if (!event.has(type, FALL_RESISTANCE)) {
                    event.add(type, FALL_RESISTANCE);
                }
                if (!event.has(type, JUMP_BOOST)) {
                    event.add(type, JUMP_BOOST);
                }
            }
        }
    }

    public static CreativeModeTab ACCESSORIES;

    @SubscribeEvent
    public void registerTabs(CreativeModeTabEvent.Register event) {
        ACCESSORIES = event.registerCreativeModeTab(new ResourceLocation(MODID, "accessories"), builder ->
                builder.icon(() -> HUItems.BOBO_ACCESSORY.get().getDefaultInstance())
                        .title(Component.translatable("tabs.heroesunited.accessories"))
                .displayItems((featureFlags, tab, hasOp) -> {
                    tab.accept(HUItems.AKIRA_JACKET.get());
                    tab.accept(HUItems.ALLEN_WALKER_JACKET.get());
                    tab.accept(HUItems.ALLEN_WALKER_PANTS.get());
                    tab.accept(HUItems.ALLEN_WALKER_SHOES.get());
                    tab.accept(HUItems.ARC_REACTOR_ACCESSORY.get());
                    tab.accept(HUItems.BIG_CHILL_CLOAK.get());
                    tab.accept(HUItems.BOBO_ACCESSORY.get());
                    tab.accept(HUItems.BOOSTED_GEAR.get());
                    tab.accept(HUItems.CAPTAIN_REX.get());
                    tab.accept(HUItems.CAP_SHIELD_ACCESSORY.get());
                    tab.accept(HUItems.CLOWN_HAT.get());
                    tab.accept(HUItems.DARK_TROOPER_HELMET.get());
                    tab.accept(HUItems.DOOM_HELMET.get());
                    tab.accept(HUItems.EMILIA_CAPE.get());
                    tab.accept(HUItems.FINN_ARM.get());
                    tab.accept(HUItems.FLASH_RING.get());
                    tab.accept(HUItems.FOTONIC_TRIDENT.get());
                    tab.accept(HUItems.GREEN_GOGGLES.get());
                    tab.accept(HUItems.GREEN_SHIRT.get());
                    tab.accept(HUItems.HEADBAND.get());
                    tab.accept(HUItems.HOKAGE_CAPE.get());
                    tab.accept(HUItems.JANGO_FETT_HELMET.get());
                    tab.accept(HUItems.JASON_MASK.get());
                    tab.accept(HUItems.JASON_JACKET.get());
                    tab.accept(HUItems.JASON_PANTS.get());
                    tab.accept(HUItems.JASON_SHIRT.get());
                    tab.accept(HUItems.KEYBLADE.get());
                    tab.accept(HUItems.KEY_VECTOR_SIGMA.get());
                    tab.accept(HUItems.MACHETE.get());
                    tab.accept(HUItems.MADNESSCLAW.get());
                    tab.accept(HUItems.MADNESSCOMBAT.get());
                    tab.accept(HUItems.MATRIX_OF_LEADERSHIP.get());
                    tab.accept(HUItems.MINING_PICK.get());
                    tab.accept(HUItems.NITRO_JETPACK.get());
                    tab.accept(HUItems.PERRY_TAIL.get());
                    tab.accept(HUItems.PERRY_THE_PLATYPUS_HAT.get());
                    tab.accept(HUItems.PETER_PARKER_SHIRT.get());
                    tab.accept(HUItems.RED_JACKET.get());
                    tab.accept(HUItems.REDA_JACKET.get());
                    tab.accept(HUItems.REDA_SHIRT.get());
                    tab.accept(HUItems.SMALLGILLY.get());
                    tab.accept(HUItems.SONIC_SHOES.get());
                    tab.accept(HUItems.STRANGE_MASK.get());
                    tab.accept(HUItems.SWORD_OF_THE_STORM.get());
                    tab.accept(HUItems.THE_ONE_RING_ACCESSORY.get());
                    tab.accept(HUItems.WALLE_HEAD.get());
                    tab.accept(HUItems.ZEK_GLASSES.get());
                })
        );
    }

    @SubscribeEvent
    public void addItemsToTabs(CreativeModeTabEvent.BuildContents event) {
        for (Suit suit : Suit.SUITS.values().stream().filter(suit -> suit.getItemGroup().equals(event.getTab())).toList()) {
            for (Pair<ResourceLocation, SuitItem> suitItem : suit.getSuitItems()) {
                event.accept(suitItem.getB());
            }
        }

        if (event.getTab().equals(CreativeModeTabs.SPAWN_EGGS)) {
            event.getEntries().putAfter(Items.EMERALD.getDefaultInstance(), HUItems.HORAS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            if (HUItems.COMIC_ITEM != null) {
                event.getEntries().putAfter(HUItems.HORAS.get().getDefaultInstance(), HUItems.COMIC_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
        if (event.getTab().equals(CreativeModeTabs.INGREDIENTS)) {
            event.getEntries().putAfter(Items.NETHERITE_INGOT.getDefaultInstance(), HUItems.TITANIUM_INGOT.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (event.getTab().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
            event.getEntries().putAfter(Items.DIAMOND_BLOCK.getDefaultInstance(), HUBlocks.TITANIUM.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.getEntries().putAfter(Items.DIAMOND_ORE.getDefaultInstance(), HUBlocks.TITANIUM_ORE.get().asItem().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
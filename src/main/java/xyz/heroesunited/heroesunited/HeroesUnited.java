package xyz.heroesunited.heroesunited;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import xyz.heroesunited.heroesunited.client.render.renderer.IGeoAbility;
import xyz.heroesunited.heroesunited.common.HUConfig;
import xyz.heroesunited.heroesunited.common.HUEventHandler;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerEvent;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;
import xyz.heroesunited.heroesunited.common.capabilities.ability.IHUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.IHUDataCap;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.container.AccessoriesContainer;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;
import xyz.heroesunited.heroesunited.common.objects.entities.Horas;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.FALL_RESISTANCE;
import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

public class HeroesUnited implements ModInitializer {

    public static final String MODID = "heroesunited";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final RegistryKey<World> SPACE = RegistryKey.of(Registry.WORLD_KEY, new Identifier(HeroesUnited.MODID,"space"));
    public static final RegistryKey<World> MARS = RegistryKey.of(Registry.WORLD_KEY, new Identifier(HeroesUnited.MODID,"mars"));

    @Override
    public void onInitialize() {

        HUPacks.init();

        HUAttributes.ATTRIBUTES.forEach((id, attribute) -> Registry.register(Registry.ATTRIBUTE, new Identifier(HeroesUnited.MODID, id), attribute));
        HUEntities.ENTITIES.forEach((id, entity) -> Registry.register(Registry.ENTITY_TYPE, new Identifier(HeroesUnited.MODID, id), entity));
        HUBlocks.BLOCKS.forEach((id, block) -> Registry.register(Registry.BLOCK, new Identifier(HeroesUnited.MODID, id), block));
        HUItems.ITEMS.forEach((id, item) -> Registry.register(Registry.ITEM, new Identifier(HeroesUnited.MODID, id), item));

        FabricDefaultAttributeRegistry.register(HUEntities.HORAS, Horas.createMobAttributes());

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> HUCoreCommand.register(dispatcher));

        MinecraftForge.EVENT_BUS.register(new HUEventHandler());
        MinecraftForge.EVENT_BUS.register(new HUPlayerEvent());
        bus.addListener(this::onRegisterNewRegistries);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HUConfig.CLIENT_SPEC);
    }

    public static final ScreenHandlerType<AccessoriesContainer> ACCESSORIES_SCREEN_HANDLER;
    public static final PaintingMotive HORAS_PAINTING;
    public static final SoundEvent FLYING;

    static {
        HORAS_PAINTING = Registry.register(Registry.PAINTING_MOTIVE, new Identifier(HeroesUnited.MODID, "horas"), new PaintingMotive(32, 32));
        ACCESSORIES_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(HeroesUnited.MODID, "accessories"), AccessoriesContainer::new);
        FLYING = Registry.register(Registry.SOUND_EVENT, new Identifier(HeroesUnited.MODID, "flying"), new SoundEvent(new Identifier(HeroesUnited.MODID, "flying")));
        AnimationController.addModelFetcher((IAnimatable o) -> {
            if (o instanceof IHUPlayer) {
                return ((IHUPlayer) o).getAnimatedModel();
            }
            return null;
        });
        AnimationController.addModelFetcher((IAnimatable o) -> {
            if (o instanceof IGeoAbility) {
                IAnimatableModel model = ((IGeoAbility) o).getGeoModel();
                return model;
            }
            return null;
        });
    }

    public void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        AbilityType.ABILITIES = new RegistryBuilder<AbilityType>().setName(new Identifier(HeroesUnited.MODID, "ability_types")).setType(AbilityType.class).setIDRange(0, 2048).create();
        CelestialBody.CELESTIAL_BODIES = new RegistryBuilder<CelestialBody>().setName(new Identifier(HeroesUnited.MODID, "celestial_bodies")).setType(CelestialBody.class).setIDRange(0, Integer.MAX_VALUE).create();
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IHUPlayer.class);
        CapabilityManager.INSTANCE.register(IHUAbilityCap.class);
        CapabilityManager.INSTANCE.register(IHUDataCap.class);

        HUNetworking.registerMessages();
        LOGGER.info(MODID + ": common is ready!");
    }

    public static final ItemGroup ACCESSORIES = new ItemGroup(ItemGroup.GROUPS.length, "accessories") {

        @Override
        public ItemStack createIcon() {
            return HUItems.BOBO_ACCESSORY.getDefaultStack();
        }
    };
}
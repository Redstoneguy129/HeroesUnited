package xyz.heroesunited.heroesunited.common;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.events.BlockCollisionEvent;
import xyz.heroesunited.heroesunited.common.events.EntitySprintingEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncCelestialBody;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncSuperpowers;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.space.CelestialBodies;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.common.space.Planet;
import xyz.heroesunited.heroesunited.hupacks.HUPackPowers;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.hupacks.js.item.IJSItem;
import xyz.heroesunited.heroesunited.mixin.entity.LivingEntityAccessor;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.HUTickrate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

public class EventHandler {

    @SubscribeEvent
    public void playerSize(EntityEvent.Size event) {
        if (event.getEntity().isAddedToWorld() && event.getEntity() instanceof Player player) {
            IFlyingAbility flying = IFlyingAbility.getFlyingAbility(player);
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                if (flying != null && flying.isFlying(player)) {
                    if (!player.isOnGround() && player.isSprinting()) {
                        if (event.getOldSize().fixed) {
                            event.setNewSize(EntityDimensions.fixed(0.6F, 0.6F));
                        } else {
                            event.setNewSize(EntityDimensions.scalable(0.6F, 0.6F));
                        }
                        event.setNewEyeHeight(0.4F);
                    }
                }
            });
            for (SizeChangeAbility a : AbilityHelper.getListOfType(SizeChangeAbility.class, AbilityHelper.getAbilities(player))) {
                if (a.getSize() != 1.0F) {
                    event.setNewSize(event.getNewSize().scale(a.getSize()));
                    event.setNewEyeHeight(event.getNewEyeHeight() * a.getSize());
                }
            }
            GlidingAbility glidingAbility = GlidingAbility.getInstance(player);
            if (glidingAbility != null && glidingAbility.canGliding(player)) {
                if (event.getOldSize().fixed) {
                    event.setNewSize(EntityDimensions.fixed(0.6F, 0.6F));
                } else {
                    event.setNewSize(EntityDimensions.scalable(0.6F, 0.6F));
                }
                event.setNewEyeHeight(0.4F);
            }
        }
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.setNewSize(event.getNewSize().scale(0.01F, 0.01F));
            event.setNewEyeHeight(event.getNewEyeHeight() * 0.01F);
        }
    }

    @SubscribeEvent
    public void cancelSprinting(EntitySprintingEvent event) {
        AbilityHelper.getAbilities(event.getEntity()).forEach(a -> a.cancelSprinting(event));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!event.player.level.isClientSide && event.player.level.dimension().equals(HeroesUnited.SPACE))
                for (CelestialBody celestialBody : CelestialBodies.REGISTRY.get().getValues()) {
                    celestialBody.tick();
                    for (Player mpPlayer : event.player.level.players()) {
                        HUNetworking.INSTANCE.sendTo(new ClientSyncCelestialBody(celestialBody.writeNBT(), celestialBody.getRegistryName()), ((ServerPlayer) mpPlayer).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    }
                }
            for (Ability a : AbilityHelper.getAbilities(event.player)) {
                a.getDataManager().syncToAll(event.player, a.name);
            }
            event.player.getCapability(HUDataCap.CAPABILITY).ifPresent(a -> a.getDataManager().syncToAll(event.player, ""));
            HUTickrate.tick(event.player, event.side);
        }
    }

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity != null && entity.isAlive()) {
            if (!entity.level.isClientSide && !HUPlayerUtil.canBreath(entity)) {
                entity.hurt((new DamageSource("space_drown")).bypassArmor(), 1);
            }
            if (!(entity.level.dimension().equals(Level.OVERWORLD) || entity.level.dimension().equals(Level.NETHER) || entity.level.dimension().equals(Level.END))) {
                JsonObject jsonObject = null;
                if (entity.level.getServer() != null) {
                    ResourceLocation res = entity.level.dimension().location();
                    try (
                            InputStreamReader reader = new InputStreamReader(entity.level.getServer().getResourceManager().getResource(
                                    new ResourceLocation(res.getNamespace(), String.format("dimension_type/%s.json", res.getPath()))).getInputStream(), StandardCharsets.UTF_8)
                    ) {
                        jsonObject = GsonHelper.fromJson(HUPacks.GSON, reader, JsonObject.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonObject != null && jsonObject.has("gravity")) {
                    AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), GsonHelper.getAsFloat(jsonObject, "gravity"), AttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            } else {
                AttributeInstance instance = entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
                if (instance != null) {
                    AttributeModifier modifier = instance.getModifier(UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"));
                    if (modifier != null && modifier.getAmount() != 0) {
                        AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), 0, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    }
                }
            }


            if (entity.level.dimension().equals(HeroesUnited.SPACE)) {
                if (!entity.isCrouching()) {
                    entity.setNoGravity(true);
                    entity.setOnGround(true);
                } else {
                    entity.setNoGravity(false);
                }
                for (CelestialBody celestialBody : CelestialBodies.REGISTRY.get().getValues()) {
                    if (celestialBody.getBoundingBox() != null && entity.level.getEntities(null, celestialBody.getBoundingBox()).contains(entity) && !entity.level.isClientSide) {
                        celestialBody.entityInside(entity);
                    }
                }
            } else {
                entity.setNoGravity(false);
                if (Planet.PLANETS_MAP.containsKey(entity.level.dimension()) && entity.position().y > 10050 && !entity.level.isClientSide) {
                    Planet planet = Planet.PLANETS_MAP.get(entity.level.dimension());
                    ServerLevel spaceLevel = ((ServerLevel) entity.level).getServer().getLevel(HeroesUnited.SPACE);
                    if (spaceLevel != null) {
                        if (entity.getVehicle() == null) {
                            entity.changeDimension(spaceLevel, new ITeleporter() {
                                @Override
                                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                    Entity repositionedEntity = repositionEntity.apply(false);
                                    repositionedEntity.teleportTo(planet.getOutCoordinates().x, planet.getOutCoordinates().y, planet.getOutCoordinates().z);
                                    repositionedEntity.setNoGravity(false);
                                    return repositionedEntity;
                                }
                            });
                        } else {
                            entity.getVehicle().changeDimension(spaceLevel, new ITeleporter() {
                                @Override
                                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                    Entity repositionedEntity = repositionEntity.apply(false);
                                    repositionedEntity.teleportTo(planet.getOutCoordinates().x, planet.getOutCoordinates().y, planet.getOutCoordinates().z);
                                    repositionedEntity.setNoGravity(false);
                                    return repositionedEntity;
                                }
                            });
                        }
                    }

                }
            }
            if (entity instanceof Player pl) {
                pl.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                    for (Map.Entry<String, Ability> e : a.getAbilities().entrySet()) {
                        Ability ability = e.getValue();
                        if (ability != null && ability.alwaysActive(pl)) {
                            if (ability.canActivate(pl)) {
                                a.enable(e.getKey());
                            } else {
                                a.disable(e.getKey());
                            }
                        }
                    }
                });
                pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                    AbilityHelper.getAbilities(pl).forEach(type -> type.onUpdate(pl));

                    for (int i = 0; i < a.getInventory().getItems().size(); ++i) {
                        if (!a.getInventory().getItems().get(i).isEmpty()) {
                            a.getInventory().getItems().get(i).inventoryTick(pl.level, pl, i, false);
                        }
                    }
                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        SuitItem suitItem = Suit.getSuitItem(equipmentSlot, pl);
                        if (suitItem != null) {
                            suitItem.getSuit().onUpdate(pl, equipmentSlot);
                        }
                    }
                    IFlyingAbility b = IFlyingAbility.getFlyingAbility(pl);
                    if (b != null && b.isFlying(pl)) {
                        pl.refreshDimensions();
                        if (!pl.isOnGround()) {
                            a.updateFlyAmount();
                            pl.playSound(b.getSoundEvent(), 0.05F, 0.5F);

                            float j = 0.0F;
                            if (pl.isShiftKeyDown()) {
                                j = -0.2F;
                            }

                            if (((LivingEntityAccessor) pl).isJumping()) {
                                j = 0.2F;
                            }

                            if (pl.zza > 0F) {
                                Vec3 vec = pl.getLookAngle();
                                double speed = pl.isSprinting() ? 2.5f : 1f;
                                for (FlightAbility ability : AbilityHelper.getListOfType(FlightAbility.class, AbilityHelper.getAbilities(pl))) {
                                    if (ability.getJsonObject().has("speed")) {
                                        speed = pl.isSprinting() ? GsonHelper.getAsFloat(ability.getJsonObject(), "maxSpeed", 2.5F) : GsonHelper.getAsFloat(ability.getJsonObject(), "speed");
                                    }
                                }
                                pl.setDeltaMovement(vec.x * speed, j + vec.y * speed, vec.z * speed);
                            } else {
                                pl.setDeltaMovement(new Vec3(pl.getDeltaMovement().x, j + Math.sin(pl.tickCount / 10F) / 100F, pl.getDeltaMovement().z));
                            }
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        if (!event.getEntityLiving().level.isClientSide && event.getEntityLiving().level.dimension().equals(HeroesUnited.SPACE) && event.getSource() == DamageSource.OUT_OF_WORLD) {
            event.setCanceled(true);
        }
        if (event.getEntityLiving() instanceof Player) {
            for (Ability a : AbilityHelper.getAbilities(event.getEntityLiving())) {
                if (a.type.equals(AbilityType.DAMAGE_IMMUNITY)) {
                    for (String s : HUJsonUtils.getStringsFromArray(GsonHelper.getAsJsonArray(a.getJsonObject(), "damage_sources"))) {
                        if (s.equals(event.getSource().getMsgId()) && a.getEnabled()) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onCancelCollision(BlockCollisionEvent event) {
        Entity entity = event.getEntity();
        AtomicBoolean collidable = new AtomicBoolean(true);
        if (entity instanceof Player) {
            entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                if (cap.isIntangible()) {
                    if (event.getState().getShape(event.getWorld(), event.getPos()) != Shapes.empty()) {
                        if (entity.position().y >= (event.getPos().getY() + event.getState().getShape(event.getWorld(), event.getPos()).bounds().getYsize())) {
                            IFlyingAbility b = IFlyingAbility.getFlyingAbility((Player) entity);
                            collidable.set(b != null && !b.isFlying((Player) entity) && !entity.isCrouching());
                        } else {
                            collidable.set(false);
                        }
                    } else {
                        collidable.set(false);
                    }
                }
            });
            event.setCanceled(!collidable.get());
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HUCoreCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onChangeEquipment(LivingEquipmentChangeEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                if (event.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
                    if (event.getFrom().getItem() instanceof SuitItem suitItem && !cap.getAbilities().isEmpty()) {
                        if (!(event.getTo().getItem() instanceof SuitItem) || ((SuitItem) event.getTo().getItem()).getSuit() != suitItem.getSuit()) {
                            cap.clearAbilities((a) -> {
                                if (suitItem.getAbilities(player).containsKey(a.name) && a.getAdditionalData().equals(suitItem.getAbilities(player).get(a.name).getAdditionalData()) && a.getAdditionalData().contains("Suit")) {
                                    if (a.getJsonObject().has("slot")) {
                                        return suitItem.getSlot().getName().toLowerCase().equals(GsonHelper.getAsString(a.getJsonObject(), "slot"));
                                    } else return true;
                                }
                                return false;
                            });
                            suitItem.getSuit().onDeactivated(player, suitItem.getSlot());
                        }
                    }
                    if (event.getTo().getItem() instanceof SuitItem suitItem) {

                        if (!suitItem.getAbilities(player).isEmpty()) {
                            for (Map.Entry<String, Ability> entry : suitItem.getAbilities(player).entrySet()) {
                                Ability a = entry.getValue();
                                boolean canAdd = a.getJsonObject().has("slot") && suitItem.getSlot().getName().toLowerCase().equals(GsonHelper.getAsString(a.getJsonObject(), "slot"));
                                if (canAdd || Suit.getSuit(player) != null) {
                                    cap.addAbility(entry.getKey(), a);
                                }
                            }
                        }
                        suitItem.getSuit().onActivated(player, suitItem.getSlot());
                    }
                }
                if (event.getFrom().getItem() instanceof IJSItem item && !cap.getAbilities().isEmpty()) {
                    cap.clearAbilities((a) -> item.getAbilities(player).containsKey(a.name) && a.getAdditionalData().getString("Item")
                            .equals(item.getAbilities(player).get(a.name).getAdditionalData().getString("Item")));
                }
                if (event.getTo().getItem() instanceof IJSItem item) {
                    EquipmentSlot slot = event.getTo().getEquipmentSlot() == null ? EquipmentSlot.MAINHAND : event.getTo().getEquipmentSlot();
                    if (!item.getAbilities(player).isEmpty()) {
                        for (Map.Entry<String, Ability> entry : item.getAbilities(player).entrySet()) {
                            if (slot == event.getSlot()) {
                                cap.addAbility(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void LivingFallEvent(LivingFallEvent event) {
        AttributeInstance fallAttribute = event.getEntityLiving().getAttribute(HUAttributes.FALL_RESISTANCE);
        if (fallAttribute != null && event.getEntityLiving() instanceof Player) {
            fallAttribute.setBaseValue(event.getDamageMultiplier());
            event.setDamageMultiplier((float) fallAttribute.getValue());
        }
    }

    @SubscribeEvent
    public void LivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        AttributeInstance attributeInstance = event.getEntityLiving().getAttribute(JUMP_BOOST);
        if (event.getEntityLiving() instanceof Player player && attributeInstance != null) {
            player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + 0.1F * attributeInstance.getValue(), player.getDeltaMovement().z);
        }
    }

    @SubscribeEvent
    public void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncSuperpowers(HUPackSuperpowers.getInstance().registeredSuperpowers), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @SubscribeEvent
    public void loggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncSuperpowers(Maps.newHashMap()), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @SubscribeEvent
    public void addListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new HUPackPowers());
        event.addListener(new HUPackSuperpowers());
    }

    @SubscribeEvent
    public void biomeLoading(BiomeLoadingEvent event) {
        if (BiomeDictionary.hasType(ResourceKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(event.getName())), BiomeDictionary.Type.OVERWORLD)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreFeatures.NATURAL_STONE,
                    HUBlocks.TITANIUM_ORE.defaultBlockState(), 4, 0.5F)).placed(List.of(CountPlacement.of(2), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(-32), VerticalAnchor.aboveBottom(32)), BiomeFilter.biome())));
        }
        //event.getGeneration().getStructures().add(() -> HUConfiguredStructures.CONFIGURED_CITY);
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
    /*private static Method GETCODEC_METHOD;

    @SubscribeEvent
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld)event.getWorld();

            // Skip terraforged
            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            } catch(Exception e){
                HeroesUnited.LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            // Skip Flat world
            if(serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator && serverWorld.dimension().equals(World.OVERWORLD)){
                return;
            }


//              putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
//              Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
//
//              NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as WorldGenRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
//              already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
//              And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
            tempMap.putIfAbsent(HUStructures.CITY.get(), DimensionStructuresSettings.DEFAULTS.get(HUStructures.CITY.get()));
            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
        }
    }*/
}

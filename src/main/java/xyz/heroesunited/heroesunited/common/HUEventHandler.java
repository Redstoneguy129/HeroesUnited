package xyz.heroesunited.heroesunited.common;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.*;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.server.ServerWorld;
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
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.network.messages.SyncAnimationMsg;
import software.bernie.geckolib3.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.events.HUCancelBlockCollision;
import xyz.heroesunited.heroesunited.common.events.HUCancelSprinting;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncCelestialBody;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.BoBoAccessory;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.common.space.Planet;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.mixin.entity.AccessorLivingEntity;
import xyz.heroesunited.heroesunited.util.HUOxygenHelper;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.HUTickrate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

public class HUEventHandler {

    @SubscribeEvent
    public void playerSize(EntityEvent.Size event) {
        if (event.getEntity().isAddedToWorld() && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            IFlyingAbility flying = IFlyingAbility.getFlyingAbility(player);
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                if ((flying != null && flying.isFlying(player)) || a.isFlying()) {
                    if (!player.isOnGround() && player.isSprinting()) {
                        if (event.getOldSize().fixed) {
                            event.setNewSize(EntitySize.fixed(0.6F, 0.6F));
                        } else {
                            event.setNewSize(EntitySize.scalable(0.6F, 0.6F));
                        }
                        event.setNewEyeHeight(0.4F);
                    }
                }
            });
            for (Ability ability : AbilityHelper.getAbilities(player)) {
                if (ability instanceof SizeChangeAbility) {
                    float size = ((SizeChangeAbility) ability).getSize();
                    if (size != 1.0F) {
                        event.setNewSize(event.getNewSize().scale(size));
                        event.setNewEyeHeight(event.getNewEyeHeight() * size);
                    }
                }
            }
        }
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.setNewSize(event.getNewSize().scale(0.01F, 0.01F));
            event.setNewEyeHeight(event.getNewEyeHeight() * 0.01F);
        }
    }

    @SubscribeEvent
    public void cancelSprinting(HUCancelSprinting event) {
        AbilityHelper.getAbilities(event.getEntity()).forEach(a -> a.cancelSprinting(event));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!event.player.level.isClientSide)
                for (CelestialBody celestialBody : CelestialBody.CELESTIAL_BODIES.getValues()) {
                    celestialBody.tick();
                    for (PlayerEntity mpPlayer : event.player.level.players()) {
                        HUNetworking.INSTANCE.sendTo(new ClientSyncCelestialBody(celestialBody.writeNBT(), celestialBody.getRegistryName()), ((ServerPlayerEntity) mpPlayer).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    }
                }
            for (Ability a : AbilityHelper.getAbilities(event.player)) {
                a.onUpdate(event.player, event.side);
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
            if (!entity.level.isClientSide && !HUOxygenHelper.canBreath(entity)) {
                entity.hurt((new DamageSource("space_drown")).bypassArmor(), 1);
            }
            if (!(entity.level.dimension().equals(World.OVERWORLD) || entity.level.dimension().equals(World.NETHER) || entity.level.dimension().equals(World.END))) {
                JsonObject jsonObject = null;
                if (entity.level instanceof ServerWorld) {
                    try {
                        IResourceManager manager = entity.level.getServer().getDataPackRegistries().getResourceManager();
                        ResourceLocation res = entity.level.dimension().location();
                        jsonObject = JSONUtils.fromJson(HUPacks.GSON, new BufferedReader(new InputStreamReader(manager.getResource(new ResourceLocation(res.getNamespace(), String.format("dimension_type/%s.json", res.getPath()))).getInputStream(), StandardCharsets.UTF_8)), JsonObject.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonObject != null && jsonObject.has("gravity")) {
                    AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), JSONUtils.getAsFloat(jsonObject, "gravity"), AttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            } else {
                AttributeModifier modifier = entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getModifier(UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"));
                if (modifier != null && modifier.getAmount() != 0) {
                    AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), 0, AttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            }


            if (entity.level.dimension().equals(HeroesUnited.SPACE)) {
                if (!entity.isCrouching()) {
                    entity.setNoGravity(true);
                    entity.setOnGround(true);
                } else {
                    entity.setNoGravity(false);
                }
                for (CelestialBody celestialBody : CelestialBody.CELESTIAL_BODIES.getValues()) {
                    if (celestialBody.getHitbox() != null && entity.level.getEntities(null, celestialBody.getHitbox()).contains(entity) && !entity.level.isClientSide) {
                        celestialBody.entityInside(entity);
                    }
                }
            } else {
                entity.setNoGravity(false);
                if (Planet.PLANETS_MAP.containsKey(entity.level.dimension()) && entity.position().y > 10050 && !entity.level.isClientSide) {
                    Planet planet = Planet.PLANETS_MAP.get(entity.level.dimension());
                    if (entity.getVehicle() == null) {
                        entity.changeDimension(((ServerWorld) entity.level).getServer().getLevel(HeroesUnited.SPACE), new ITeleporter() {
                            @Override
                            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                Entity repositionedEntity = repositionEntity.apply(false);

                                repositionedEntity.teleportTo(planet.getOutCoordinates().x, planet.getOutCoordinates().y, planet.getOutCoordinates().z);
                                repositionedEntity.setNoGravity(false);
                                return repositionedEntity;
                            }
                        });
                    } else {
                        entity.getVehicle().changeDimension(((ServerWorld) entity.level).getServer().getLevel(HeroesUnited.SPACE), new ITeleporter() {
                            @Override
                            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                Entity repositionedEntity = repositionEntity.apply(false);

                                repositionedEntity.teleportTo(planet.getOutCoordinates().x, planet.getOutCoordinates().y, planet.getOutCoordinates().z);
                                repositionedEntity.setNoGravity(false);
                                return repositionedEntity;
                            }
                        });
                    }

                }
            }
            if (entity instanceof PlayerEntity) {
                PlayerEntity pl = (PlayerEntity) entity;
                pl.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                    for (Map.Entry<String, Ability> e : a.getAbilities().entrySet()) {
                        Ability ability = e.getValue();
                        if (ability != null && ability.alwaysActive(pl)) {
                            if (ability.canActivate(pl)) {
                                a.enable(e.getKey(), ability);
                            } else {
                                a.disable(e.getKey());
                            }
                        }
                    }
                });
                pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                    IFlyingAbility ab = IFlyingAbility.getFlyingAbility(pl);
                    if ((ab != null && ab.isFlying(pl)) || a.isFlying()) {
                        pl.refreshDimensions();
                    }
                    AbilityHelper.getAbilities(pl).forEach(type -> type.onUpdate(pl));

                    for (int i = 0; i < a.getInventory().getItems().size(); ++i) {
                        if (!a.getInventory().getItems().get(i).isEmpty()) {
                            a.getInventory().getItems().get(i).inventoryTick(pl.level, pl, i, false);
                        }
                    }
                    ItemStack stack = a.getInventory().getItem(EquipmentAccessoriesSlot.HELMET.getSlot());
                    if (!stack.isEmpty() && stack.getItem() == HUItems.BOBO_ACCESSORY && !pl.level.isClientSide) {
                        BoBoAccessory accessory = ((BoBoAccessory) stack.getItem());
                        final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) pl.level);
                        AnimationController controller = GeckoLibUtil.getControllerForID(accessory.getFactory(), id, "controller");
                        final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pl);
                        if (controller.getAnimationState() == AnimationState.Stopped) {
                            HUNetworking.INSTANCE.send(target, new SyncAnimationMsg(accessory.getSyncKey(), id, 0));
                        }
                    }

                    for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
                        if (Suit.getSuitItem(equipmentSlot, pl) != null) {
                            Suit.getSuitItem(equipmentSlot, pl).getSuit().onUpdate(pl, equipmentSlot);
                        }
                    }

                    IFlyingAbility b = IFlyingAbility.getFlyingAbility(pl);
                    if ((b != null && b.isFlying(pl) && !pl.isOnGround()) || a.isFlying() && !pl.isOnGround()) {
                        HUPlayerUtil.playSoundToAll(pl.level, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HUSounds.FLYING, SoundCategory.PLAYERS, 0.05F, 0.5F);

                        float j = 0.0F;
                        if (pl.isShiftKeyDown()) {
                            j = -0.2F;
                        }

                        if (((AccessorLivingEntity) pl).isJumping()) {
                            j = 0.2F;
                        }

                        if (pl.zza > 0F) {
                            Vector3d vec = pl.getLookAngle();
                            double speed = pl.isSprinting() ? 2.5f : 1f;
                            for (Ability ability : AbilityHelper.getAbilities(pl)) {
                                if (ability instanceof FlightAbility && ability.getJsonObject() != null && ability.getJsonObject().has("speed")) {
                                    speed = pl.isSprinting() ? JSONUtils.getAsFloat(ability.getJsonObject(), "maxSpeed", 2.5F) : JSONUtils.getAsFloat(ability.getJsonObject(), "speed");
                                }
                            }
                            pl.setDeltaMovement(vec.x * speed, j + vec.y * speed, vec.z * speed);
                        } else {
                            pl.setDeltaMovement(new Vector3d(pl.getDeltaMovement().x, j + Math.sin(pl.tickCount) / 10000F, pl.getDeltaMovement().z));
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
        if (event.getEntityLiving() instanceof PlayerEntity) {
            for (Ability ability : AbilityHelper.getAbilities(event.getEntityLiving())) {
                if (ability instanceof DamageImmunityAbility && ((DamageImmunityAbility) ability).haveImmuneTo(event.getSource())) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCancelCollision(HUCancelBlockCollision event) {
        Entity entity = event.getEntity();
        AtomicBoolean collidable = new AtomicBoolean(true);
        if (entity instanceof PlayerEntity) {
            entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                if (cap != null && cap.isIntangible()) {
                    if (event.getState().getShape(event.getWorld(), event.getPos()) != VoxelShapes.empty()) {
                        if (entity.position().y >= (event.getPos().getY() + event.getState().getShape(event.getWorld(), event.getPos()).bounds().getYsize())) {
                            IFlyingAbility b = IFlyingAbility.getFlyingAbility((PlayerEntity) entity);
                            collidable.set(((b != null && !b.isFlying((PlayerEntity) entity)) || !cap.isFlying()) && !entity.isCrouching());
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
        if (event.getEntityLiving() instanceof PlayerEntity && event.getSlot().getType() == EquipmentSlotType.Group.ARMOR) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                if (event.getTo().getItem() instanceof SuitItem) {
                    SuitItem suitItem = (SuitItem) event.getTo().getItem();

                    if (!suitItem.getAbilities(player).isEmpty()) {
                        for (Map.Entry<String, Ability> entry : suitItem.getAbilities(player).entrySet()) {
                            Ability a = entry.getValue();
                            boolean canAdd = a.getJsonObject() != null && a.getJsonObject().has("slot") && suitItem.getSlot().getName().toLowerCase().equals(JSONUtils.getAsString(a.getJsonObject(), "slot"));
                            if (canAdd || Suit.getSuit(player) != null) {
                                cap.addAbility(entry.getKey(), a);
                            }
                        }
                    }
                    suitItem.getSuit().onActivated(player, suitItem.getSlot());
                } else if (event.getFrom().getItem() instanceof SuitItem && !cap.getAbilities().isEmpty()) {
                    SuitItem suitItem = (SuitItem) event.getFrom().getItem();
                    for (Ability a : AbilityHelper.getAbilityMap(player).values()) {
                        if (suitItem.getAbilities(player).containsKey(a.name)) {
                            CompoundNBT suit = suitItem.getAbilities(player).get(a.name).getAdditionalData();
                            if (a.getAdditionalData().equals(suit) && a.getAdditionalData().contains("Suit")) {
                                cap.removeAbility(a.name);
                            }
                        }
                    }
                    suitItem.getSuit().onDeactivated(player, suitItem.getSlot());
                }
            });
        }
    }

    @SubscribeEvent
    public void LivingFallEvent(LivingFallEvent event) {
        ModifiableAttributeInstance fallAttribute = event.getEntityLiving().getAttribute(HUAttributes.FALL_RESISTANCE);
        if (fallAttribute != null && event.getEntityLiving() instanceof PlayerEntity) {
            fallAttribute.setBaseValue(event.getDamageMultiplier());
            event.setDamageMultiplier((float) fallAttribute.getValue());
        }
    }

    @SubscribeEvent
    public void LivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (!event.getEntityLiving().isCrouching() && event.getEntityLiving() instanceof PlayerEntity) {
            event.getEntityLiving().setDeltaMovement(event.getEntity().getDeltaMovement().x, event.getEntity().getDeltaMovement().y + 0.1F * event.getEntityLiving().getAttribute(JUMP_BOOST).getValue(), event.getEntity().getDeltaMovement().z);
        }
    }

    @SubscribeEvent
    public void addListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new HUPackSuperpowers());
    }

    @SubscribeEvent
    public void biomeLoading(BiomeLoadingEvent event) {
        if (BiomeDictionary.hasType(RegistryKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(event.getName())), BiomeDictionary.Type.OVERWORLD)) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                    HUBlocks.TITANIUM_ORE.defaultBlockState(), 4)).range(32).squared().count(2));
        }
    }
}

package xyz.heroesunited.heroesunited.common;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
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
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.network.GeckoLibNetwork;
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
import xyz.heroesunited.heroesunited.hupacks.HUPackPowers;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.hupacks.js.item.IJSItem;
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
        if (event.getEntity().isAddedToWorld() && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            IFlyingAbility flying = IFlyingAbility.getFlyingAbility(player);
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                if ((flying != null && flying.isFlying(player)) || a.isFlying()) {
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
                    for (Player mpPlayer : event.player.level.players()) {
                        HUNetworking.INSTANCE.sendTo(new ClientSyncCelestialBody(celestialBody.writeNBT(), celestialBody.getRegistryName()), ((ServerPlayer) mpPlayer).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
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
            if (!(entity.level.dimension().equals(Level.OVERWORLD) || entity.level.dimension().equals(Level.NETHER) || entity.level.dimension().equals(Level.END))) {
                JsonObject jsonObject = null;
                if (entity.level instanceof ServerLevel) {
                    try {
                        ResourceManager manager = entity.level.getServer().getResourceManager();
                        ResourceLocation res = entity.level.dimension().location();
                        jsonObject = GsonHelper.fromJson(HUPacks.GSON, new BufferedReader(new InputStreamReader(manager.getResource(new ResourceLocation(res.getNamespace(), String.format("dimension_type/%s.json", res.getPath()))).getInputStream(), StandardCharsets.UTF_8)), JsonObject.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonObject != null && jsonObject.has("gravity")) {
                    AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), GsonHelper.getAsFloat(jsonObject, "gravity"), AttributeModifier.Operation.MULTIPLY_TOTAL);
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
                        entity.changeDimension(((ServerLevel) entity.level).getServer().getLevel(HeroesUnited.SPACE), new ITeleporter() {
                            @Override
                            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                Entity repositionedEntity = repositionEntity.apply(false);

                                repositionedEntity.teleportTo(planet.getOutCoordinates().x, planet.getOutCoordinates().y, planet.getOutCoordinates().z);
                                repositionedEntity.setNoGravity(false);
                                return repositionedEntity;
                            }
                        });
                    } else {
                        entity.getVehicle().changeDimension(((ServerLevel) entity.level).getServer().getLevel(HeroesUnited.SPACE), new ITeleporter() {
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
            if (entity instanceof Player) {
                Player pl = (Player) entity;
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
                        final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerLevel) pl.level);
                        AnimationController controller = GeckoLibUtil.getControllerForID(accessory.getFactory(), id, "controller");
                        final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pl);
                        if (controller.getAnimationState() == AnimationState.Stopped) {
                            GeckoLibNetwork.syncAnimation(target, accessory, id, 0);
                        }
                    }

                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        if (Suit.getSuitItem(equipmentSlot, pl) != null) {
                            Suit.getSuitItem(equipmentSlot, pl).getSuit().onUpdate(pl, equipmentSlot);
                        }
                    }

                    IFlyingAbility b = IFlyingAbility.getFlyingAbility(pl);
                    if ((b != null && b.isFlying(pl) && !pl.isOnGround()) || a.isFlying() && !pl.isOnGround()) {
                        HUPlayerUtil.playSoundToAll(pl.level, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HUSounds.FLYING, SoundSource.PLAYERS, 0.05F, 0.5F);

                        float j = 0.0F;
                        if (pl.isShiftKeyDown()) {
                            j = -0.2F;
                        }

                        if (((AccessorLivingEntity) pl).isJumping()) {
                            j = 0.2F;
                        }

                        if (pl.zza > 0F) {
                            Vec3 vec = pl.getLookAngle();
                            double speed = pl.isSprinting() ? 2.5f : 1f;
                            for (Ability ability : AbilityHelper.getAbilities(pl)) {
                                if (ability instanceof FlightAbility && ability.getJsonObject() != null && ability.getJsonObject().has("speed")) {
                                    speed = pl.isSprinting() ? GsonHelper.getAsFloat(ability.getJsonObject(), "maxSpeed", 2.5F) : GsonHelper.getAsFloat(ability.getJsonObject(), "speed");
                                }
                            }
                            pl.setDeltaMovement(vec.x * speed, j + vec.y * speed, vec.z * speed);
                        } else {
                            pl.setDeltaMovement(new Vec3(pl.getDeltaMovement().x, j + Math.sin(pl.tickCount / 10F) / 100F, pl.getDeltaMovement().z));
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
        if (entity instanceof Player) {
            entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                if (cap != null && cap.isIntangible()) {
                    if (event.getState().getShape(event.getWorld(), event.getPos()) != Shapes.empty()) {
                        if (entity.position().y >= (event.getPos().getY() + event.getState().getShape(event.getWorld(), event.getPos()).bounds().getYsize())) {
                            IFlyingAbility b = IFlyingAbility.getFlyingAbility((Player) entity);
                            collidable.set(((b != null && !b.isFlying((Player) entity)) || !cap.isFlying()) && !entity.isCrouching());
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
        if (event.getEntityLiving() instanceof Player && event.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
            Player player = (Player) event.getEntityLiving();
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                if (event.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
                    if (event.getTo().getItem() instanceof SuitItem suitItem) {

                        if (!suitItem.getAbilities(player).isEmpty()) {
                            for (Map.Entry<String, Ability> entry : suitItem.getAbilities(player).entrySet()) {
                                Ability a = entry.getValue();
                                boolean canAdd = a.getJsonObject() != null && a.getJsonObject().has("slot") && suitItem.getSlot().getName().toLowerCase().equals(GsonHelper.getAsString(a.getJsonObject(), "slot"));
                                if (canAdd || Suit.getSuit(player) != null) {
                                    cap.addAbility(entry.getKey(), a);
                                }
                            }
                        }
                        suitItem.getSuit().onActivated(player, suitItem.getSlot());
                    }
                    if (event.getFrom().getItem() instanceof SuitItem suitItem && !cap.getAbilities().isEmpty()) {
                        for (Ability a : AbilityHelper.getAbilityMap(player).values()) {
                            if (suitItem.getAbilities(player).containsKey(a.name)) {
                                CompoundTag suit = suitItem.getAbilities(player).get(a.name).getAdditionalData();
                                if (a.getAdditionalData().equals(suit) && a.getAdditionalData().contains("Suit")) {
                                    if (a.getJsonObject() != null && a.getJsonObject().has("slot")) {
                                        if (suitItem.getSlot().getName().toLowerCase().equals(GsonHelper.getAsString(a.getJsonObject(), "slot"))) {
                                            cap.removeAbility(a.name);
                                        }
                                    } else {
                                        cap.removeAbility(a.name);
                                    }
                                }
                            }
                        }
                        suitItem.getSuit().onDeactivated(player, suitItem.getSlot());
                    }
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
                if (event.getFrom().getItem() instanceof IJSItem item && !cap.getAbilities().isEmpty()) {
                    for (Ability a : AbilityHelper.getAbilityMap(player).values()) {
                        if (item.getAbilities(player).containsKey(a.name)) {
                            CompoundTag suit = item.getAbilities(player).get(a.name).getAdditionalData();
                            if (a.getAdditionalData().equals(suit)) {
                                cap.removeAbility(a.name);
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
        if (!event.getEntityLiving().isCrouching() && event.getEntityLiving() instanceof Player) {
            event.getEntityLiving().setDeltaMovement(event.getEntity().getDeltaMovement().x, event.getEntity().getDeltaMovement().y + 0.1F * event.getEntityLiving().getAttribute(JUMP_BOOST).getValue(), event.getEntity().getDeltaMovement().z);
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
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE,
                    HUBlocks.TITANIUM_ORE.defaultBlockState(), 4)).rangeUniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(32)).squared().count(2));
        }
    }
}

package xyz.heroesunited.heroesunited.common;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
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
import software.bernie.geckolib3.network.messages.SyncAnimationMsg;
import software.bernie.geckolib3.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUBoundingBoxEvent;
import xyz.heroesunited.heroesunited.client.events.HUEyeHeightEvent;
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.events.HUCancelBlockCollision;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncCelestialBody;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.BoBoAccessory;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.common.space.CelestialBody;
import xyz.heroesunited.heroesunited.common.space.Planet;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;
import xyz.heroesunited.heroesunited.mixin.entity.AccessorLivingEntity;
import xyz.heroesunited.heroesunited.util.HUDamageSource;
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
            IFlyingAbility ability = IFlyingAbility.getFlyingAbility(player);
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                if ((ability != null && ability.isFlying(player)) || a.isFlying()) {
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
        }
        if (event.getEntity().level.dimension().equals(HeroesUnited.SPACE)) {
            event.setNewSize(event.getNewSize().scale(0.01F, 0.01F));
            event.setNewEyeHeight(event.getNewEyeHeight() * 0.01F);
        }
    }

    @SubscribeEvent
    public void changeEyeheight(HUEyeHeightEvent event) {
        for (Ability ability : AbilityHelper.getAbilities(event.getEntityLiving())) {
            if (event.getEntityLiving() instanceof PlayerEntity && ability instanceof SizeChangeAbility) {
                float height = ((SizeChangeAbility) ability).getSize();
                if (height != 1.0F) {
                    event.setNewEyeHeight(event.getOldEyeHeight() * height);
                }
            }
        }
    }

    @SubscribeEvent
    public void changeBoundingBox(HUBoundingBoxEvent event) {
        for (Ability ability : AbilityHelper.getAbilities(event.getPlayer())) {
            if (ability instanceof SizeChangeAbility) {
                float size = ((SizeChangeAbility) ability).getSize();
                event.setNewSize(event.getOldSize().scaled(size, size));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!event.player.level.isClientSide)
                for (CelestialBody celestialBody : CelestialBody.CELESTIAL_BODIES.getValues()) {
                    celestialBody.tick();
                    for (PlayerEntity mpPlayer : event.player.level.players()) {
                        HUNetworking.INSTANCE.sendTo(new ClientSyncCelestialBody(celestialBody.writeNBT(), celestialBody.getRegistryName()), ((ServerPlayerEntity) mpPlayer).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    }
                }
            AbilityHelper.getAbilities(event.player).forEach(type -> type.onUpdate(event.player, event.side));
            HUTickrate.tick(event.player, event.side);
        }
    }

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity != null && entity.isAlive()) {
            if (!entity.world.isClient && !HUOxygenHelper.canBreath(entity)) {
                entity.damage((new HUDamageSource("space_drown")).setBypassesArmor(), 1);
            }
            if (!(entity.world.getRegistryKey().equals(World.OVERWORLD) || entity.world.getRegistryKey().equals(World.NETHER) || entity.world.getRegistryKey().equals(World.END))) {
                JsonObject jsonObject = null;
                if (entity.world instanceof ServerWorld) {
                    try {
                        ResourceManager manager = entity.world.getServer().getDataPackRegistries().getResourceManager();
                        Identifier res = entity.world.getRegistryKey().getValue();
                        jsonObject = JsonHelper.deserialize(HUPacks.GSON, new BufferedReader(new InputStreamReader(manager.getResource(new Identifier(res.getNamespace(), String.format("dimension_type/%s.json", res.getPath()))).getInputStream(), StandardCharsets.UTF_8)), JsonObject.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jsonObject != null && jsonObject.has("gravity")) {
                    AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), JsonHelper.getFloat(jsonObject, "gravity"), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            } else {
                EntityAttributeModifier modifier = entity.getAttributeInstance(ForgeMod.ENTITY_GRAVITY.get()).getModifier(UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"));
                if (modifier != null && modifier.getValue() != 0) {
                    AbilityHelper.setAttribute(entity, "hu_gravity", ForgeMod.ENTITY_GRAVITY.get(), UUID.fromString("f308847a-43e7-4aaa-a0a5-f474dac5404e"), 0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                }
            }


            if (entity.world.getRegistryKey().equals(HeroesUnited.SPACE)) {
                if (!entity.isInSneakingPose()) {
                    entity.setNoGravity(true);
                    entity.setOnGround(true);
                } else {
                    entity.setNoGravity(false);
                }
                for (CelestialBody celestialBody : CelestialBody.CELESTIAL_BODIES.getValues()) {
                    if (celestialBody.getHitbox() != null && entity.world.getOtherEntities(null, celestialBody.getHitbox()).contains(entity) && !entity.world.isClient) {
                        celestialBody.entityInside(entity);
                    }
                }
            } else {
                entity.setNoGravity(false);
                if (Planet.PLANETS_MAP.containsKey(entity.world.getRegistryKey()) && entity.getPos().y > 10050 && !entity.world.isClient) {
                    entity.moveToWorld(((ServerWorld) entity.world).getServer().getWorld(HeroesUnited.SPACE));
                }
            }
            if (entity instanceof PlayerEntity) {
                PlayerEntity pl = (PlayerEntity) entity;
                pl.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                    for (Map.Entry<String, Ability> e : a.getAbilities().entrySet()) {
                        Ability ability = e.getValue();
                        if (ability != null && ability.alwaysActive(pl)) {
                            if (AbilityHelper.canActiveAbility(ability, pl)) {
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
                            HUNetworking.INSTANCE.send(target, new SyncAnimationMsg(accessory.getSyncKey(), id, 0));
                        }
                    }

                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        if (Suit.getSuitItem(equipmentSlot, pl) != null) {
                            Suit.getSuitItem(equipmentSlot, pl).getSuit().onUpdate(pl, equipmentSlot);
                        }
                    }

                    IFlyingAbility b = IFlyingAbility.getFlyingAbility(pl);
                    if ((b != null && b.isFlying(pl) && !pl.isOnGround()) || a.isFlying() && !pl.isOnGround()) {
                        HUPlayerUtil.playSoundToAll(pl.world, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HeroesUnited.FLYING, SoundCategory.PLAYERS, 0.05F, 0.5F);

                        float j = 0.0F;
                        if (pl.isSneaking()) {
                            j = -0.2F;
                        }

                        if (((AccessorLivingEntity) pl).isJumping()) {
                            j = 0.2F;
                        }

                        if (pl.forwardSpeed > 0F) {
                            Vec3d vec = pl.getRotationVector();
                            double speed = pl.isSprinting() ? 2.5f : 1f;
                            for (Ability ability : AbilityHelper.getAbilities(pl)) {
                                if (ability instanceof FlightAbility && ability.getJsonObject() != null && ability.getJsonObject().has("speed")) {
                                    speed = pl.isSprinting() ? JsonHelper.getFloat(ability.getJsonObject(), "maxSpeed", 2.5F) : JsonHelper.getFloat(ability.getJsonObject(), "speed");
                                }
                            }
                            pl.setVelocity(vec.x * speed, j + vec.y * speed, vec.z * speed);
                        } else {
                            pl.setDeltaMovement(new Vec3(pl.getDeltaMovement().x, j + Math.sin(pl.tickCount) / 10000F, pl.getDeltaMovement().z));
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

    }

    @SubscribeEvent
    public void onChangeEquipment(LivingEquipmentChangeEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && event.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getCapability(HUAbilityCap.CAPABILITY).ifPresent(cap -> {
                if (event.getTo().getItem() instanceof SuitItem) {
                    SuitItem suitItem = (SuitItem) event.getTo().getItem();

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
                    for (Ability ability : AbilityHelper.getAbilities(player)) {
                        if (ability != null && suitItem.getSuit().hasArmorOn(player) && !suitItem.getSuit().canCombineWithAbility(ability, player)) {
                            AbilityHelper.disable(player);
                        }
                    }
                } else if (event.getFrom().getItem() instanceof SuitItem && !cap.getAbilities().isEmpty()) {
                    SuitItem suitItem = (SuitItem) event.getFrom().getItem();
                    for (Ability a : AbilityHelper.getAbilityMap(player).values()) {
                        if (suitItem.getAbilities(player).containsKey(a.name)) {
                            CompoundTag suit = suitItem.getAbilities(player).get(a.name).getAdditionalData();
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
        EntityAttributeInstance fallAttribute = event.getEntityLiving().getAttribute(HUAttributes.FALL_RESISTANCE);
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
        if (BiomeDictionary.hasType(RegistryKey.of(Registry.BIOME_KEY, Objects.requireNonNull(event.getName())), BiomeDictionary.Type.OVERWORLD)) {
            event.getGeneration().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    HUBlocks.TITANIUM_ORE.getDefaultState(), 4)).range(32).squared().count(2));
        }
    }
}

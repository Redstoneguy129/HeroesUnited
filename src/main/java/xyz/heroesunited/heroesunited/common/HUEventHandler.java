package xyz.heroesunited.heroesunited.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
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
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;
import xyz.heroesunited.heroesunited.common.planets.Planet;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.HUTickrate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.ATTRIBUTES;
import static xyz.heroesunited.heroesunited.common.objects.HUAttributes.JUMP_BOOST;

public class HUEventHandler {


    @SubscribeEvent
    public void playerSize(EntityEvent.Size event) {
        if (event.getEntity().isAddedToWorld() && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            player.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(cap -> {
                if (cap.isFlying() && !player.isOnGround() && player.isSprinting()) {
                    if (event.getOldSize().fixed) {
                        event.setNewSize(EntitySize.fixed(0.6F, 0.6F));
                    } else {
                        event.setNewSize(EntitySize.scalable(0.6F, 0.6F));
                    }
                    event.setNewEyeHeight(0.4F);
                }
            });
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
                event.setNewSize(event.getOldSize().scale(size, size));
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            HUTickrate.tick(event.player, event.side);
        }
    }

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().isAlive()) {
            if (event.getEntityLiving().level.dimension().equals(HeroesUnited.SPACE)) {
                if(!event.getEntityLiving().isCrouching()){
                    event.getEntityLiving().setNoGravity(true);
                    event.getEntityLiving().setOnGround(true);
                } else {
                    event.getEntityLiving().setNoGravity(false);
                }
                for (Planet planet : Planet.PLANETS.getValues()) {
                    if (event.getEntityLiving().level.getEntities(null, planet.getHitbox()).contains(event.getEntity()) && !event.getEntityLiving().level.isClientSide) {
                        event.getEntityLiving().changeDimension(((ServerWorld) event.getEntityLiving().level).getServer().getLevel(planet.getDimension()), new ITeleporter() {
                            @Override
                            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                                Entity repositionedEntity = repositionEntity.apply(false);

                                repositionedEntity.teleportTo(0, 10000, 0);

                                return repositionedEntity;
                            }
                        });
                    }
                }
            } else {
                if (Planet.PLANETS_MAP.containsKey(event.getEntityLiving().level.dimension()) && event.getEntityLiving().position().y > 10050) {
                    Planet planet = Planet.PLANETS_MAP.get(event.getEntityLiving().level.dimension());
                    event.getEntityLiving().changeDimension(((ServerWorld) event.getEntityLiving().level).getServer().getLevel(HeroesUnited.SPACE), new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                            Entity repositionedEntity = repositionEntity.apply(false);

                            repositionedEntity.teleportTo(planet.getOutCoordinates().x,planet.getOutCoordinates().y,planet.getOutCoordinates().z);

                            return repositionedEntity;
                        }
                    });
                }
                AbilityHelper.setAttribute(event.getEntityLiving(), "space_gravity", ForgeMod.ENTITY_GRAVITY.get(),
                        UUID.fromString("16c0c8f6-565e-4175-94f5-029986f3cc1d"),
                        0,
                        AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
        }
        if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving() != null) {
            PlayerEntity pl = (PlayerEntity) event.getEntityLiving();
            pl.getCapability(HUAbilityCap.CAPABILITY).ifPresent(a -> {
                for (Map.Entry<String, Ability> e : a.getAbilities().entrySet()) {
                    Ability ability = e.getValue();
                    if (ability != null && ability.alwaysActive()) {
                        if (AbilityHelper.canActiveAbility(ability, pl)) {
                            a.enable(e.getKey(), ability);
                        } else {
                            a.disable(e.getKey());
                        }
                    }
                }
            });
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                AbilityHelper.getAbilities(pl).forEach(type -> type.onUpdate(pl));

                for (int i = 0; i < a.getInventory().getInventory().size(); ++i) {
                    if (!a.getInventory().getInventory().get(i).isEmpty()) {
                        a.getInventory().getInventory().get(i).inventoryTick(pl.level, pl, i, false);
                    }
                }
                ItemStack stack = a.getInventory().getItem(EquipmentAccessoriesSlot.HELMET.getSlot());
                if (!stack.isEmpty() && stack.getItem() == HUItems.BOBO_ACCESSORY) {
                    AnimationController controller = GeckoLibUtil.getControllerForStack(((IAnimatable) stack.getItem()).getFactory(), stack, "controller");
                    if (controller.getAnimationState() == AnimationState.Stopped) {
                        controller.markNeedsReload();
                        controller.setAnimation((new AnimationBuilder()).addAnimation("animation.bobo", true));
                    }
                }

                for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
                    if (Suit.getSuitItem(equipmentSlot, pl) != null) {
                        Suit.getSuitItem(equipmentSlot, pl).getSuit().onUpdate(pl, equipmentSlot);
                    }
                }
                if (a.getAnimationTimer() > 0) a.setAnimationTimer(a.getAnimationTimer() + 1);
                if (a.getAnimationTimer() >= 3600) a.setAnimationTimer(3600);

                if (a.isFlying() && !pl.isOnGround()) {
                    HUPlayerUtil.playSoundToAll(pl.level, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HUSounds.FLYING : HUSounds.FLYING, SoundCategory.PLAYERS, 0.05F, 0.5F);
                    if (pl.zza > 0F) {
                        Vector3d vec = pl.getLookAngle();
                        double speed = pl.isSprinting() ? 2.5f : 1f;
                        pl.setDeltaMovement(vec.x * speed, vec.y * speed - (pl.isCrouching() ? pl.getBbHeight() * 0.2F : 0), vec.z * speed);
                    } else if (pl.isCrouching())
                        pl.setDeltaMovement(new Vector3d(pl.getDeltaMovement().x, pl.getBbHeight() * -0.2F, pl.getDeltaMovement().z));
                    else
                        pl.setDeltaMovement(new Vector3d(pl.getDeltaMovement().x, Math.sin(pl.tickCount / 10F) / 100F, pl.getDeltaMovement().z));
                }
            });
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
                            collidable.set(!cap.isFlying() && !entity.isCrouching());
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
                    for (Ability ability : AbilityHelper.getAbilities(player)) {
                        if (ability != null && suitItem.getSuit().hasArmorOn(player) && !suitItem.getSuit().canCombineWithAbility(ability, player)) {
                            AbilityHelper.disable(player);
                        }
                    }
                } else if (event.getFrom().getItem() instanceof SuitItem) {
                    SuitItem suitItem = (SuitItem) event.getFrom().getItem();
                    for (Ability ab1 : suitItem.getAbilities(player).values()) {
                        for (Ability a : cap.getAbilities().values().stream().collect(Collectors.toList())) {
                            if (a.name.equals(ab1.name)) {
                                a.setAdditionalData(ab1.getAdditionalData());
                                boolean all = a.getAdditionalData().equals(ab1.getAdditionalData()) && a.getAdditionalData().contains("Suit");
                                if (a.getAdditionalData().contains("Slot")) {
                                    String slot = a.getAdditionalData().getString("Slot");
                                    if (slot == "all" ? all : all && suitItem.getSlot().getName().toLowerCase().equals(slot)) {
                                        cap.removeAbility(a.name);
                                    }
                                } else if (all) {
                                    cap.removeAbility(a.name);
                                }
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

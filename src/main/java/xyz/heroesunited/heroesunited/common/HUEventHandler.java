package xyz.heroesunited.heroesunited.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.common.BiomeDictionary;
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
import xyz.heroesunited.heroesunited.common.abilities.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.events.HUCancelBlockCollision;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;
import xyz.heroesunited.heroesunited.hupacks.HUPackSuperpowers;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.HUTickrate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
                        event.setNewSize(EntitySize.flexible(0.6F, 0.6F));
                    }
                    event.setNewEyeHeight(0.4F);
                }
                for (Ability ability : cap.getActiveAbilities().values()) {
                    if (ability instanceof EyeHeightAbility) {
                        event.setNewEyeHeight(event.getOldEyeHeight() * JSONUtils.getFloat(ability.getJsonObject(), "amount", 1));
                    }
                }
            });
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
        if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving() != null) {
            PlayerEntity pl = (PlayerEntity) event.getEntityLiving();
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                AbilityHelper.getAbilities(pl).forEach(type -> {
                    type.onUpdate(pl);
                    if (type instanceof ITimerAbility) {
                        ITimerAbility timer = (ITimerAbility) type;
                        if (a.isInTimer() && a.getTimer() < timer.maxTimer()) {
                            a.setTimer(a.getTimer() + 1);
                        } else if (!a.isInTimer() & a.getTimer() > 0) {
                            a.setTimer(a.getTimer() - 1);
                        }
                    }
                });

                for (Map.Entry<String, Ability> e : a.getAbilities().entrySet()) {
                    Ability ability = e.getValue();
                    if (ability != null && ability.alwaysActive() && AbilityHelper.canActiveAbility(ability, pl)) {
                        a.enable(e.getKey(), ability);
                    }
                }

                for (int i = 0; i < a.getInventory().getInventory().size(); ++i) {
                    if (!a.getInventory().getInventory().get(i).isEmpty()) {
                        a.getInventory().getInventory().get(i).inventoryTick(pl.world, pl, i, false);
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
                    HUPlayerUtil.playSoundToAll(pl.world, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HUSounds.FLYING : HUSounds.FLYING, SoundCategory.PLAYERS, 0.05F, 0.5F);
                    if (pl.moveForward > 0F) {
                        Vector3d vec = pl.getLookVec();
                        double speed = pl.isSprinting() ? 2.5f : 1f;
                        pl.setMotion(vec.x * speed, vec.y * speed - (pl.isSneaking() ? pl.getHeight() * 0.2F : 0), vec.z * speed);
                    } else if (pl.isSneaking())
                        pl.setMotion(new Vector3d(pl.getMotion().x, pl.getHeight() * -0.2F, pl.getMotion().z));
                    else
                        pl.setMotion(new Vector3d(pl.getMotion().x, Math.sin(pl.ticksExisted / 10F) / 100F, pl.getMotion().z));
                }
            });
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            for (Ability ability : AbilityHelper.getAbilities((PlayerEntity) event.getEntityLiving())) {
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
                        if (entity.getPositionVec().y >= (event.getPos().getY() + event.getState().getShape(event.getWorld(), event.getPos()).getBoundingBox().getYSize())) {
                            collidable.set(!cap.isFlying() && !entity.isSneaking());
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
        if (event.getEntityLiving() instanceof PlayerEntity && event.getSlot().getSlotType() == EquipmentSlotType.Group.ARMOR) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (event.getTo().getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) event.getTo().getItem();
                suitItem.getSuit().onActivated(player, suitItem.getEquipmentSlot());
                for (Ability ability : AbilityHelper.getAbilities(player)) {
                    if (ability != null && suitItem.getSuit().hasArmorOn(player) && !suitItem.getSuit().canCombineWithAbility(ability, player)) {
                        AbilityHelper.disable(player);
                    }
                }
            } else if (event.getFrom().getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) event.getFrom().getItem();
                suitItem.getSuit().onDeactivated(player, suitItem.getEquipmentSlot());
            }
        }
    }

    @SubscribeEvent
    public void LivingFallEvent(LivingFallEvent event) {
        ModifiableAttributeInstance fallAttribute = event.getEntityLiving().getAttribute(HUAttributes.FALL_RESISTANCE);
        if (fallAttribute != null) {
            fallAttribute.setBaseValue(event.getDamageMultiplier());
            event.setDamageMultiplier((float) fallAttribute.getValue());
        }
    }

    @SubscribeEvent
    public void LivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (!event.getEntityLiving().isCrouching()) {
            event.getEntityLiving().setMotion(event.getEntity().getMotion().x, event.getEntity().getMotion().y + 0.1F * event.getEntityLiving().getAttribute(HUAttributes.JUMP_BOOST).getValue(), event.getEntity().getMotion().z);
        }
    }

    @SubscribeEvent
    public void addListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new HUPackSuperpowers());
    }

    @SubscribeEvent
    public void biomeLoading(BiomeLoadingEvent event) {
        if (BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, Objects.requireNonNull(event.getName())), BiomeDictionary.Type.OVERWORLD)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                    HUBlocks.TITANIUM_ORE.getDefaultState(), 4)).range(32).square().func_242731_b(2));
        }
    }
}

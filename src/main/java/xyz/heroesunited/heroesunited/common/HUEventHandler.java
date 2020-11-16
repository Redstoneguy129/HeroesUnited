package xyz.heroesunited.heroesunited.common;

import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.heroesunited.heroesunited.client.events.HUBoundingBoxEvent;
import xyz.heroesunited.heroesunited.client.events.HUEyeHeightEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.IFlyingAbility;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;
import xyz.heroesunited.heroesunited.common.command.HUCoreCommand;
import xyz.heroesunited.heroesunited.common.objects.HUAttributes;
import xyz.heroesunited.heroesunited.common.objects.HUSounds;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public class HUEventHandler {

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote) {
            MinecraftForge.EVENT_BUS.post(new HUEyeHeightEvent(event.getEntityLiving(), event.getEntityLiving().getEyeHeight()));
        }
        MinecraftForge.EVENT_BUS.post(new HUBoundingBoxEvent(event.getEntityLiving(), event.getEntityLiving().getBoundingBox()));
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity pl = event.player;
        if (event.phase == TickEvent.Phase.START) {
            pl.getCapability(HUPlayerProvider.CAPABILITY).ifPresent(a -> {
                for (AbilityType type : AbilityHelper.getAbilities(pl)) type.create().onUpdate(pl);
                if (Suit.getSuit(pl) != null) Suit.getSuit(pl).onUpdate(pl);
                if (a.isFlying() && !pl.isOnGround()) {
                    HUPlayerUtil.playSoundToAll(pl.world, HUPlayerUtil.getPlayerPos(pl), 10, IFlyingAbility.getFlyingAbility(pl) != null ? IFlyingAbility.getFlyingAbility(pl).getSoundEvent() : HUSounds.FLYING, SoundCategory.PLAYERS, 0.05F, 0.5F);
                    if (pl.moveForward > 0F) {
                        Vector3d vec = pl.getLookVec();
                        double speed = pl.isSprinting() ? 2.5f : 1f;
                        pl.setMotion(vec.x * speed, vec.y * speed - (pl.isSneaking() ? pl.getHeight() * 0.2F : 0), vec.z * speed);
                    } else if (pl.isSneaking()) pl.setMotion(new Vector3d(pl.getMotion().x, pl.getHeight() * -0.2F, pl.getMotion().z));
                    else pl.setMotion(new Vector3d(pl.getMotion().x, Math.sin(pl.ticksExisted / 10F) / 100F, pl.getMotion().z));
                }
            });
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HUCoreCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onChangeEquipment(LivingEquipmentChangeEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity && e.getSlot().getSlotType() == EquipmentSlotType.Group.ARMOR) {
            PlayerEntity player = (PlayerEntity) e.getEntityLiving();
            if (e.getTo().getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) e.getTo().getItem();
                if (Suit.getSuit(player) != null) {
                    suitItem.getSuit().onActivated(player);
                    for (AbilityType type : AbilityHelper.getAbilities(player)) {
                        if (type != null && !suitItem.getSuit().canCombineWithAbility(type, player)) {
                            AbilityHelper.disable(player);
                        }
                    }
                }
            } else if (e.getFrom().getItem() instanceof SuitItem) {
                SuitItem suitItem = (SuitItem) e.getFrom().getItem();
                suitItem.getSuit().onDeactivated(player);
            }
        }
    }

    @SubscribeEvent
    public void LivingFallEvent(LivingFallEvent e) {
        ModifiableAttributeInstance fallAttribute = e.getEntityLiving().getAttribute(HUAttributes.FALL_RESISTANCE);
        if (fallAttribute != null) {
            fallAttribute.setBaseValue(e.getDamageMultiplier());
            e.setDamageMultiplier((float) fallAttribute.getValue());
        }
    }

    @SubscribeEvent
    public void LivingJumpEvent(LivingEvent.LivingJumpEvent e) {
        Vector3d motion = e.getEntity().getMotion();
        if (!e.getEntityLiving().isCrouching() && e.getEntityLiving().getAttribute(HUAttributes.JUMP_BOOST) != null) {
            e.getEntityLiving().setMotion(motion.x, motion.y + 0.1F * e.getEntityLiving().getAttribute(HUAttributes.JUMP_BOOST).getValue(), motion.z);
        }
    }
}

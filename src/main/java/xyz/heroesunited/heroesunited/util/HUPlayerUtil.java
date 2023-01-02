package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;
import xyz.heroesunited.heroesunited.common.space.Planet;

import java.util.Objects;

public class HUPlayerUtil {

    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static boolean canBreath(LivingEntity entity) {
        boolean canBreath = !entity.level.dimension().equals(HeroesUnited.SPACE);
        if (Planet.PLANETS_MAP.containsKey(entity.level.dimension())) {
            Planet planet = Planet.PLANETS_MAP.get(entity.level.dimension());
            canBreath = planet.hasOxygen();
        }

        if (entity instanceof Player) {
            for (Ability a : AbilityHelper.getAbilities(entity)) {
                if (Objects.equals(a.type, AbilityType.OXYGEN) && !canBreath) {
                    canBreath = a.getEnabled();
                    break;
                }
            }
        }

        Suit suit = Suit.getSuit(entity);
        if (suit != null && !canBreath) {
            canBreath = suit.canBreathOnSpace();
        }

        return canBreath;
    }

    public static void playSoundToAll(Level world, Vec3 vec, double range, SoundEvent sound, SoundSource category, float volume, float pitch) {
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, getCollisionBoxWithRange(vec, range))) {
            ForgeRegistries.SOUND_EVENTS.getHolder(sound).ifPresent(holder -> player.connection
                    .send(new ClientboundSoundPacket(holder, category, vec.x, vec.y, vec.z, volume, pitch, player.getRandom().nextLong())));
        }
    }

    public static boolean haveSmallArms(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer) entity).getModelName().equalsIgnoreCase("slim");
        }
        return false;
    }

    public static AABB getCollisionBoxWithRange(Vec3 posVc3d, double range) {
        return new AABB(new BlockPos(posVc3d.x - range, posVc3d.y - range, posVc3d.z - range), new BlockPos(posVc3d.x + range, posVc3d.y + range, posVc3d.z + range));
    }

    public static Vec3 getPlayerPos(Player player) {
        return new Vec3(player.getX(), player.getY(), player.getZ());
    }

    public static HitResult getPosLookingAt(Entity entity, double distance) {
        HitResult hitResult = entity.pick(distance, 1F, false);
        Vec3 eyePos = entity.getEyePosition(1F);
        double d = hitResult.getLocation().distanceToSqr(eyePos);

        Vec3 viewVector = entity.getViewVector(1.0F);
        AABB aabb = entity.getBoundingBox().expandTowards(viewVector.scale(distance)).inflate(1.0D, 1.0D, 1.0D);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, eyePos, eyePos.add(viewVector.scale(distance)), aabb, (e) -> !e.isSpectator() && e.isPickable(), d);
        if (entityhitresult != null) {
            Vec3 vec3 = entityhitresult.getLocation();
            if (eyePos.distanceToSqr(vec3) < d || hitResult.getType() == HitResult.Type.MISS) {
                hitResult = entityhitresult;
            }
        }
        return hitResult;
    }

    public static void setSuitForPlayer(Player player, Suit suit) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item helmet = suit.getItemBySlot(slot);
            if (helmet != null) {
                if (player.getItemBySlot(slot).isEmpty()) {
                    player.setItemSlot(slot, new ItemStack(helmet));
                } else player.addItem(new ItemStack(helmet));
                player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        }
    }
}

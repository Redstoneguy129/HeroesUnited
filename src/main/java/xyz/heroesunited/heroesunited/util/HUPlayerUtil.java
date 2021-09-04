package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

public class HUPlayerUtil {

    public static void sendMessage(Player player, Component text) {
        player.sendMessage(text, player.getUUID());
    }

    public static void sendStatusMessage(Player player, Component text, boolean showInActionbar) {
        if (player.level.isClientSide) player.displayClientMessage(text, showInActionbar);
    }

    public static void playSoundToAll(Level world, Vec3 vec, double range, SoundEvent sound, SoundSource category, float volume, float pitch) {
        for (Player player : world.getEntitiesOfClass(Player.class, getCollisionBoxWithRange(vec, range))) {
            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).connection.send(new ClientboundCustomSoundPacket(sound.getRegistryName(), category, new Vec3(vec.x, vec.y, vec.z), volume, pitch));
            }
        }
    }

    public static void spawnParticleForAll(Level world, double range, ParticleOptions particleIn, boolean longDistanceIn, Vec3 posVc3d, Vec3 offsetVc3d, float speedIn, int countIn) {
        for (Player player : world.getEntitiesOfClass(Player.class, getCollisionBoxWithRange(posVc3d, range))) {
            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).connection.send(new ClientboundLevelParticlesPacket(particleIn, longDistanceIn, posVc3d.x, posVc3d.y, posVc3d.z, (float) offsetVc3d.x, (float) offsetVc3d.y, (float) offsetVc3d.z, speedIn, countIn));
            }
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

    public static void makeLaserLooking(Player player, double distance) {
        HitResult rtr = getPosLookingAt(player, distance);
        if (rtr != null && !player.level.isClientSide) {
            if (rtr.getType() == HitResult.Type.ENTITY) {
                EntityHitResult ertr = (EntityHitResult) rtr;
                if (ertr.getEntity() != null && ertr.getEntity() != player) {
                    ertr.getEntity().setSecondsOnFire(5);
                    ertr.getEntity().hurt(DamageSource.mobAttack(player), 2);
                }
            } else if (rtr.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = new BlockPos(rtr.getLocation());
                for (Direction dir : Direction.values()) {
                    BlockPos blockPos = new BlockPos(pos.relative(dir.getOpposite()));
                    if (player.level.isEmptyBlock(blockPos)) {
                        player.level.setBlock(blockPos, Blocks.FIRE.defaultBlockState(), 11);
                    }
                }
            }
        }
    }

    public static HitResult getPosLookingAt(Player player, double distance) {
        Vec3 startPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 endPos = player.position().add(0, player.getEyeHeight(), 0).add(player.getLookAngle().scale(distance));

        for (int i = 0; i < distance * 2; i++) {
            float scale = i / 2F;
            Vec3 pos = startPos.add(endPos.subtract(startPos).scale(scale / distance));
            BlockPos bpos = new BlockPos(pos);
            boolean block = !player.level.getBlockState(bpos).canOcclude() && player.level.getBlockState(bpos).getBlock() instanceof AbstractGlassBlock;
            if ((player.level.getBlockState(bpos).canOcclude() && !player.level.isEmptyBlock(bpos)) || block) {
                return new BlockHitResult(pos, null, bpos, false);
            } else {
                Vec3 min = pos.add(0.25F, 0.25F, 0.25F);
                Vec3 max = pos.add(-0.25F, -0.25F, -0.25F);
                for (Entity entity : player.level.getEntities(player, new AABB(min.x, min.y, min.z, max.x, max.y, max.z))) {
                    return new EntityHitResult(entity);
                }
            }
        }
        return null;
    }

    public static void setSuitForPlayer(Player player, Suit suit) {
        Item helmet = suit.getHelmet();
        Item chest = suit.getChestplate();
        Item legs = suit.getLegs();
        Item feet = suit.getBoots();
        if (helmet != null) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmet));
            } else player.addItem(new ItemStack(helmet));
            player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (chest != null) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
                player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
            } else player.addItem(new ItemStack(chest));
            player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (legs != null) {
            if (player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
                player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(legs));
            } else player.addItem(new ItemStack(legs));
            player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (feet != null) {
            if (player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
                player.setItemSlot(EquipmentSlot.FEET, new ItemStack(feet));
            } else player.addItem(new ItemStack(feet));
            player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}

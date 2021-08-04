package xyz.heroesunited.heroesunited.util;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

public class HUPlayerUtil {

    public static void sendMessage(PlayerEntity player, Text text) {
        player.sendSystemMessage(text, player.getUuid());
    }

    public static void sendStatusMessage(PlayerEntity player, Text text, boolean showInActionbar) {
        if (player.world.isClient) player.sendMessage(text, showInActionbar);
    }

    public static void playSoundToAll(World world, Vec3d vec, double range, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        for (PlayerEntity player : world.getNonSpectatingEntities(PlayerEntity.class, getCollisionBoxWithRange(vec, range))) {
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new PlaySoundIdS2CPacket(Registry.SOUND_EVENT.getId(sound), category, new Vec3d(vec.x, vec.y, vec.z), volume, pitch));
            }
        }
    }

    public static void spawnParticleForAll(World world, double range, ParticleEffect particleIn, boolean longDistanceIn, Vec3d posVc3d, Vec3d offsetVc3d, float speedIn, int countIn) {
        for (PlayerEntity player : world.getNonSpectatingEntities(PlayerEntity.class, getCollisionBoxWithRange(posVc3d, range))) {
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new ParticleS2CPacket(particleIn, longDistanceIn, posVc3d.x, posVc3d.y, posVc3d.z, (float) offsetVc3d.x, (float) offsetVc3d.y, (float) offsetVc3d.z, speedIn, countIn));
            }
        }
    }

    public static boolean haveSmallArms(Entity entity) {
        if (entity instanceof AbstractClientPlayerEntity) {
            return ((AbstractClientPlayerEntity) entity).getModel().equalsIgnoreCase("slim");
        }
        return false;
    }

    public static Box getCollisionBoxWithRange(Vec3d posVc3d, double range) {
        return new Box(new BlockPos(posVc3d.x - range, posVc3d.y - range, posVc3d.z - range), new BlockPos(posVc3d.x + range, posVc3d.y + range, posVc3d.z + range));
    }

    public static Vec3d getPlayerPos(PlayerEntity player) {
        return new Vec3d(player.getX(), player.getY(), player.getZ());
    }

    public static void makeLaserLooking(PlayerEntity player, double distance) {
        HitResult rtr = getPosLookingAt(player, distance);
        if (rtr != null && !player.world.isClient) {
            if (rtr.getType() == HitResult.Type.ENTITY) {
                EntityHitResult ertr = (EntityHitResult) rtr;
                if (ertr.getEntity() != null && ertr.getEntity() != player) {
                    ertr.getEntity().setOnFireFor(5);
                    ertr.getEntity().damage(DamageSource.mob(player), 2);
                }
            } else if (rtr.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = new BlockPos(rtr.getPos());
                for (Direction dir : Direction.values()) {
                    BlockPos blockPos = new BlockPos(pos.offset(dir.getOpposite()));
                    if (player.world.isAir(blockPos)) {
                        player.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState(), 11);
                    }
                }
            }
        }
    }

    public static HitResult getPosLookingAt(PlayerEntity player, double distance) {
        Vec3d startPos = player.getPos().add(0, player.getStandingEyeHeight(), 0);
        Vec3d endPos = player.getPos().add(0, player.getStandingEyeHeight(), 0).add(player.getRotationVector().multiply(distance));

        for (int i = 0; i < distance * 2; i++) {
            float scale = i / 2F;
            Vec3d pos = startPos.add(endPos.subtract(startPos).multiply(scale / distance));
            BlockPos bpos = new BlockPos(pos);
            boolean block = !player.world.getBlockState(bpos).isOpaque() && player.world.getBlockState(bpos).getBlock() instanceof AbstractGlassBlock;
            if ((player.world.getBlockState(bpos).isOpaque() && !player.world.isAir(bpos)) || block) {
                return new BlockHitResult(pos, null, bpos, false);
            } else {
                Vec3d min = pos.add(0.25F, 0.25F, 0.25F);
                Vec3d max = pos.add(-0.25F, -0.25F, -0.25F);
                for (Entity entity : player.world.getOtherEntities(player, new Box(min.x, min.y, min.z, max.x, max.y, max.z))) {
                    return new EntityHitResult(entity);
                }
            }
        }
        return null;
    }

    public static void setSuitForPlayer(PlayerEntity player, Suit suit) {
        Item helmet = suit.getHelmet();
        Item chest = suit.getChestplate();
        Item legs = suit.getLegs();
        Item feet = suit.getBoots();
        if (helmet != null) {
            if (player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                player.equipStack(EquipmentSlot.HEAD, new ItemStack(helmet));
            } else player.giveItemStack(new ItemStack(helmet));
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (chest != null) {
            if (player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
                player.equipStack(EquipmentSlot.CHEST, new ItemStack(chest));
            } else player.giveItemStack(new ItemStack(chest));
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (legs != null) {
            if (player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) {
                player.equipStack(EquipmentSlot.LEGS, new ItemStack(legs));
            } else player.giveItemStack(new ItemStack(legs));
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (feet != null) {
            if (player.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
                player.equipStack(EquipmentSlot.FEET, new ItemStack(feet));
            } else player.giveItemStack(new ItemStack(feet));
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}

package xyz.heroesunited.heroesunited.util;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import xyz.heroesunited.heroesunited.common.abilities.suit.Suit;

public class HUPlayerUtil {

    public static void sendMessage(PlayerEntity player, String text) {
        player.sendMessage(new StringTextComponent(text), player.getUniqueID());
    }

    public static void sendStatusMessage(PlayerEntity player, String text, boolean showInActionbar) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> player.sendStatusMessage(new StringTextComponent(text), showInActionbar));
    }

    public static void playSound(PlayerEntity player, Vector3d vec, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).connection.sendPacket(new SPlaySoundPacket(sound.getRegistryName(), category, new Vector3d(vec.x, vec.y, vec.z), volume, pitch));
        }
    }

    public static void playSoundToAll(World world, Vector3d vec, double range, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        for (PlayerEntity players : world.getEntitiesWithinAABB(PlayerEntity.class, getCollisionBoxWithRange(vec, range))) {
            playSound(players, vec, sound, category, volume, pitch);
        }
    }

    public static void spawnParticle(PlayerEntity player, IParticleData particleIn, boolean longDistanceIn, Vector3d posVc3d, Vector3d offsetVc3d, float speedIn, int countIn) {
        if (player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).connection.sendPacket(new SSpawnParticlePacket(particleIn, longDistanceIn, posVc3d.x, posVc3d.y, posVc3d.z, (float) offsetVc3d.x, (float) offsetVc3d.y, (float) offsetVc3d.z, speedIn, countIn));
        }
    }

    public static void spawnParticleForAll(World world, double range, IParticleData particleIn, boolean longDistanceIn, Vector3d posVc3d, Vector3d offsetVc3d, float speedIn, int countIn) {
        for (PlayerEntity players : world.getEntitiesWithinAABB(PlayerEntity.class, getCollisionBoxWithRange(posVc3d, range))) {
            spawnParticle(players, particleIn, longDistanceIn, posVc3d, offsetVc3d, speedIn, countIn);
        }
    }

    public static boolean haveSmallArms(Entity entity) {
        if (entity instanceof AbstractClientPlayerEntity)
            return ((AbstractClientPlayerEntity) entity).getSkinType().equalsIgnoreCase("slim");
        return false;
    }

    public static AxisAlignedBB getCollisionBoxWithRange(Vector3d posVc3d, double range){
        AxisAlignedBB a = new AxisAlignedBB(new BlockPos(posVc3d.x - range, posVc3d.y - range, posVc3d.z - range), new BlockPos(posVc3d.x + range, posVc3d.y + range, posVc3d.z + range));
        return a;
    }

    public static Vector3d getPlayerPos(PlayerEntity player){
        Vector3d vec = new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ());
        return vec;
    }

    public static void makeLaserLooking(PlayerEntity player, double distance) {
        RayTraceResult rtr = getPosLookingAt(player, distance);
        if (rtr != null && !player.world.isRemote) {
            if (rtr.getType() == RayTraceResult.Type.ENTITY) {
                EntityRayTraceResult ertr = (EntityRayTraceResult) rtr;
                if (ertr.getEntity() != null && ertr.getEntity() != player) {
                    ertr.getEntity().setFire(5);
                    ertr.getEntity().attackEntityFrom(DamageSource.causeMobDamage(player), 2);
                }
            } else if (rtr.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = new BlockPos(rtr.getHitVec());
                for (Direction dir : Direction.values()) {
                    BlockPos blockPos = new BlockPos(pos.add(dir.getDirectionVec()));
                    if (player.world.isAirBlock(blockPos)) {
                        player.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }

    public static RayTraceResult getPosLookingAt(PlayerEntity player, double distance) {
        Vector3d startPos = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        Vector3d endPos = player.getPositionVec().add(0, player.getEyeHeight(), 0).add(player.getLookVec().scale(distance));

        for (int i = 0; i < distance * 2; i++) {
            float scale = i / 2F;
            Vector3d pos = startPos.add(endPos.subtract(startPos).scale(scale / distance));
            BlockPos bpos = new BlockPos(pos);
            boolean block = !player.world.getBlockState(bpos).isSolid() && player.world.getBlockState(bpos).getBlock() instanceof AbstractGlassBlock;
            if ((player.world.getBlockState(bpos).isSolid() && !player.world.isAirBlock(bpos)) || block) {
                return new BlockRayTraceResult(pos, null, bpos, false);
            } else {
                Vector3d min = pos.add(0.25F, 0.25F, 0.25F);
                Vector3d max = pos.add(-0.25F, -0.25F, -0.25F);
                for (Entity entity : player.world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z))) {
                    return new EntityRayTraceResult(entity);
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
            if (player.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()) {
                player.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(helmet));
            } else player.addItemStackToInventory(new ItemStack(helmet));
            playSound(player, getPlayerPos(player), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (chest != null) {
            if (player.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()) {
                player.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(chest));
            } else player.addItemStackToInventory(new ItemStack(chest));
            playSound(player, getPlayerPos(player), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (legs != null) {
            if (player.getItemStackFromSlot(EquipmentSlotType.LEGS).isEmpty()) {
                player.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(legs));
            } else player.addItemStackToInventory(new ItemStack(legs));
            playSound(player, getPlayerPos(player), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (feet != null) {
            if (player.getItemStackFromSlot(EquipmentSlotType.FEET).isEmpty()) {
                player.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(feet));
            } else player.addItemStackToInventory(new ItemStack(feet));
            playSound(player, getPlayerPos(player), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}

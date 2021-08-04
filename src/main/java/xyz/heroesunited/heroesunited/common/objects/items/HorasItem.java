package xyz.heroesunited.heroesunited.common.objects.items;

import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;

import java.util.Objects;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class HorasItem extends HUItem {
    public HorasItem(Settings properties) {
        super(Items.EMERALD, properties);
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getStackInHand(handIn);
        HitResult rtr = raycast(worldIn, playerIn, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (rtr.getType() != HitResult.Type.BLOCK && !worldIn.isClient) {
            BlockPos pos = ((BlockHitResult) rtr).getBlockPos();
            if (worldIn.canPlayerModifyAt(playerIn, pos) && playerIn.canPlaceOn(pos, ((BlockHitResult) rtr).getSide(), itemstack)) {
                if (HUEntities.HORAS.spawnFromItemStack((ServerWorld) worldIn, itemstack, playerIn, pos, SpawnReason.SPAWN_EGG, false, false) == null) {
                    return TypedActionResult.pass(itemstack);
                } else {
                    itemstack.decrement(1);
                    playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
                    return TypedActionResult.consume(itemstack);
                }
            } else {
                return TypedActionResult.fail(itemstack);
            }
        } else {
            return TypedActionResult.success(itemstack);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ItemStack itemstack = context.getStack();
            BlockPos pos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockPos pos1 = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty() ? pos : pos.add(direction.getVector());
            if (HUEntities.HORAS.spawnFromItemStack((ServerWorld) world, itemstack, context.getPlayer(), pos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, pos1) && direction == Direction.UP) != null) {
                itemstack.decrement(1);
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }
}

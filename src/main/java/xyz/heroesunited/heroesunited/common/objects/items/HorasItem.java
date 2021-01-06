package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;

import java.util.Objects;

public class HorasItem extends Item {
    public HorasItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult rtr = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (rtr.getType() != RayTraceResult.Type.BLOCK && !worldIn.isRemote) {
            BlockPos pos = ((BlockRayTraceResult) rtr).getPos();
            if (worldIn.isBlockModifiable(playerIn, pos) && playerIn.canPlayerEdit(pos, ((BlockRayTraceResult) rtr).getFace(), itemstack)) {
                if (HUEntities.HORAS.spawn((ServerWorld) worldIn, itemstack, playerIn, pos, SpawnReason.SPAWN_EGG, false, false) == null) {
                    return ActionResult.resultPass(itemstack);
                } else {
                    itemstack.shrink(1);
                    playerIn.addStat(Stats.ITEM_USED.get(this));
                    return ActionResult.resultConsume(itemstack);
                }
            } else {
                return ActionResult.resultFail(itemstack);
            }
        } else {
            return ActionResult.resultSuccess(itemstack);
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            ItemStack itemstack = context.getItem();
            BlockPos pos = context.getPos();
            Direction direction = context.getFace();
            BlockPos pos1 = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty() ? pos : pos.offset(direction);
            if (HUEntities.HORAS.spawn((ServerWorld) world, itemstack, context.getPlayer(), pos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, pos1) && direction == Direction.UP) != null) {
                itemstack.shrink(1);
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}

package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
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

public class HorasItem extends HUItem {
    public HorasItem(Properties properties) {
        super(Items.EMERALD, properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        RayTraceResult rtr = getPlayerPOVHitResult(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (rtr.getType() != RayTraceResult.Type.BLOCK && !worldIn.isClientSide) {
            BlockPos pos = ((BlockRayTraceResult) rtr).getBlockPos();
            if (worldIn.mayInteract(playerIn, pos) && playerIn.mayUseItemAt(pos, ((BlockRayTraceResult) rtr).getDirection(), itemstack)) {
                if (HUEntities.HORAS.spawn((ServerWorld) worldIn, itemstack, playerIn, pos, SpawnReason.SPAWN_EGG, false, false) == null) {
                    return ActionResult.pass(itemstack);
                } else {
                    itemstack.shrink(1);
                    playerIn.awardStat(Stats.ITEM_USED.get(this));
                    return ActionResult.consume(itemstack);
                }
            } else {
                return ActionResult.fail(itemstack);
            }
        } else {
            return ActionResult.success(itemstack);
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!world.isClientSide) {
            ItemStack itemstack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockPos pos1 = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty() ? pos : pos.offset(direction.getNormal());
            if (HUEntities.HORAS.spawn((ServerWorld) world, itemstack, context.getPlayer(), pos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, pos1) && direction == Direction.UP) != null) {
                itemstack.shrink(1);
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}

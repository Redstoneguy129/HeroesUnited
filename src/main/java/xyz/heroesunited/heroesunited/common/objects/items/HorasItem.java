package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import xyz.heroesunited.heroesunited.common.objects.entities.HUEntities;

import java.util.Objects;

public class HorasItem extends HUItem {
    public HorasItem(Properties properties) {
        super(Items.EMERALD, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        HitResult rtr = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
        if (rtr.getType() != HitResult.Type.BLOCK && !worldIn.isClientSide) {
            BlockPos pos = ((BlockHitResult) rtr).getBlockPos();
            if (worldIn.mayInteract(playerIn, pos) && playerIn.mayUseItemAt(pos, ((BlockHitResult) rtr).getDirection(), itemstack)) {
                if (HUEntities.HORAS.spawn((ServerLevel) worldIn, itemstack, playerIn, pos, MobSpawnType.SPAWN_EGG, false, false) == null) {
                    return InteractionResultHolder.pass(itemstack);
                } else {
                    itemstack.shrink(1);
                    playerIn.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.consume(itemstack);
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.success(itemstack);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            ItemStack itemstack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockPos pos1 = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty() ? pos : pos.offset(direction.getNormal());
            if (HUEntities.HORAS.spawn((ServerLevel) world, itemstack, context.getPlayer(), pos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(pos, pos1) && direction == Direction.UP) != null) {
                itemstack.shrink(1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}

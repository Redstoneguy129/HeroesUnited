package xyz.heroesunited.heroesunited.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.common.events.HUCancelBlockCollision;

import java.util.Map;

@Mixin(WallBlock.class)
public abstract class MixinWallBlock {

    @Shadow @Final private Map<BlockState, VoxelShape> collisionShapeByIndex;

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "RETURN"), cancellable = true)
    public void onGetCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state != null && context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity().isPresent()) {
            Entity entity = ((EntityCollisionContext) context).getEntity().get();
            HUCancelBlockCollision event = new HUCancelBlockCollision(entity.level, pos, state, entity);
            MinecraftForge.EVENT_BUS.post(event);
            cir.setReturnValue(!event.isCanceled() ? this.collisionShapeByIndex.get(state) : Shapes.empty());
        }
    }

}

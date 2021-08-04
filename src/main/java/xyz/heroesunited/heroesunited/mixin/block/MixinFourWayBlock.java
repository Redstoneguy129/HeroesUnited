package xyz.heroesunited.heroesunited.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.common.events.HUCancelBlockCollision;

@Mixin(HorizontalConnectingBlock.class)
public abstract class MixinFourWayBlock {

    @Shadow
    protected abstract int getAABBIndex(BlockState state);

    @Shadow @Final protected VoxelShape[] collisionShapeByIndex;

    @Inject(method = "getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;", at = @At(value = "RETURN"), cancellable = true)
    public void onGetCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state != null && context != null && context.getEntity() != null) {
            HUCancelBlockCollision event = new HUCancelBlockCollision(context.getEntity().level, pos, state, context.getEntity());
            MinecraftForge.EVENT_BUS.post(event);
            cir.setReturnValue(!event.isCanceled() ? this.collisionShapeByIndex[this.getAABBIndex(state)] : VoxelShapes.empty());
        }

    }

}

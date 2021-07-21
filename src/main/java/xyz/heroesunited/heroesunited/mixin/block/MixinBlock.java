package xyz.heroesunited.heroesunited.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.heroesunited.heroesunited.common.events.HUCancelBlockCollision;

@Mixin(AbstractBlock.class)
public abstract class MixinBlock {

    @Shadow @Final protected boolean hasCollision;

    @Inject(method = "getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;", at = @At(value = "RETURN"), cancellable = true)
    public void onGetCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state != null && context != null && context.getEntity() != null) {
            HUCancelBlockCollision event = new HUCancelBlockCollision(context.getEntity().level, pos, state, context.getEntity());
            MinecraftForge.EVENT_BUS.post(event);
            cir.setReturnValue(!event.isCanceled() && this.hasCollision ? state.getShape(worldIn, pos) : VoxelShapes.empty());
        }
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;

public abstract class BasicLaserAbility extends JSONAbility {
    public BasicLaserAbility(AbilityType type, Player player, JsonObject jsonObject) {
        super(type, player, jsonObject);
    }

    @Override
    public void registerData() {
        super.registerData();
        this.dataManager.register("timer", 0);
        this.dataManager.register("prev_timer", 0);
        this.dataManager.register("maxTimer", 10, true);
        this.dataManager.register("distance", 20.0F, true);
        this.dataManager.register("strength", 0.0F, true);
    }

    @Override
    public void action(Player player) {
        super.action(player);
        int timer = this.dataManager.getAsInt("timer");
        this.dataManager.set("prev_timer", timer);
        if (getEnabled() && timer < this.dataManager.getAsInt("maxTimer")) {
            this.dataManager.set("timer", timer + 1);
        }
        if (timer >= this.dataManager.getAsInt("maxTimer")) {
            HitResult hitResult = HUPlayerUtil.getPosLookingAt(player, this.dataManager.getAsFloat("distance"));
            if (hitResult.getType() != HitResult.Type.MISS && !player.level.isClientSide) {
                if (hitResult instanceof EntityHitResult rtr && rtr.getEntity() != player) {
                    this.onHitEntity(rtr);
                } else if (hitResult instanceof BlockHitResult rtr) {
                    this.onHitBlock(rtr);
                }
            }
        }
        if (!getEnabled() && timer != 0) {
            this.dataManager.set("timer", timer - 1);
        }
    }

    public float getAlpha(float partialTicks) {
        return (this.dataManager.getAsInt("prev_timer") + (this.dataManager.getAsInt("timer") - this.dataManager.getAsInt("prev_timer")) * partialTicks) / this.dataManager.getAsInt("maxTimer");
    }

    protected void onHitEntity(EntityHitResult hitResult) {
        float strength = this.dataManager.<Float>getValue("strength");
        hitResult.getEntity().setSecondsOnFire((int) (strength * 5));
        hitResult.getEntity().hurt(DamageSource.mobAttack(player), strength * 2F);
    }

    protected void onHitBlock(BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        if (this.player.level.getBlockState(blockPos).getBlock() instanceof SandBlock) {
            this.player.level.setBlock(blockPos, Blocks.GLASS.defaultBlockState(), 11);
        } else {
            blockPos = blockPos.relative(hitResult.getDirection());
            if (this.player.level.isEmptyBlock(blockPos)) {
                this.player.level.setBlock(blockPos, Blocks.FIRE.defaultBlockState(), 11);
            }
        }
    }
}

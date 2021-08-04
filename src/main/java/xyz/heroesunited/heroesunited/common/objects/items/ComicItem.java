package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.gui.FiveYearsLaterBookGUI;

public class ComicItem extends Item {

    public ComicItem() {
        super(new Item.Settings().maxCount(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!playerIn.world.isClient) return TypedActionResult.pass(playerIn.getStackInHand(handIn));
        MinecraftClient.getInstance().setScreen(new FiveYearsLaterBookGUI());

        return super.use(worldIn, playerIn, handIn);
    }
}

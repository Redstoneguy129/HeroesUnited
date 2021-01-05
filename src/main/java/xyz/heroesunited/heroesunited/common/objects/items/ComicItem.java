package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.gui.FiveYearsLaterBookGUI;

public class ComicItem extends Item {

    public ComicItem() {
        super(new Item.Properties().maxStackSize(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!playerIn.world.isRemote) return ActionResult.resultPass(playerIn.getHeldItem(handIn));
        Minecraft.getInstance().displayGuiScreen(new FiveYearsLaterBookGUI());

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}

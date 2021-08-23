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

public class ComicItem extends HUItem {

    public ComicItem() {
        super(HUItems.HORAS, new Item.Properties().stacksTo(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!playerIn.level.isClientSide) return ActionResult.pass(playerIn.getItemInHand(handIn));
        Minecraft.getInstance().setScreen(new FiveYearsLaterBookGUI());

        return super.use(worldIn, playerIn, handIn);
    }
}

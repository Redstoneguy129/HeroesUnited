package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.gui.FiveYearsLaterBookGUI;

public class ComicItem extends HUItem {

    public ComicItem() {
        super(HUItems.HORAS, new Item.Properties().stacksTo(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!playerIn.level.isClientSide) return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        Minecraft.getInstance().setScreen(new FiveYearsLaterBookGUI());

        return super.use(worldIn, playerIn, handIn);
    }
}

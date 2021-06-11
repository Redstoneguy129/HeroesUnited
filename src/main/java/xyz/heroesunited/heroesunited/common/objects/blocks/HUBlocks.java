package xyz.heroesunited.heroesunited.common.objects.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.items.HUBlockItem;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

public class HUBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HeroesUnited.MODID);

    public static final Block TITANIUM = register("titanium", new Block(AbstractBlock.Properties.of(Material.METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(5.0F, 6.0F).sound(SoundType.METAL)), Items.DIAMOND_BLOCK, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS));
    public static final Block TITANIUM_ORE = register("titanium_ore", new OreBlock(AbstractBlock.Properties.of(Material.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(3.0F, 3.0F)), Items.DIAMOND_ORE, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS));

    private static Block register(String name, Block block, Item afterItem, Item.Properties properties) {
        BLOCKS.register(name, () -> block);
        HUItems.ITEMS.register(name, () -> new HUBlockItem(block, afterItem, properties));
        return block;
    }

}

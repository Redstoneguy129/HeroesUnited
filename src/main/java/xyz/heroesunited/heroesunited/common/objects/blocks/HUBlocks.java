package xyz.heroesunited.heroesunited.common.objects.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

public class HUBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HeroesUnited.MODID);

    public static final Block TITANIUM = register("titanium", new Block(AbstractBlock.Properties.of(Material.METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(5.0F, 6.0F).sound(SoundType.METAL)), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS));
    public static final Block TITANIUM_ORE = register("titanium_ore", new OreBlock(AbstractBlock.Properties.of(Material.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(3.0F, 3.0F)), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS));

    private static Block register(String name, Block block, Item.Properties item) {
        BLOCKS.register(name, () -> block);
        HUItems.ITEMS.register(name, () -> new BlockItem(block, item));
        return block;
    }

}

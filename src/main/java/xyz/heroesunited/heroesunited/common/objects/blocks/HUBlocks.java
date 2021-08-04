package xyz.heroesunited.heroesunited.common.objects.blocks;

import com.google.common.collect.Maps;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.OreBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.items.HUBlockItem;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

import java.util.Map;

public class HUBlocks {

    public static final Map<String, Block> BLOCKS = Maps.newHashMap();

    public static final Block TITANIUM = register("titanium", new Block(AbstractBlock.Settings.of(Material.METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(5.0F, 6.0F).sound(BlockSoundGroup.METAL)), Items.DIAMOND_BLOCK, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
    public static final Block TITANIUM_ORE = register("titanium_ore", new OreBlock(AbstractBlock.Settings.of(Material.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(3.0F, 3.0F)), Items.DIAMOND_ORE, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));

    private static Block register(String name, Block block, Item afterItem, Item.Settings properties) {
        HUBlocks.BLOCKS.put(name, block);
        HUItems.ITEMS.put(name, new HUBlockItem(block, afterItem, properties));
        return block;
    }

}

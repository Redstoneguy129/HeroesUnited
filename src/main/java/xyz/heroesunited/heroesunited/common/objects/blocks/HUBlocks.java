package xyz.heroesunited.heroesunited.common.objects.blocks;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.items.HUBlockItem;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

import java.util.function.Supplier;

public class HUBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HeroesUnited.MODID);

    public static final RegistryObject<Block> TITANIUM = register("titanium", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)), Items.DIAMOND_BLOCK, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
    public static final RegistryObject<OreBlock> TITANIUM_ORE = register("titanium_ore", () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)), Items.DIAMOND_ORE, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Item afterItem, Item.Properties properties) {
        RegistryObject<T> registryObject = HUBlocks.BLOCKS.register(name, block);
        HUItems.ITEMS.register(name, () -> new HUBlockItem(registryObject.get(), afterItem, properties));
        return registryObject;
    }

}

package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HeroesUnited.MODID);

    public static final Item TITANIUM_INGOT = register("titanium_ingot", new Item(new Item.Properties().group(ItemGroup.MATERIALS)));
    public static final Item HEROES_UNITED = register("heroes_united", new Item(new Item.Properties().maxStackSize(1)));
    public static final Item HORAS = register("horas", new HorasItem(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC)));
    public static final ComicItem COMIC_ITEM = registerSpecial("comic", new ComicItem());

    private static <T extends Item> T register(String name, T item) {
        ITEMS.register(name, () -> item);
        return item;
    }

    private static <T extends Item> T registerSpecial(String name, T item) {
        if (FMLEnvironment.production || ModList.get().getMods().stream().filter(modInfo -> modInfo.getModId().equals("huben10") || modInfo.getModId().equals("hugeneratorrex") || modInfo.getModId().equals("hudannyphantom")).count() >= 3) {
            return register(name, item);
        } else {
            return null;
        }
    }
}

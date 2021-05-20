package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

public class HUItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HeroesUnited.MODID);

    public static final Item TITANIUM_INGOT = register("titanium_ingot", new Item(new Item.Properties().tab(ItemGroup.TAB_MATERIALS)));
    public static final Item HEROES_UNITED = register("heroes_united", new Item(new Item.Properties().stacksTo(1)));
    public static final Item HORAS = register("horas", new HorasItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));
    public static final ComicItem COMIC_ITEM = registerSpecial("comic", new ComicItem());

    public static final TheOneRingAccessory THE_ONE_RING_ACCESSORY = register("the_one_ring", new TheOneRingAccessory());
    public static final GeckoAccessory ARC_REACTOR_ACCESSORY = register("arc_reactor", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "CandyFreak"));
    public static final GeckoAccessory BOBO_ACCESSORY = register("bobo", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Chappie"));
    public static final GeckoAccessory HEADBAND = register("headband", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "FatherKhimsky"));
    public static final GeckoAccessory WALLE_HEAD = register("walle_head", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Wilbert"));
    public static final GeckoAccessory KEY_VECTOR_SIGMA = register("key_vector_sigma", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "TimeVortex_TV"));
    public static final GeckoAccessory FLASH_RING = register("flash_ring", new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "Mike Wazowski, yes he lmao xD"));
    public static final GeckoAccessory KEYBLADE = register("keyblade", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "FalloutWolfGod"));
    public static final GeckoAccessory GREEN_GOGGLES = register("green_goggles", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "artman") {
        @Override
        public ResourceLocation getModelFile() {
            return new ResourceLocation(HeroesUnited.MODID, "geo/rex_glasses.geo.json");
        }
    });
    public static final GeckoAccessory CAP_SHIELD_ACCESSORY = register("cap_shield", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "El Dunchess"));

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

package xyz.heroesunited.heroesunited.common.objects.items;

import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.Map;

public class HUItems {

    public static final Map<String, Item> ITEMS = Maps.newHashMap();

    public static final Item TITANIUM_INGOT = register("titanium_ingot", new HUItem(Items.NETHERITE_INGOT, new Item.Settings().group(ItemGroup.MATERIALS)));
    public static final Item HEROES_UNITED = register("heroes_united", new Item(new Item.Settings().maxCount(1)));
    public static final Item HORAS = register("horas", new HorasItem(new Item.Settings().maxCount(1).group(ItemGroup.MISC)));
    public static final ComicItem COMIC_ITEM = registerSpecial("comic", new ComicItem());

    public static final TheOneRingAccessory THE_ONE_RING_ACCESSORY = register("the_one_ring", new TheOneRingAccessory());
    public static final BoBoAccessory BOBO_ACCESSORY = register("bobo", new BoBoAccessory());
    public static final GeckoAccessory ARC_REACTOR_ACCESSORY = register("arc_reactor", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "CandyFreak"));
    public static final GeckoAccessory HEADBAND = register("headband", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "FatherKhimsky"));
    public static final GeckoAccessory WALLE_HEAD = register("walle_head", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Wilbert"));
    public static final GeckoAccessory KEY_VECTOR_SIGMA = register("key_vector_sigma", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "TimeVortex_TV"));
    public static final GeckoAccessory FLASH_RING = register("flash_ring", new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "Mike Wazowski, yes he lmao xD"));
    public static final GeckoAccessory KEYBLADE = register("keyblade", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "FalloutWolfGod"));
    public static final GeckoAccessory SMALLGILLY = register("smallgilly", new GeckoAccessory(EquipmentAccessoriesSlot.GLOVES, "Gillygogs"));
    public static final GeckoAccessory JASON_MASK = register("jason_mask", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "FalloutWolfGod"));
    public static final DefaultAccessoryItem REDA_SHIRT = register("simp_tshirt", new DefaultAccessoryItem(EquipmentAccessoriesSlot.TSHIRT, "Reda"));
    public static final GeckoAccessory FINN_ARM = register("finn_arm", new GeckoAccessory(EquipmentAccessoriesSlot.RIGHT_WRIST, "Mattetull"));
    public static final GeckoAccessory NITRO_JETPACK = register("nitro_jetpack", new GeckoAccessory(EquipmentAccessoriesSlot.JACKET, "PenGuiN41K"));
    public static final GeckoAccessory MACHETE = register("machete", new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "FalloutWolfGod"));
    public static final GeckoAccessory LARA_CROFT = register("lara_croft", new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "CandyFreak"));
    public static final GeckoAccessory ZEK_GLASSES = register("zek_glasses", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Zekeram12"));
    public static final GeckoAccessory CAPTAIN_REX = register("captain_rex", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Zekeram12"));
    public static final GeckoAccessory CAP_SHIELD_ACCESSORY = register("cap_shield", new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "El Dunchess"));
    public static final GeckoAccessory GREEN_GOGGLES = register("green_goggles", new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "artman") {
        @Override
        public Identifier getModelFile() {
            return new Identifier(HeroesUnited.MODID, "geo/rex_glasses.geo.json");
        }
    });

    private static <T extends Item> T register(String name, T item) {
        ITEMS.put(name, item);
        return item;
    }

    private static <T extends Item> T registerSpecial(String name, T item) {
        if (FabricLauncherBase.getLauncher().isDevelopment() || FabricLoader.getInstance().getAllMods().stream().filter(modInfo -> modInfo.getMetadata().getId().equals("huben10") || modInfo.getMetadata().getId().equals("hugeneratorrex") || modInfo.getMetadata().getId().equals("hudannyphantom")).count() >= 3) {
            return register(name, item);
        } else {
            return null;
        }
    }
}

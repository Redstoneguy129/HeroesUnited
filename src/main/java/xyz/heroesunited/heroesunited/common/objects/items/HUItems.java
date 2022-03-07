package xyz.heroesunited.heroesunited.common.objects.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class HUItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HeroesUnited.MODID);

    public static final RegistryObject<HUItem> TITANIUM_INGOT = ITEMS.register("titanium_ingot", () -> new HUItem(Items.NETHERITE_INGOT, new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    public static final RegistryObject<Item> HEROES_UNITED = ITEMS.register("heroes_united", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<HorasItem> HORAS = ITEMS.register("horas", () -> new HorasItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<ComicItem> COMIC_ITEM = registerSpecial(ComicItem::new);

    public static final RegistryObject<TheOneRingAccessory> THE_ONE_RING_ACCESSORY = ITEMS.register("the_one_ring", TheOneRingAccessory::new);
    public static final RegistryObject<BoBoAccessory> BOBO_ACCESSORY = ITEMS.register("bobo", BoBoAccessory::new);

    public static final RegistryObject<JasonAccessory> JASON_JACKET = ITEMS.register("jason_jacket", () -> new JasonAccessory(EquipmentAccessoriesSlot.JACKET));
    public static final RegistryObject<JasonAccessory> JASON_SHIRT = ITEMS.register("jason_shirt", () -> new JasonAccessory(EquipmentAccessoriesSlot.TSHIRT));
    public static final RegistryObject<JasonAccessory> JASON_PANTS = ITEMS.register("jason_pants", () -> new JasonAccessory(EquipmentAccessoriesSlot.PANTS));

    public static final RegistryObject<DefaultAccessoryItem> GREEN_SHIRT = ITEMS.register("green_tshirt", () -> new DefaultAccessoryItem(new Item.Properties(), EquipmentAccessoriesSlot.TSHIRT, "Blazefire"));
    public static final RegistryObject<DefaultAccessoryItem> RED_JACKET = ITEMS.register("red_jacket", () -> new DefaultAccessoryItem(new Item.Properties(), EquipmentAccessoriesSlot.JACKET, "Blazefire"));
    public static final RegistryObject<DefaultAccessoryItem> REDA_SHIRT = ITEMS.register("simp_tshirt", () -> new DefaultAccessoryItem(new Item.Properties(), EquipmentAccessoriesSlot.TSHIRT, "Reda"));
    public static final RegistryObject<DefaultAccessoryItem> REDA_JACKET = ITEMS.register("umbrella_academy", () -> new DefaultAccessoryItem(new Item.Properties(), EquipmentAccessoriesSlot.JACKET, "Reda"));

    public static final RegistryObject<GeckoAccessory> ARC_REACTOR_ACCESSORY = ITEMS.register("arc_reactor", () -> new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "CandyFreak"));
    public static final RegistryObject<GeckoAccessory> HEADBAND = ITEMS.register("headband", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "FatherKhimsky"));
    public static final RegistryObject<GeckoAccessory> WALLE_HEAD = ITEMS.register("walle_head", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Wilbert"));
    public static final RegistryObject<GeckoAccessory> KEY_VECTOR_SIGMA = ITEMS.register("key_vector_sigma", () -> new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "TimeVortex_TV"));
    public static final RegistryObject<GeckoAccessory> FLASH_RING = ITEMS.register("flash_ring", () -> new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "Mike Wazowski, yes he lmao xD"));
    public static final RegistryObject<GeckoAccessory> KEYBLADE = ITEMS.register("keyblade", () -> new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "FalloutWolfGod"));
    public static final RegistryObject<GeckoAccessory> SMALLGILLY = ITEMS.register("smallgilly", () -> new GeckoAccessory(EquipmentAccessoriesSlot.GLOVES, "Gillygogs"));
    public static final RegistryObject<GeckoAccessory> JASON_MASK = ITEMS.register("jason_mask", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "FalloutWolfGod"));
    public static final RegistryObject<GeckoAccessory> FINN_ARM = ITEMS.register("finn_arm", () -> new GeckoAccessory(EquipmentAccessoriesSlot.RIGHT_WRIST, "Mattetull"));
    public static final RegistryObject<GeckoAccessory> NITRO_JETPACK = ITEMS.register("nitro_jetpack", () -> new GeckoAccessory(EquipmentAccessoriesSlot.JACKET, "PenGuiN41K"));
    public static final RegistryObject<GeckoAccessory> MACHETE = ITEMS.register("machete", () -> new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "FalloutWolfGod"));
    public static final RegistryObject<GeckoAccessory> LARA_CROFT = ITEMS.register("lara_croft", () -> new GeckoAccessory(EquipmentAccessoriesSlot.WRIST, "CandyFreak"));
    public static final RegistryObject<GeckoAccessory> ZEK_GLASSES = ITEMS.register("zek_glasses", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Zekeram12"));
    public static final RegistryObject<GeckoAccessory> PERRY_THE_PLATYPUS_HAT = ITEMS.register("perry_the_platypus_hat", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Mr Ali S"));
    public static final RegistryObject<GeckoAccessory> CAPTAIN_REX = ITEMS.register("captain_rex", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Zekeram12"));
    public static final RegistryObject<GeckoAccessory> PERRY_TAIL = ITEMS.register("perry_tail", () -> new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "Mr Ali S"));
    public static final RegistryObject<GeckoAccessory> MADNESSCOMBAT = ITEMS.register("madnesscombat", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "Mattetull"));
    public static final RegistryObject<GeckoAccessory> AKIRA_JACKET = ITEMS.register("akira_jacket", () -> new GeckoAccessory(EquipmentAccessoriesSlot.JACKET, "ThatOneGuy"));
    public static final RegistryObject<GeckoAccessory> MADNESSCLAW = ITEMS.register("madnessclaw", () -> new GeckoAccessory(EquipmentAccessoriesSlot.RIGHT_WRIST, "Mattetull"));
    public static final RegistryObject<GeckoAccessory> CAP_SHIELD_ACCESSORY = ITEMS.register("cap_shield", () -> new GeckoAccessory(EquipmentAccessoriesSlot.TSHIRT, "El Dunchess"));
    public static final RegistryObject<GeckoAccessory> GREEN_GOGGLES = ITEMS.register("green_goggles", () -> new GeckoAccessory(EquipmentAccessoriesSlot.HELMET, "artman"));
    public static final RegistryObject<GeckoAccessory> HOKAGE_CAPE = ITEMS.register("hokage_cape", () -> new GeckoAccessory(EquipmentAccessoriesSlot.JACKET, "Master Ern"));

    private static <T extends Item> RegistryObject<T> registerSpecial(Supplier<T> item) {
        if (FMLEnvironment.production || ModList.get().getMods().stream().filter(modInfo -> modInfo.getModId().equals("huben10") || modInfo.getModId().equals("hugeneratorrex") || modInfo.getModId().equals("hudannyphantom")).count() >= 3) {
            return ITEMS.register("comic", item);
        } else {
            return null;
        }
    }
}

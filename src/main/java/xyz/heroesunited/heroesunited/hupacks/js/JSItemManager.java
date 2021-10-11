package xyz.heroesunited.heroesunited.hupacks.js;

import com.google.common.collect.Maps;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.hupacks.js.item.*;

import javax.script.Invocable;
import java.util.*;
import java.util.function.Function;

public class JSItemManager extends JSReloadListener {

    private static final Map<ResourceLocation, Function<Map.Entry<JSItemProperties, NashornScriptEngine>, Item>> types = Maps.newHashMap();
    private final List<Item> items = new ArrayList<>();

    public JSItemManager(IEventBus bus) {
        super("huitems", new NashornScriptEngineFactory());
        bus.addGenericListener(Item.class, this::registerItems);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "default"), JSItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "sword"), JSSwordItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "pickaxe"), JSPickaxeItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "axe"), JSAxeItem::new);
    }

    public static void registerItemType(ResourceLocation resourceLocation, Function<Map.Entry<JSItemProperties, NashornScriptEngine>, Item> function) {
        types.put(Objects.requireNonNull(resourceLocation), Objects.requireNonNull(function));
    }

    @Override
    public void apply(Map<ResourceLocation, NashornScriptEngine> map, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        for (Map.Entry<ResourceLocation, NashornScriptEngine> entry : map.entrySet()) {
            try {
                JSItemProperties properties = new JSItemProperties();
                ((Invocable) entry.getValue()).invokeFunction("init", properties);
                items.add(types.get(new ResourceLocation(properties.type)).apply(new AbstractMap.SimpleEntry<>(properties, entry.getValue())).setRegistryName(entry.getKey()));
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack item {}", entry.getKey(), throwable);
            }
        }
    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : items) {
            event.getRegistry().register(item);
        }
    }
}

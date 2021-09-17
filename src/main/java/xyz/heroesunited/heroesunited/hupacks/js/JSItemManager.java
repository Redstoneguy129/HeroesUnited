package xyz.heroesunited.heroesunited.hupacks.js;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.hupacks.js.item.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class JSItemManager extends JSReloadListener {

    private static final Map<ResourceLocation, Function<Map.Entry<JSItemProperties, ScriptEngine>, Item>> types = Maps.newHashMap();
    private final List<Item> items = Lists.newArrayList();

    public JSItemManager(IEventBus bus) {
        super("huitems", new ScriptEngineManager());
        bus.addGenericListener(Item.class, this::registerItems);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "default"), JSItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "sword"), JSSwordItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "pickaxe"), JSPickaxeItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "axe"), JSAxeItem::new);
    }

    public static void registerItemType(ResourceLocation resourceLocation, Function<Map.Entry<JSItemProperties, ScriptEngine>, Item> function) {
        types.put(Objects.requireNonNull(resourceLocation), Objects.requireNonNull(function));
    }

    @Override
    protected void apply(Map<ResourceLocation, ScriptEngine> map, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        for (Map.Entry<ResourceLocation, ScriptEngine> entry : map.entrySet()) {
            try {
                JSItemProperties properties = new JSItemProperties();
                ((Invocable) entry.getValue()).invokeFunction("init", properties);
                items.add(types.get(new ResourceLocation(properties.type)).apply(new AbstractMap.SimpleEntry<>(properties, entry.getValue())).setRegistryName(entry.getKey()));
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : items) {
            event.getRegistry().register(item);
        }
    }
}

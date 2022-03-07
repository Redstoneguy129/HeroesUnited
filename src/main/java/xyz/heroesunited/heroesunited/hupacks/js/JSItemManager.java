package xyz.heroesunited.heroesunited.hupacks.js;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.hupacks.js.item.*;

import javax.script.Invocable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class JSItemManager extends JSReloadListener {

    private static final Map<ResourceLocation, Function<Map.Entry<JSItemProperties, NashornScriptEngine>, Item>> TYPES = Maps.newHashMap();
    private final Map<ResourceLocation, Map.Entry<JSItemProperties, NashornScriptEngine>> items = Maps.newHashMap();

    public JSItemManager() {
        super("huitems", new NashornScriptEngineFactory());
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "default"), JSItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "sword"), JSSwordItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "pickaxe"), JSPickaxeItem::new);
        registerItemType(new ResourceLocation(HeroesUnited.MODID, "axe"), JSAxeItem::new);
    }

    public static void registerItemType(ResourceLocation resourceLocation, Function<Map.Entry<JSItemProperties, NashornScriptEngine>, Item> function) {
        TYPES.put(Objects.requireNonNull(resourceLocation), Objects.requireNonNull(function));
    }

    @Override
    public void apply(Map<ResourceLocation, NashornScriptEngine> map, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        for (Map.Entry<ResourceLocation, NashornScriptEngine> entry : map.entrySet()) {
            try {
                JSItemProperties properties = new JSItemProperties();
                ((Invocable) entry.getValue()).invokeFunction("init", properties);
                items.put(entry.getKey(), new AbstractMap.SimpleEntry<>(properties, entry.getValue()));
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack item {}", entry.getKey(), throwable);
            }
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        for (var e : items.entrySet()) {
            event.getRegistry().register(TYPES.get(e.getValue().getKey().type).apply(e.getValue()).setRegistryName(e.getKey()));
        }
    }
}

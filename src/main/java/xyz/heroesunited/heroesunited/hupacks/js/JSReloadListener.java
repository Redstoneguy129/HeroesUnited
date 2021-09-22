package xyz.heroesunited.heroesunited.hupacks.js;

import com.google.common.collect.Maps;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.hupacks.HUPacks;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class JSReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, NashornScriptEngine>> {
    protected final NashornScriptEngineFactory manager;
    private final String directory;

    public JSReloadListener(String directory, NashornScriptEngineFactory manager) {
        this.manager = manager;
        this.directory = directory;
    }

    public static <T extends JSReloadListener> void init(T manager) {
        manager.apply(manager.prepare(HUPacks.getInstance().getResourceManager(), InactiveProfiler.INSTANCE), HUPacks.getInstance().getResourceManager(), InactiveProfiler.INSTANCE);
    }

    @Override
    public Map<ResourceLocation, NashornScriptEngine> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, NashornScriptEngine> map = Maps.newHashMap();
        for (ResourceLocation resourcelocation : manager.listResources(this.directory, (s) -> s.endsWith(".js"))) {
            String s = resourcelocation.getPath();
            ResourceLocation location = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.directory.length() + 1, s.length() - ".js".length()));

            try (
                    InputStream inputstream = manager.getResource(resourcelocation).getInputStream();
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
            ) {
                NashornScriptEngine engine = (NashornScriptEngine) this.manager.getScriptEngine();
                engine.put("path", location.toString());
                engine.eval(IOUtils.toString(reader));
                map.put(location, engine);
            } catch (IOException | ScriptException jsonparseexception) {
                HeroesUnited.LOGGER.error("Couldn't parse data file {} from {}", location, resourcelocation, jsonparseexception);
            }
        }

        return map;
    }
}

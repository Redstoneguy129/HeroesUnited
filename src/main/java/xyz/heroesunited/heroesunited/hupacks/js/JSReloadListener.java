package xyz.heroesunited.heroesunited.hupacks.js;

import com.google.common.collect.Maps;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;

import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class JSReloadListener extends ReloadListener<Map<ResourceLocation, NashornScriptEngine>> {
    protected final NashornScriptEngineFactory manager;
    private final String directory;

    public JSReloadListener(String directory, NashornScriptEngineFactory manager) {
        this.manager = manager;
        this.directory = directory;
    }

    @Override
    public Map<ResourceLocation, NashornScriptEngine> prepare(IResourceManager manager, IProfiler profiler) {
        Map<ResourceLocation, NashornScriptEngine> map = Maps.newHashMap();
        for (ResourceLocation resourcelocation : manager.listResources(this.directory, (s) -> s.endsWith(".js"))) {
            String s = resourcelocation.getPath();
            ResourceLocation location = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.directory.length() + 1, s.length() - ".js".length()));

            try (
                    InputStream inputstream = manager.getResource(resourcelocation).getInputStream();
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8))
            ) {
                NashornScriptEngine engine = (NashornScriptEngine) this.manager.getScriptEngine();
                engine.put("path", location.toString());
                engine.eval(reader);
                map.put(location, engine);
            } catch (IOException | ScriptException jsonparseexception) {
                HeroesUnited.LOGGER.error("Couldn't parse data file {} from {}", location, resourcelocation, jsonparseexception);
            }
        }

        return map;
    }
}

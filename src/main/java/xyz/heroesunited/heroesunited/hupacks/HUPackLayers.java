package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.Map;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

public class HUPackLayers extends JsonDataLoader {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HUPackLayers INSTANCE;
    private Map<Identifier, Layer> registeredLayers = Maps.newHashMap();

    public HUPackLayers() {
        super(GSON, "models/layers");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager iResourceManager, Profiler iProfiler) {
        for (Map.Entry<Identifier, JsonElement> entry : map.entrySet()) {
            Identifier resourcelocation = entry.getKey();
            try {
                Layer layer = parseLayer(resourcelocation, (JsonObject) entry.getValue());
                if (layer != null) {
                    this.registeredLayers.put(resourcelocation, layer);
                }
            } catch (Exception e) {
                HeroesUnited.LOGGER.error("Parsing error loading layer {}", resourcelocation, e);
            }
        }
        HeroesUnited.LOGGER.info("Loaded {} layer", this.registeredLayers.size());
    }

    public Layer parseLayer(Identifier name, JsonObject json) {
        JsonObject resources = JsonHelper.getObject(json, "resources");
        Map<String, Identifier> list = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> e : resources.entrySet()) {
            list.put(e.getKey(), new Identifier(e.getValue().getAsString()));
        }
        return new Layer(name, list, json);
    }

    public Map<Identifier, Layer> getLayers() {
        return this.registeredLayers;
    }

    public Layer getLayer(Identifier name) {
        return this.registeredLayers.get(name);
    }

    public static HUPackLayers getInstance() {
        return INSTANCE;
    }

    public static class Layer {

        private final Identifier name;
        private final Map<String, Identifier> list;
        private final JsonObject jsonObject;

        public Layer(Identifier name, Map<String, Identifier> list, JsonObject jsonObject) {
            this.name = name;
            this.list = list;
            this.jsonObject = jsonObject;
        }

        public Identifier getRegistryName() {
            return name;
        }

        public Identifier getTexture(String name) {
            return list.get(name);
        }

        public JsonObject getJsonObject() {
            return jsonObject;
        }
    }
}

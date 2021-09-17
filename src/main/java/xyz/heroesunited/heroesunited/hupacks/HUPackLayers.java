package xyz.heroesunited.heroesunited.hupacks;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;

import java.util.Map;

public class HUPackLayers extends JsonReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HUPackLayers INSTANCE;
    private final Map<ResourceLocation, Layer> registeredLayers = Maps.newHashMap();

    public HUPackLayers() {
        super(GSON, "models/layers");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager iResourceManager, IProfiler iProfiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
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

    public Layer parseLayer(ResourceLocation name, JsonObject json) {
        JsonObject resources = JSONUtils.getAsJsonObject(json, "resources");
        Map<String, ResourceLocation> list = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> e : resources.entrySet()) {
            list.put(e.getKey(), new ResourceLocation(e.getValue().getAsString()));
        }
        return new Layer(name, list, json);
    }

    public Map<ResourceLocation, Layer> getLayers() {
        return this.registeredLayers;
    }

    public Layer getLayer(ResourceLocation name) {
        return this.registeredLayers.get(name);
    }

    public static HUPackLayers getInstance() {
        return INSTANCE;
    }

    public static class Layer {

        private final ResourceLocation name;
        private final Map<String, ResourceLocation> list;
        private final JsonObject jsonObject;

        public Layer(ResourceLocation name, Map<String, ResourceLocation> list, JsonObject jsonObject) {
            this.name = name;
            this.list = list;
            this.jsonObject = jsonObject;
        }

        public ResourceLocation getRegistryName() {
            return name;
        }

        public ResourceLocation getTexture(String name) {
            return list.get(name);
        }

        public JsonObject getJsonObject() {
            return jsonObject;
        }
    }
}

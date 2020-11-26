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
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HUPackLayers INSTANCE;
    private Map<ResourceLocation, Layer> registeredLayers = Maps.newHashMap();

    public HUPackLayers() {
        super(GSON, "models/heroes");
        INSTANCE = this;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager iResourceManager, IProfiler iProfiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            try {
                Layer layer = parseLayer(resourcelocation, (JsonObject) entry.getValue());
                if (layer != null) this.registeredLayers.put(resourcelocation, layer);
            } catch (Exception e) {
                HeroesUnited.getLogger().error("Parsing error loading layer {}", resourcelocation, e);
            }
        }
        HeroesUnited.getLogger().info("Loaded {} layer", this.registeredLayers.size());
    }

    public Layer parseLayer(ResourceLocation name, JsonObject json) {
        JsonObject resources = JSONUtils.getJsonObject(json, "resources");
        ResourceLocation layer1 = new ResourceLocation(JSONUtils.getString(resources, "layer_0"));
        ResourceLocation layer2 = new ResourceLocation(JSONUtils.getString(resources, "layer_1"));
        ResourceLocation smallArms = new ResourceLocation(JSONUtils.getString(resources, "smallArms"));
        return new Layer(name, layer1, layer2, smallArms);
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
        private final ResourceLocation layer0;
        private final ResourceLocation layer1;
        private final ResourceLocation smallArms;

        public Layer(ResourceLocation name, ResourceLocation layer0, ResourceLocation layer1, ResourceLocation smallArms) {
            this.name = name;
            this.layer0 = layer0;
            this.layer1 = layer1;
            this.smallArms = smallArms;
        }

        public ResourceLocation getRegistryName() {
            return name;
        }

        public ResourceLocation getLayer0() {
            return layer0;
        }

        public ResourceLocation getSmallArms() {
            return smallArms;
        }

        public ResourceLocation getLayer1() {
            return layer1;
        }
    }
}

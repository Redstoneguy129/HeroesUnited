package xyz.heroesunited.heroesunited.client.events;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

/**
 * This event is called when skin/cape/elytra/skin model type want to be changed
 * Can be used to change skin/cape/elytra/model type (slim, default)
 */
public class SkinChangeEvent extends Event {

    private final Map<MinecraftProfileTexture.Type, ResourceLocation> defaultTextures,
            textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);

    private final GameProfile gameProfile;
    private final String defaultModel;
    private String skinModel;

    public SkinChangeEvent(GameProfile gameProfile, Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations, String model) {
        this.gameProfile = gameProfile;
        this.defaultTextures = textureLocations;
        this.defaultModel = model;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public void setTextureFor(MinecraftProfileTexture.Type type, ResourceLocation location) {
        this.textures.put(type, location);
    }

    public ResourceLocation getTextureFor(MinecraftProfileTexture.Type type) {
        return this.textures.get(type);
    }

    public ResourceLocation getDefaultTextureFor(MinecraftProfileTexture.Type type) {
        return this.defaultTextures.get(type);
    }

    public Map<MinecraftProfileTexture.Type, ResourceLocation> getTextures() {
        return textures;
    }

    public void setSkinModel(String skinModel) {
        this.skinModel = skinModel;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public String getSkinModel() {
        return skinModel;
    }
}

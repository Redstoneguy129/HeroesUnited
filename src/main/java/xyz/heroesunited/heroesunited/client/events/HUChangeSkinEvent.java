package xyz.heroesunited.heroesunited.client.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is called when skin want be changed
 * Can be used to change skin/skin type (slim, default)
 */
public class HUChangeSkinEvent extends Event {

    private final String defaultModel;
    private String skinModel;
    private final ResourceLocation defaultSkin;
    private ResourceLocation skin;
    private final GameProfile gameProfile;

    public HUChangeSkinEvent(String model, ResourceLocation skin, GameProfile gameProfile) {
        this.defaultModel = model;
        this.defaultSkin = skin;
        this.gameProfile = gameProfile;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public ResourceLocation getSkin() {
        return skin == null ? defaultSkin : skin;
    }

    public ResourceLocation getDefaultSkin() {
        return defaultSkin;
    }

    public void setSkin(ResourceLocation skin) {
        this.skin = skin;
    }

    public void setSkinModel(String skinModel) {
        this.skinModel = skinModel;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public String getSkinModel() {
        return skinModel == null ? defaultModel : skinModel;
    }
}

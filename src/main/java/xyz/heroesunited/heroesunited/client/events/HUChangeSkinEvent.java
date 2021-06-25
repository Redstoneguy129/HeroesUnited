package xyz.heroesunited.heroesunited.client.events;

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

    public HUChangeSkinEvent(String model, ResourceLocation skin) {
        this.defaultModel = model;
        this.defaultSkin = skin;
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

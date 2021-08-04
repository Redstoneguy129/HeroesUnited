package xyz.heroesunited.heroesunited.client.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is called when skin want be changed
 * Can be used to change skin/skin type (slim, default)
 */
public class HUChangeSkinEvent extends Event {

    private final String defaultModel;
    private String skinModel;
    private final Identifier defaultSkin;
    private Identifier skin;
    private final GameProfile gameProfile;

    public HUChangeSkinEvent(String model, Identifier skin, GameProfile gameProfile) {
        this.defaultModel = model;
        this.defaultSkin = skin;
        this.gameProfile = gameProfile;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public Identifier getSkin() {
        return skin == null ? defaultSkin : skin;
    }

    public Identifier getDefaultSkin() {
        return defaultSkin;
    }

    public void setSkin(Identifier skin) {
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

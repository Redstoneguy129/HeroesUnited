package xyz.heroesunited.heroesunited.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import xyz.heroesunited.heroesunited.client.ClientEventHandler;

import java.util.HashMap;

public class KeyMap extends HashMap<Integer, Boolean> {

    public KeyMap() {
        for (int i = 1; i < 10; i++) {
            this.put(i, false);
        }
    }

    public KeyMapping getKeyMapping(int i) {
        if (i < 6) {
            return ClientEventHandler.ABILITY_KEYS.get(i-1);
        } else if (i == 6) {
            return Minecraft.getInstance().options.keyPickItem;
        } else if (i == 7) {
            return Minecraft.getInstance().options.keyJump;
        } else if (i == 8) {
            return Minecraft.getInstance().options.keyAttack;
        } else {
            return Minecraft.getInstance().options.keyUse;
        }
    }

    @Override
    public Boolean get(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return super.get(key);
    }
}

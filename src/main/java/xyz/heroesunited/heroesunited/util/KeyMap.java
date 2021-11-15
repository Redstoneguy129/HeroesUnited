package xyz.heroesunited.heroesunited.util;

import java.util.HashMap;

public class KeyMap extends HashMap<Integer, Boolean> {

    @Override
    public Boolean get(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return super.get(key);
    }

    public enum HUKeys {
        ABILITY_1(1),
        ABILITY_2(2),
        ABILITY_3(3),
        ABILITY_4(4),
        ABILITY_5(5),
        JUMP(7),
        ATTACK(8),
        USE_ITEM(9),
        PICK_ITEM(10);

        public final int index;

        HUKeys(int index) {
            this.index = index;
        }

        public static HUKeys getKey(int i) {
            for (HUKeys key : values()) {
                if (key.index == i) {
                    return key;
                }
            }
            return null;
        }
    }
}

package xyz.heroesunited.heroesunited.common.abilities;

import java.util.HashMap;

public class KeyMap extends HashMap<Integer, Boolean> {

    public KeyMap() {
        super();
        for (int i = 1; i < 10; i++) {
            this.put(i, false);
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

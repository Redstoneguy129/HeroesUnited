package xyz.heroesunited.heroesunited.common.abilities;

import java.util.HashMap;

public class KeyMap extends HashMap<Integer, Boolean> {

    @Override
    public Boolean get(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return super.get(key);
    }
}

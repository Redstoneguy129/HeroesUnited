package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.util.KeyMap;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

import javax.annotation.Nullable;

public abstract class AbilityEvent extends PlayerEvent {

    protected final Ability ability;

    public AbilityEvent(Player player, Ability ability) {
        super(player);
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static class Enabled extends AbilityEvent {
        public Enabled(Player player, Ability ability) {
            super(player, ability);
        }
    }

    public static class Disabled extends AbilityEvent {
        public Disabled(Player player, Ability ability) {
            super(player, ability);
        }
    }

    public static class RegisterData extends AbilityEvent {

        public RegisterData(Player player, Ability ability) {
            super(player, ability);
        }

        @Nullable
        @Override
        public Player getEntity() {
            return super.getEntity();
        }

        public HUDataManager getDataManager() {
            return ability.getDataManager();
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    public static class KeyInput extends AbilityEvent {
        private final KeyMap originalMap;
        private final KeyMap map;

        public KeyInput(Player player, Ability ability, KeyMap originalMap, KeyMap map) {
            super(player, ability);
            this.originalMap = originalMap;
            this.map = map;
        }

        public boolean isPressed(int key) {
            return this.originalMap.get(key);
        }

        /**
         * Needs for abilities, that using keys from index in overlay
         */
        public boolean isPressed() {
            return this.map.get(-1);
        }
    }
}

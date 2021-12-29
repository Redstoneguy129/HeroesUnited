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
        public Player getPlayer() {
            return super.getPlayer();
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
        private final KeyMap map;

        public KeyInput(Player player, Ability ability, KeyMap map) {
            super(player, ability);
            this.map = map;
        }

        public boolean isPressed(KeyMap.HUKeys key) {
            return this.map.get(key.index);
        }
    }
}

package xyz.heroesunited.heroesunited.hupacks.js;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.JSONAbility;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSAbilityManager extends JSReloadListener {

    private final List<AbilityType> types = new ArrayList<>();

    public JSAbilityManager(IEventBus bus) {
        super("huabilities", new NashornScriptEngineFactory());
        bus.addGenericListener(AbilityType.class, this::registerAbilityTypes);
    }

    @Override
    public void apply(Map<ResourceLocation, NashornScriptEngine> map, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        for (Map.Entry<ResourceLocation, NashornScriptEngine> entry : map.entrySet()) {
            try {
                types.add(new AbilityType(type -> new JSAbility(type, entry.getValue())).setRegistryName(entry.getKey()));
            } catch (Throwable throwable) {
                HeroesUnited.LOGGER.error("Couldn't read hupack ability {}", entry.getKey(), throwable);
            }
        }
    }

    public void registerAbilityTypes(RegistryEvent.Register<AbilityType> event) {
        for (AbilityType type : types) {
            event.getRegistry().register(type);
        }
    }

    public static class JSAbility extends JSONAbility {

        private final NashornScriptEngine engine;

        public JSAbility(AbilityType type, NashornScriptEngine engine) {
            super(type);
            this.engine = engine;
        }

        @Override
        public void registerData() {
            super.registerData();
            try {
                engine.invokeFunction("registerData", this);
            } catch (ScriptException | NoSuchMethodException ignored) {
            }
        }

        @Override
        public boolean canActivate(PlayerEntity player) {
            try {
                return (boolean) engine.invokeFunction("canActivate", player, this);
            } catch (ScriptException | NoSuchMethodException e) {
                return super.canActivate(player);
            }
        }

        @Override
        public void onUpdate(PlayerEntity player) {
            super.onUpdate(player);
            try {
                engine.invokeFunction("update", player, this);
            } catch (ScriptException | NoSuchMethodException ignored) {
            }
        }

        @Override
        public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
            super.onKeyInput(player, map);
            try {
                engine.invokeFunction("keyInput", player, this, map);
            } catch (ScriptException | NoSuchMethodException ignored) {
            }
        }

        @Override
        public boolean getEnabled() {
            try {
                return (boolean) engine.invokeFunction("enabled", this);
            } catch (ScriptException | NoSuchMethodException e) {
                return super.getEnabled();
            }
        }

        @Override
        public void setRotationAngles(HUSetRotationAnglesEvent event) {
            super.setRotationAngles(event);
            try {
                engine.invokeFunction("setupAnim", event, this);
            } catch (ScriptException | NoSuchMethodException ignored) {
            }
        }
    }
}
package xyz.heroesunited.heroesunited.hupacks.js;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.abilities.AbilityType;
import xyz.heroesunited.heroesunited.common.abilities.JSONAbility;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

public class JSAbilityManager extends JSReloadListener {

    private final List<AbilityType> types = Lists.newArrayList();

    public JSAbilityManager(IEventBus bus) {
        super("huabilities", new NashornScriptEngineFactory());
        bus.addGenericListener(AbilityType.class, this::registerAbilityTypes);
    }

    @Override
    public void apply(Map<ResourceLocation, NashornScriptEngine> map, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
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
        public boolean canActivate(Player player) {
            try {
                return (boolean) engine.invokeFunction("canActivate", player, this);
            } catch (ScriptException | NoSuchMethodException e) {
                return super.canActivate(player);
            }
        }

        @Override
        public void onUpdate(Player player) {
            super.onUpdate(player);
            try {
                engine.invokeFunction("update", player, this);
            } catch (ScriptException | NoSuchMethodException ignored) {
            }
        }

        @Override
        public void onKeyInput(Player player, Map<Integer, Boolean> map) {
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
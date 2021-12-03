package xyz.heroesunited.heroesunited.hupacks.js.item;

import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;

public interface IJSItem extends IAbilityProvider {

    NashornScriptEngine getEngine();
}

package xyz.heroesunited.heroesunited.hupacks.js.item;

import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;

import javax.script.ScriptEngine;

public interface IJSItem extends IAbilityProvider {

    ScriptEngine getEngine();
}

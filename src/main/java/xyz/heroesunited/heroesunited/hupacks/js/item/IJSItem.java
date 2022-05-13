package xyz.heroesunited.heroesunited.hupacks.js.item;

import com.google.common.collect.Maps;
import io.netty.util.internal.StringUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.IAbilityProvider;
import xyz.heroesunited.heroesunited.hupacks.HUPackPowers;

import java.util.LinkedHashMap;
import java.util.Objects;

public interface IJSItem extends IAbilityProvider {

    private Item self() {
        return (Item) this;
    }

    NashornScriptEngine getEngine();

    ResourceLocation getPower();

    @Override
    default LinkedHashMap<String, Ability> getAbilities(Player player) {
        LinkedHashMap<String, Ability> map = Maps.newLinkedHashMap();
        if (!StringUtil.isNullOrEmpty(this.getPower().getPath())) {
            HUPackPowers.getPower(this.getPower()).forEach(a -> {
                Ability ability = a.create(player);
                ability.getAdditionalData().putString("Item", Objects.requireNonNull(self().getRegistryName()).toString());
                map.put(ability.name, ability);
            });
        }
        return map;
    }
}

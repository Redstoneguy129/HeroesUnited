package xyz.heroesunited.heroesunited.common.abilities;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import java.util.List;
import java.util.Map;

public class Superpower implements IAbilityProvider {

    private final Identifier name;
    public JsonObject jsonObject;
    private List<AbilityCreator> containedAbilities;

    public Superpower(Identifier name) {
        this.name = name;
    }

    public Superpower(Identifier name, List<AbilityCreator> containedAbilities) {
        this.name = name;
        this.containedAbilities = containedAbilities;
    }

    public Superpower(Identifier name, JsonObject jsonObject) {
        this(name, AbilityHelper.parseAbilityCreators(jsonObject, name));
        this.jsonObject = jsonObject;
    }

    public Text getDisplayName() {
        return new TranslatableText(Util.createTranslationKey("superpowers", name));
    }

    @Nullable
    public Identifier getRegistryName() {
        return name;
    }

    @Override
    public Map<String, Ability> getAbilities(PlayerEntity player) {
        Map<String, Ability> map = Maps.newHashMap();
        getContainedAbilities().forEach(a -> {
            Ability ability = a.getAbilityType().create(a.getKey());
            ability.getAdditionalData().putString("Superpower", this.getRegistryName().toString());
            if (a.getJsonObject() != null) {
                ability.setJsonObject(player, a.getJsonObject());
            }
            map.put(ability.name, ability);
        });
        return map;
    }

    public List<AbilityCreator> getContainedAbilities() {
        return containedAbilities;
    }
}
package xyz.heroesunited.heroesunited.hupacks.js.item;

import com.google.common.collect.Maps;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.hupacks.HUPackPowers;

import javax.script.ScriptException;
import java.util.Map;
import java.util.Objects;

public class JSItem extends Item implements IJSItem {

    private final NashornScriptEngine engine;
    private final String power;

    public JSItem(Map.Entry<JSItemProperties, NashornScriptEngine> entry) {
        super(entry.getKey());
        this.power = entry.getKey().power;
        this.engine = entry.getValue();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean selected) {
        super.inventoryTick(stack, world, entity, itemSlot, selected);
        try {
            engine.invokeFunction("inventoryTick", stack, world, entity, itemSlot, selected);
        } catch (ScriptException | NoSuchMethodException ignored) {
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        try {
            return (InteractionResultHolder<ItemStack>) engine.invokeFunction("use", world, player, hand);
        } catch (ScriptException | NoSuchMethodException e) {
            return super.use(world, player, hand);
        }
    }

    @Override
    public NashornScriptEngine getEngine() {
        return this.engine;
    }

    @Override
    public Map<String, Ability> getAbilities(Player player) {
        Map<String, Ability> map = Maps.newHashMap();
        HUPackPowers.getPower(new ResourceLocation(this.power)).forEach(abilityCreator -> {
            Ability a = abilityCreator.getAbilityType().create(abilityCreator.getKey());
            a.getAdditionalData().putString("Item", Objects.requireNonNull(this.getRegistryName()).toString());
            if (abilityCreator.getJsonObject() != null) {
                a.setJsonObject(player, abilityCreator.getJsonObject());
            }
            map.put(a.name, a);
        });
        return map;
    }
}

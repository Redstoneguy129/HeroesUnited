package xyz.heroesunited.heroesunited.hupacks.js.item;

import com.google.common.collect.Maps;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean selected) {
        super.inventoryTick(stack, world, entity, itemSlot, selected);
        try {
            engine.invokeFunction("inventoryTick", stack, world, entity, itemSlot, selected);
        } catch (ScriptException | NoSuchMethodException ignored) {
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        try {
            return (ActionResult<ItemStack>) engine.invokeFunction("use", world, player, hand);
        } catch (ScriptException | NoSuchMethodException e) {
            return super.use(world, player, hand);
        }
    }

    @Override
    public NashornScriptEngine getEngine() {
        return this.engine;
    }

    @Override
    public Map<String, Ability> getAbilities(PlayerEntity player) {
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

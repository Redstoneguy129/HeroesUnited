package xyz.heroesunited.heroesunited.hupacks.js.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;
import java.util.Objects;

public class JSAxeItem extends AxeItem implements IJSItem {

    private final ScriptEngine engine;
    private final String power;

    public JSAxeItem(Map.Entry<JSItemProperties, ScriptEngine> entry) {
        super(entry.getKey().tier, entry.getKey().attackDamage, entry.getKey().attackSpeed, entry.getKey());
        this.engine = entry.getValue();
        this.power = entry.getKey().power;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean selected) {
        super.inventoryTick(stack, world, entity, itemSlot, selected);
        try {
            ((Invocable) engine).invokeFunction("inventoryTick", stack, world, entity, itemSlot, selected);
        } catch (ScriptException | NoSuchMethodException ignored) {
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        try {
            return (InteractionResultHolder<ItemStack>) ((Invocable) engine).invokeFunction("use", world, player, hand);
        } catch (ScriptException | NoSuchMethodException e) {
            return super.use(world, player, hand);
        }
    }

    @Override
    public ScriptEngine getEngine() {
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

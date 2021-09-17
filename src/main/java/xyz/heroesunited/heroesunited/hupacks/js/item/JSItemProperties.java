package xyz.heroesunited.heroesunited.hupacks.js.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;

public class JSItemProperties extends Item.Properties {
    public String type = "default";
    protected IItemTier tier;
    protected int attackDamage;
    protected float attackSpeed;

    public Item.Properties type(String type) {
        this.type = type;
        return this;
    }

    public Item.Properties tier(IItemTier tier) {
        this.tier = tier;
        return this;
    }

    public Item.Properties attackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }

    public Item.Properties attackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }
}

package xyz.heroesunited.heroesunited.hupacks.js.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;

public class JSItemProperties extends Item.Properties {
    public String type = "heroesunited:default";
    protected IItemTier tier = ItemTier.DIAMOND;
    protected int attackDamage = 1;
    protected float attackSpeed = 0.5f;
    public ResourceLocation power = new ResourceLocation("");

    public Item.Properties type(String type) {
        this.type = type;
        return this;
    }

    public Item.Properties power(String power) {
        this.power = new ResourceLocation(power);
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

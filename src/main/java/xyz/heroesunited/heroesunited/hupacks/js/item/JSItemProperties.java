package xyz.heroesunited.heroesunited.hupacks.js.item;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public class JSItemProperties extends Item.Properties {
    public String type = "default";
    protected Tier tier;
    protected int attackDamage;
    protected float attackSpeed;

    public Item.Properties type(String type) {
        this.type = type;
        return this;
    }

    public Item.Properties tier(Tier tier) {
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

package xyz.heroesunited.heroesunited.hupacks.js.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class JSItemProperties extends Item.Properties {
    public String type = "heroesunited:default";
    protected Tier tier = Tiers.DIAMOND;
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

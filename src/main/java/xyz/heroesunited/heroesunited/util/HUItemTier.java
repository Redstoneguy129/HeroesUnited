package xyz.heroesunited.heroesunited.util;

import java.util.function.Supplier;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class HUItemTier implements ToolMaterial {

    private final int harvestLevel, maxUses, enchantability;
    private final float efficiency, attackDamage;
    private final Supplier<Ingredient> repairMaterial;

    public HUItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairMaterial = repairMaterial;
    }

    @Override
    public int getDurability() {
        return maxUses;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return efficiency;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }
}

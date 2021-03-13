package xyz.heroesunited.heroesunited.common.objects;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class HUAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, HeroesUnited.MODID);

    public static final Attribute FALL_RESISTANCE = register("fall_resistance", new RangedAttribute("heroesunited.fallResistance", 0D, 0D, Double.MAX_VALUE));
    public static final Attribute JUMP_BOOST = register("jump_boost", new RangedAttribute("heroesunited.jumpBoost", 0D, 0D, Double.MAX_VALUE).setSyncable(true));

    private static Attribute register(String name, Attribute attribute) {
        ATTRIBUTES.register(name, () -> attribute);
        return attribute;
    }
}

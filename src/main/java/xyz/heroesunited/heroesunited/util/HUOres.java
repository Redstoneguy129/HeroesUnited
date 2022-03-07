package xyz.heroesunited.heroesunited.util;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.blocks.HUBlocks;

import java.util.List;

public class HUOres {

    public static Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_TITANIUM_FEATURE;
    public static Holder<PlacedFeature> ORE_TITANIUM_PLACEMENT;

    public static void registerConfiguredFeatures() {
        ORE_TITANIUM_FEATURE = FeatureUtils.register(HeroesUnited.MODID + ":ore_titanium", Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, HUBlocks.TITANIUM_ORE.get().defaultBlockState())), 4));

        ORE_TITANIUM_PLACEMENT = PlacementUtils.register(HeroesUnited.MODID + ":ore_titanium", ORE_TITANIUM_FEATURE, List.of(CountPlacement.of(2), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(48)), BiomeFilter.biome()));
    }
}

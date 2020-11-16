package xyz.heroesunited.heroesunited.common.objects.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.INoiseRandom;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HUBiome extends Biome.Builder {
    public static List<HUBiome> biomes = new ArrayList<>();
    private final Biome biome;

    static final String PARENT = "heroesunited";

    public HUBiome(Biome.Climate climate, Biome.Category category, float depth, float scale, BiomeAmbience ambiance, BiomeGenerationSettings generationSettings, MobSpawnInfo spawnInfo) {
        biome = new Biome(climate, category, depth, scale, ambiance, generationSettings, spawnInfo);
        biomes.add(this);
    }

    public Biome getBiome() {
        return biome;
    }

    @Nullable
    public Biome getHill(INoiseRandom random) {
        return null;
    }

}

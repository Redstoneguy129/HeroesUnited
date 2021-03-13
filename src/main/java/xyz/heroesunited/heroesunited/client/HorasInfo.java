package xyz.heroesunited.heroesunited.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HorasInfo {

    private static final List<PlanetInfo> planets = new ArrayList<>();
    private static final List<EvoInfo> evos = new ArrayList<>();
    private static final List<GhostInfo> ghosts = new ArrayList<>();
    private static final List<AlienInfo> aliens = new ArrayList<>();
    private static final List<DimensionInfo> dimensions = new ArrayList<>();

    public static List<PlanetInfo> getPlanets() {
        return planets;
    }

    public static List<DimensionInfo> getDimensions() {
        return dimensions;
    }

    public static List<AlienInfo> getAliens() {
        return aliens;
    }

    public static List<EvoInfo> getEvos() {
        return evos;
    }

    public static List<GhostInfo> getGhosts() {
        return ghosts;
    }

    /*
    GUI shows all Alien Info.
    GUI shows all Evo Info.
    GUI shows all Ghost Info.
    GUI shows all Planet Info.
    GUI shows all Dimension Info.

    GUI has 2 categories: Dimensions, Aliens, Evo's, Ghosts and Planets.

    Dimension Info shows Name of Dimension and a description of whats in there.
    Planet Info shows Name of Planet and a description of what entities are in there.
    Alien Info shows Name of Alien and what it can do.
    Evo Info shows Name of Evo and what it can do.
     */

    @OnlyIn(Dist.CLIENT)
    public static class AlienInfo {

        private final String name, description;
        private final EntityType<? extends LivingEntity> entityType;

        public AlienInfo(String name, String description, EntityType<? extends LivingEntity> entityType) {
            this.name = name;
            this.description = description;
            this.entityType = entityType;
            assert Minecraft.getInstance().level != null;
            aliens.add(this);
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public EntityType<? extends LivingEntity> getEntityType() {
            return this.entityType;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EvoInfo {

        private final String name, description;
        private final EntityType<? extends LivingEntity> entityType;

        public EvoInfo(String name, String description, EntityType<? extends LivingEntity> entityType) {
            this.name = name;
            this.description = description;
            this.entityType = entityType;
            assert Minecraft.getInstance().level != null;
            evos.add(this);
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public EntityType<? extends LivingEntity> getEntityType() {
            return this.entityType;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GhostInfo {

        private final String name, description;
        private final EntityType<? extends LivingEntity> entityType;

        public GhostInfo(String name, String description, EntityType<? extends LivingEntity> entityType) {
            this.name = name;
            this.description = description;
            this.entityType = entityType;
            assert Minecraft.getInstance().level != null;
            ghosts.add(this);
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public EntityType<? extends LivingEntity> getEntityType() {
            return this.entityType;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class PlanetInfo {

        private final String name, description;
        private final ResourceLocation dimensionID, planetImage;

        public PlanetInfo(String name, String description, ResourceLocation dimensionID, ResourceLocation planetImage) {
            this.name = name;
            this.description = description;
            this.dimensionID = dimensionID;
            this.planetImage = planetImage;
            planets.add(this);
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public ResourceLocation getDimensionID() {
            return this.dimensionID;
        }

        public ResourceLocation getPlanetImage() {
            return this.planetImage;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class DimensionInfo {

        private final String name, description;
        private final ResourceLocation dimensionID, dimensionImage;

        public DimensionInfo(String name, String description, ResourceLocation dimensionID, ResourceLocation dimensionImage) {
            this.name = name;
            this.description = description;
            this.dimensionID = dimensionID;
            this.dimensionImage = dimensionImage;
            dimensions.add(this);
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public ResourceLocation getDimensionID() {
            return this.dimensionID;
        }

        public ResourceLocation getDimensionImage() {
            return this.dimensionImage;
        }
    }
}

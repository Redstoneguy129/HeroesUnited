package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import javax.annotation.Nullable;

public class PlayerGeoModel extends AnimatedTickingGeoModel<IHUPlayer> {
    @Nullable
    private ModelData modelData;

    @Override
    public ResourceLocation getModelLocation(IHUPlayer o) {
        return new ResourceLocation(HeroesUnited.MODID, "geo/player.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(IHUPlayer o) {
        return ((LocalPlayer) ((HUPlayer) o).livingEntity).getSkinTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(IHUPlayer o) {
        return ((HUPlayer) o).animationFile != null ? ((HUPlayer) o).animationFile : new ResourceLocation(HeroesUnited.MODID, "animations/player.animation.json");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setCustomAnimations(IHUPlayer entity, int uniqueID, AnimationEvent customPredicate) {
        if (customPredicate != null) {
            this.modelData = (ModelData) customPredicate.getExtraDataOfType(ModelData.class).get(0);
        }
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public void setMolangQueries(IAnimatable animatable, double currentTick) {
        super.setMolangQueries(animatable, currentTick);
        MolangParser parser = GeckoLibCache.getInstance().parser;
        if (animatable instanceof IHUPlayer && this.modelData != null) {
            for (PlayerPart part : PlayerPart.bodyParts()) {
                ModelPart renderer = part.initialModelPart(this.modelData.model);
                parser.setValue(String.format("player.%s.x_rot", part.name().toLowerCase()), () -> renderer.xRot / Math.PI * 180.0);
                parser.setValue(String.format("player.%s.y_rot", part.name().toLowerCase()), () -> renderer.yRot / Math.PI * 180.0);
                parser.setValue(String.format("player.%s.z_rot", part.name().toLowerCase()), () -> renderer.zRot / Math.PI * 180.0);

                parser.setValue(String.format("player.%s.x", part.name().toLowerCase()), () -> renderer.x);
                parser.setValue(String.format("player.%s.y", part.name().toLowerCase()), () -> renderer.y);
                parser.setValue(String.format("player.%s.z", part.name().toLowerCase()), () -> renderer.z);
            }
            parser.setValue("player.limbSwing", () -> this.modelData.limbSwing);
            parser.setValue("player.limbSwingAmount", () -> this.modelData.limbSwingAmount);
            parser.setValue("player.ageInTicks", () -> this.modelData.ageInTicks);
            parser.setValue("player.headPitch", () -> this.modelData.headPitch);
            parser.setValue("player.netHeadYaw", () -> this.modelData.netHeadYaw);
        }

    }

    public static class ModelData {
        public final PlayerModel<?> model;
        public final float limbSwing, limbSwingAmount, ageInTicks, headPitch, netHeadYaw;

        public ModelData(PlayerModel<?> model) {
            this(model, 0, 0, 0, 0, 0);
        }

        public ModelData(PlayerModel<?> model, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float netHeadYaw) {
            this.model = model;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.headPitch = headPitch;
            this.netHeadYaw = netHeadYaw;
        }
    }
}

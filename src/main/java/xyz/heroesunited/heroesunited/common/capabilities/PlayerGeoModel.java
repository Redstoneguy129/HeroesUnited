package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;
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
        return ((LocalPlayer) ((HUPlayer) o).player).getSkinTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(IHUPlayer o) {
        return ((HUPlayer) o).animationFile != null ? ((HUPlayer) o).animationFile : new ResourceLocation(HeroesUnited.MODID, "animations/player.animation.json");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setLivingAnimations(IHUPlayer entity, Integer uniqueID, AnimationEvent customPredicate) {
        if (customPredicate != null) {
            this.modelData = (ModelData) customPredicate.getExtraDataOfType(ModelData.class).get(0);
        }
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public void setMolangQueries(IAnimatable animatable, double currentTick) {
        super.setMolangQueries(animatable, currentTick);
        MolangParser parser = GeckoLibCache.getInstance().parser;
        if (animatable instanceof IHUPlayer && this.modelData != null) {
            for (PlayerPart part : PlayerPart.bodyParts()) {
                ModelPart renderer = part.getModelRendererByBodyPart(this.modelData.renderer.getModel());
                parser.setValue(String.format("player.%s.x_rot", part.name().toLowerCase()), renderer.xRot / Math.PI * 180.0);
                parser.setValue(String.format("player.%s.y_rot", part.name().toLowerCase()), renderer.yRot / Math.PI * 180.0);
                parser.setValue(String.format("player.%s.z_rot", part.name().toLowerCase()), renderer.zRot / Math.PI * 180.0);

                parser.setValue(String.format("player.%s.x", part.name().toLowerCase()), renderer.x);
                parser.setValue(String.format("player.%s.y", part.name().toLowerCase()), renderer.y);
                parser.setValue(String.format("player.%s.z", part.name().toLowerCase()), renderer.z);
            }
            parser.setValue("player.limbSwing", this.modelData.limbSwing);
            parser.setValue("player.limbSwingAmount", this.modelData.limbSwingAmount);
            parser.setValue("player.ageInTicks", this.modelData.ageInTicks);
            parser.setValue("player.headPitch", this.modelData.headPitch);
            parser.setValue("player.netHeadYaw", this.modelData.netHeadYaw);
        }

    }

    public static class ModelData {
        public final PlayerRenderer renderer;
        public final float limbSwing, limbSwingAmount, ageInTicks, headPitch, netHeadYaw;

        public ModelData(PlayerRenderer renderer) {
            this(renderer, 0, 0, 0, 0, 0);
        }

        public ModelData(PlayerRenderer renderer, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float netHeadYaw) {
            this.renderer = renderer;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.ageInTicks = ageInTicks;
            this.headPitch = headPitch;
            this.netHeadYaw = netHeadYaw;
        }
    }
}

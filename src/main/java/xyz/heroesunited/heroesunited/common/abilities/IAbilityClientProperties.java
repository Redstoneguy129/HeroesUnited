package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.client.events.RendererChangeEvent;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.common.events.RegisterPlayerControllerEvent;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

public interface IAbilityClientProperties {

    IAbilityClientProperties DUMMY = new IAbilityClientProperties() {};

    default void render(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    default void inputUpdate(MovementInputUpdateEvent event) {
    }

    default void registerPlayerControllers(RegisterPlayerControllerEvent event) {
    }

    default void setupAnim(SetupAnimEvent event) {
    }

    default void renderPlayerPre(RenderPlayerEvent.Pre event) {
    }

    default void renderPlayerPost(RenderPlayerEvent.Post event) {
    }

    default boolean renderFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {
        return true;
    }

    default void rendererChange(RendererChangeEvent event) {
    }

    default void drawIcon(PoseStack stack, JsonObject jsonObject, int x, int y) {
        if (jsonObject != null && jsonObject.has("icon")) {
            JsonObject icon = jsonObject.getAsJsonObject("icon");
            String type = GsonHelper.getAsString(icon, "type");
            if (type.equals("texture")) {
                ResourceLocation texture = new ResourceLocation(GsonHelper.getAsString(icon, "texture"));
                int width = GsonHelper.getAsInt(icon, "width", 16);
                int height = GsonHelper.getAsInt(icon, "height", 16);
                int textureWidth = GsonHelper.getAsInt(icon, "texture_width", 256);
                int textureHeight = GsonHelper.getAsInt(icon, "texture_height", 256);
                RenderSystem.setShaderTexture(0, texture);
                GuiComponent.blit(stack, x, y, GsonHelper.getAsInt(icon, "u", 0), GsonHelper.getAsInt(icon, "v", 0), width, height, textureWidth, textureHeight);
            } else if (type.equals("item")) {
                HUClientUtil.renderGuiItem(stack, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(icon, "item")))), x, y, 0);
            }
        } else {
            HUClientUtil.renderGuiItem(stack, new ItemStack(Items.APPLE), x, y, 0);
        }
    }

    default void renderAlways(EntityRendererProvider.Context context, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    default void setAlwaysRotationAngles(SetupAnimEvent event) {

    }

    default void renderPlayerPreAlways(RenderPlayerEvent.Pre event) {

    }

    default void renderPlayerPostAlways(RenderPlayerEvent.Post event) {
    }

    default void renderAlwaysFirstPersonArm(EntityModelSet modelSet, PlayerRenderer renderer, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer player, HumanoidArm side) {

    }
}

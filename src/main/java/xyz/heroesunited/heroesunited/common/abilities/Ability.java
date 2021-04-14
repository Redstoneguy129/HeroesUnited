package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCreators;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public abstract class Ability implements INBTSerializable<CompoundNBT> {

    public String name;
    public final AbilityType type;
    protected int cooldownTicks = 0;
    protected CompoundNBT additionalData = new CompoundNBT();
    protected JsonObject jsonObject;

    public Ability(AbilityType type) {
        this.type = type;
    }

    public boolean canActivate(PlayerEntity player) {
        return jsonObject != null && jsonObject.has("condition") ? AbilityHelper.getEnabled(JSONUtils.getAsString(jsonObject, "condition"), player) : true;
    }

    @Nullable
    public List<ITextComponent> getHoveredDescription() {
        return getJsonObject() != null && getJsonObject().has("description") ? HUJsonUtils.parseDescriptionLines(jsonObject.get("description")) : null;
    }

    public void onActivated(PlayerEntity player) {
    }

    public void onUpdate(PlayerEntity player) {
        if(cooldownTicks > 0) {
            --cooldownTicks;
        }
    }

    public void onDeactivated(PlayerEntity player) {
    }

    @Deprecated
    public void toggle(PlayerEntity player, int id, boolean pressed) {
    }

    public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
        map.forEach((i, b) -> toggle(player, i, b));
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @OnlyIn(Dist.CLIENT)
    public void setRotationAngles(HUSetRotationAnglesEvent event) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFirstPersonArm(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, HandSide side) {
    }

    @OnlyIn(Dist.CLIENT)
    public boolean renderFirstPersonArm(PlayerEntity player) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawIcon(MatrixStack stack, int x, int y) {
        if (getJsonObject() != null) {
            JsonObject icon = JSONUtils.getAsJsonObject(getJsonObject(), "icon", null);
            if (icon != null) {
                String type = JSONUtils.getAsString(icon, "type");
                if (type.equals("texture")) {
                    ResourceLocation texture = new ResourceLocation(JSONUtils.getAsString(icon, "texture"));
                    int width = JSONUtils.getAsInt(icon, "width", 16);
                    int height = JSONUtils.getAsInt(icon, "height", 16);
                    int textureWidth = JSONUtils.getAsInt(icon, "texture_width", 256);
                    int textureHeight = JSONUtils.getAsInt(icon, "texture_height", 256);
                    Minecraft.getInstance().getTextureManager().bind(texture);
                    AbstractGui.blit(stack, x, y, JSONUtils.getAsInt(icon, "u"), JSONUtils.getAsInt(icon, "v"), width, height, textureWidth, textureHeight);
                } else if (type.equals("item")) {
                    String item = JSONUtils.getAsString(icon, "item");
                    Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item))), x, y);
                }
            } else {
                Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(Items.APPLE), x, y);
            }
        } else {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(Items.DIAMOND), x, y);
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("AbilityType", this.type.getRegistryName().toString());
        nbt.putInt("Cooldown", cooldownTicks);
        nbt.put("AdditionalData", additionalData);
        if (this.jsonObject != null) {
            nbt.putString("JsonObject", this.jsonObject.toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.cooldownTicks = nbt.getInt("Cooldown");
        this.additionalData = nbt.getCompound("AdditionalData");
        if (nbt.contains("JsonObject")) {
            this.jsonObject = new JsonParser().parse(nbt.getString("JsonObject")).getAsJsonObject();
        }
    }

    public ITextComponent getTitle() {
        if (getJsonObject() != null && getJsonObject().has("title")) {
            return ITextComponent.Serializer.fromJson(JSONUtils.getAsJsonObject(getJsonObject(), "title"));
        } else {
            return new TranslationTextComponent(name);
        }
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public CompoundNBT getAdditionalData() {
        return additionalData;
    }

    public boolean isHidden() {
        return jsonObject != null && JSONUtils.getAsBoolean(getJsonObject(), "hidden", false);
    }

    public boolean alwaysActive() {
        return getJsonObject() != null && JSONUtils.getAsBoolean(getJsonObject(), "active", false);
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public Ability setJsonObject(Entity entity, JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        if (entity != null && jsonObject != null && entity instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new ClientSyncAbilityCreators(entity.getId(), name, jsonObject));
        }
        return this;
    }

    public Ability setCooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
        return this;
    }

    public Ability setAdditionalData(CompoundNBT nbt) {
        this.additionalData = nbt;
        return this;
    }
}

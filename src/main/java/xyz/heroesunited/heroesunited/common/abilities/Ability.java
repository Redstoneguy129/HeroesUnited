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
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.events.HUCancelSprinting;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCreators;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.hudata.HUData;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public abstract class Ability implements INBTSerializable<CompoundNBT> {

    public String name;
    public final AbilityType type;
    protected CompoundNBT additionalData = new CompoundNBT();
    protected JsonObject jsonObject;
    protected HUDataManager dataManager = new HUDataManager();
    protected JsonConditionManager conditionManager = new JsonConditionManager() {
        @Override
        public void sync(PlayerEntity player) {
            super.sync(player);
            Ability.this.syncToAll(player);
        }
    };

    public Ability(AbilityType type) {
        this.type = type;
    }

    public HUDataManager getDataManager() {
        return this.dataManager;
    }

    public void registerData() {
        this.dataManager.register("prev_cooldown", 0);
        this.dataManager.register("cooldown", 0);
        this.dataManager.register("maxCooldown", 0, true);
    }

    public boolean canActivate(PlayerEntity player) {
        return this.conditionManager.isEnabled(player, "canActivate");
    }

    @Nullable
    public List<ITextComponent> getHoveredDescription() {
        return getJsonObject() != null && getJsonObject().has("description") ? HUJsonUtils.parseDescriptionLines(jsonObject.get("description")) : null;
    }

    public void onActivated(PlayerEntity player) {
    }

    public void onUpdate(PlayerEntity player) {
        this.dataManager.set("prev_cooldown", this.dataManager.<Integer>getValue("cooldown"));
        if (this.dataManager.<Integer>getValue("cooldown") > 0) {
            this.dataManager.set("cooldown", this.dataManager.<Integer>getValue("cooldown") - 1);
        }
        this.conditionManager.update(player);
    }

    public void onUpdate(PlayerEntity player, LogicalSide side) {
    }

    public void onDeactivated(PlayerEntity player) {
    }

    public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
    }

    public void cancelSprinting(HUCancelSprinting event) {
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @OnlyIn(Dist.CLIENT)
    public void inputUpdate(InputUpdateEvent event) {
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
    public void huRenderPlayer(HUChangeRendererEvent event) {
    }

    @OnlyIn(Dist.CLIENT)
    public boolean renderFirstPersonArm(PlayerEntity player) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawIcon(MatrixStack stack, int x, int y) {
        Minecraft.getInstance().getItemRenderer().blitOffset -= 100f;
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
        Minecraft.getInstance().getItemRenderer().blitOffset += 100f;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("AbilityType", this.type.getRegistryName().toString());
        nbt.put("HUData", this.dataManager.serializeNBT());
        nbt.put("Conditions", this.conditionManager.serializeNBT());
        nbt.put("AdditionalData", additionalData);
        if (this.jsonObject != null) {
            nbt.putString("JsonObject", this.jsonObject.toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.dataManager.deserializeNBT(nbt.getCompound("HUData"));
        this.conditionManager.deserializeNBT(nbt.getCompound("Conditions"));
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

    public JsonConditionManager getConditionManager() {
        return conditionManager;
    }

    public CompoundNBT getAdditionalData() {
        return additionalData;
    }

    public int getMaxCooldown(PlayerEntity player) {
        return this.dataManager.<Integer>getValue("maxCooldown");
    }

    public boolean getEnabled() {
        return false;
    }

    public boolean isHidden(PlayerEntity player) {
        return getJsonObject() != null && JSONUtils.getAsBoolean(getJsonObject(), "hidden", false) && this.conditionManager.isEnabled(player, "isHidden");
    }

    public boolean alwaysActive(PlayerEntity player) {
        return getJsonObject() != null && JSONUtils.getAsBoolean(getJsonObject(), "active", false) && this.conditionManager.isEnabled(player, "alwaysActive");
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public Ability setJsonObject(Entity entity, JsonObject jsonObject) {
        if (jsonObject != null) {
            this.jsonObject = jsonObject;
            this.conditionManager.registerConditions(jsonObject);
            if (entity != null) {
                for (Map.Entry<String, HUData<?>> entry : this.dataManager.getHUDataMap().entrySet()) {
                    HUData data = entry.getValue();
                    if (data.isJson()) {
                        this.dataManager.set(entry.getKey(), data.getFromJson(jsonObject, entry.getKey(), entry.getValue().getDefaultValue()));
                    }
                }
                if (entity instanceof ServerPlayerEntity) {
                    HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCreators(entity.getId(), name, jsonObject), ((ServerPlayerEntity) entity).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        }
        return this;
    }

    public Ability setAdditionalData(CompoundNBT nbt) {
        this.additionalData = nbt;
        return this;
    }

    public void sync(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncAbility(player.getId(), this.name, this.serializeNBT()), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public void syncToAll(PlayerEntity player) {
        this.sync(player);
        for (PlayerEntity mpPlayer : player.level.players()) {
            if (mpPlayer instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbility(player.getId(), this.name, this.serializeNBT()), ((ServerPlayerEntity) mpPlayer).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}

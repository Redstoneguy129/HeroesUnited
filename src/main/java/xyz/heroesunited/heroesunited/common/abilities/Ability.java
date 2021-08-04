package xyz.heroesunited.heroesunited.common.abilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.heroesunited.heroesunited.client.events.HUChangeRendererEvent;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.networking.HUNetworking;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbility;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncAbilityCreators;
import xyz.heroesunited.heroesunited.common.networking.client.ClientSyncHUData;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.hudata.HUData;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public abstract class Ability implements INBTSerializable<NbtCompound> {

    public String name;
    public final AbilityType type;
    protected NbtCompound additionalData = new NbtCompound();
    protected JsonObject jsonObject;
    protected HUDataManager dataManager = new HUDataManager() {
        @Override
        public <T> void updateData(Entity entity, String id, HUData<T> data, T value) {
            for (PlayerEntity mpPlayer : entity.world.getPlayers()) {
                if (mpPlayer instanceof ServerPlayerEntity) {
                    HUNetworking.INSTANCE.sendTo(new ClientSyncHUData(entity.getId(), name, id, data.serializeNBT(id, value)), ((ServerPlayerEntity) mpPlayer).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        }
    };
    protected JsonConditionManager conditionManager = new JsonConditionManager() {
        @Override
        public void sync(PlayerEntity player) {
            super.sync(player);
            Ability.this.syncToAll(player);
        }
    };

    public Ability(AbilityType type) {
        this.type = type;
        this.registerData();
    }

    public HUDataManager getDataManager() {
        return this.dataManager;
    }

    public void registerData() {
        this.dataManager.register("cooldown", 0);
    }

    public boolean canActivate(PlayerEntity player) {
        return this.conditionManager.isEnabled(player, "canActivate");
    }

    @Nullable
    public List<Text> getHoveredDescription() {
        return getJsonObject() != null && getJsonObject().has("description") ? HUJsonUtils.parseDescriptionLines(jsonObject.get("description")) : null;
    }

    public void onActivated(PlayerEntity player) {
    }

    public void onUpdate(PlayerEntity player) {
        if (this.dataManager.<Integer>getValue("cooldown") > 0) {
            this.dataManager.set(player, "cooldown", this.dataManager.<Integer>getValue("cooldown") - 1);
        }
        this.conditionManager.update(player);
    }

    public void onUpdate(PlayerEntity player, LogicalSide side) {
    }

    public void onDeactivated(PlayerEntity player) {
    }

    @Deprecated
    public void toggle(PlayerEntity player, int id, boolean pressed) {
    }

    public void onKeyInput(PlayerEntity player, Map<Integer, Boolean> map) {
        map.keySet().forEach((i) -> toggle(player, i, map.get(i)));
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public void renderFirstPersonArm(PlayerEntityRenderer renderer, MatrixStack matrix, VertexConsumerProvider bufferIn, int packedLightIn, AbstractClientPlayerEntity player, Arm side) {
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
        if (getJsonObject() != null) {
            JsonObject icon = JsonHelper.getObject(getJsonObject(), "icon", null);
            if (icon != null) {
                String type = JsonHelper.getString(icon, "type");
                if (type.equals("texture")) {
                    Identifier texture = new Identifier(JsonHelper.getString(icon, "texture"));
                    int width = JsonHelper.getInt(icon, "width", 16);
                    int height = JsonHelper.getInt(icon, "height", 16);
                    int textureWidth = JsonHelper.getInt(icon, "texture_width", 256);
                    int textureHeight = JsonHelper.getInt(icon, "texture_height", 256);
                    MinecraftClient.getInstance().getTextureManager().bind(texture);
                    DrawableHelper.drawTexture(stack, x, y, JsonHelper.getInt(icon, "u"), JsonHelper.getInt(icon, "v"), width, height, textureWidth, textureHeight);
                } else if (type.equals("item")) {
                    String item = JsonHelper.getString(icon, "item");
                    MinecraftClient.getInstance().getItemRenderer().renderInGui(new ItemStack(Registry.ITEM.get(new Identifier(item))), x, y);
                }
            } else {
                MinecraftClient.getInstance().getItemRenderer().renderInGui(new ItemStack(Items.APPLE), x, y);
            }
        } else {
            MinecraftClient.getInstance().getItemRenderer().renderInGui(new ItemStack(Items.DIAMOND), x, y);
        }
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound nbt = new NbtCompound();
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
    public void deserializeNBT(NbtCompound nbt) {
        this.dataManager.deserializeNBT(nbt.getCompound("HUData"));
        this.conditionManager.deserializeNBT(nbt.getCompound("Conditions"));
        this.additionalData = nbt.getCompound("AdditionalData");
        if (nbt.contains("JsonObject")) {
            this.jsonObject = new JsonParser().parse(nbt.getString("JsonObject")).getAsJsonObject();
        }
    }

    public Text getTitle() {
        if (getJsonObject() != null && getJsonObject().has("title")) {
            return Text.Serializer.fromJson(JsonHelper.getObject(getJsonObject(), "title"));
        } else {
            return new TranslatableText(name);
        }
    }

    public JsonConditionManager getConditionManager() {
        return conditionManager;
    }

    public NbtCompound getAdditionalData() {
        return additionalData;
    }

    public boolean isHidden(PlayerEntity player) {
        return getJsonObject() != null && JsonHelper.getBoolean(getJsonObject(), "hidden", false) && this.conditionManager.isEnabled(player, "isHidden");
    }

    public boolean alwaysActive(PlayerEntity player) {
        return getJsonObject() != null && JsonHelper.getBoolean(getJsonObject(), "active", false) && this.conditionManager.isEnabled(player, "alwaysActive");
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
                        this.dataManager.set(entity, entry.getKey(), data.getFromJson(jsonObject, entry.getKey(), entry.getValue().getDefaultValue()));
                    }
                }
                if (entity instanceof ServerPlayerEntity) {
                    HUNetworking.INSTANCE.sendTo(new ClientSyncAbilityCreators(entity.getId(), name, jsonObject), ((ServerPlayerEntity) entity).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            }
        }
        return this;
    }

    public Ability setAdditionalData(NbtCompound nbt) {
        this.additionalData = nbt;
        return this;
    }

    public void sync(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.sendTo(new ClientSyncAbility(player.getId(), this.name, this.serializeNBT()), ((ServerPlayerEntity) player).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public void syncToAll(PlayerEntity player) {
        this.sync(player);
        for (PlayerEntity mpPlayer : player.world.getPlayers()) {
            if (mpPlayer instanceof ServerPlayerEntity) {
                HUNetworking.INSTANCE.sendTo(new ClientSyncAbility(player.getId(), this.name, this.serializeNBT()), ((ServerPlayerEntity) mpPlayer).networkHandler.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}

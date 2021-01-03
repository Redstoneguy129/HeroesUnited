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

import javax.annotation.Nullable;
import java.util.List;

public abstract class Ability implements INBTSerializable<CompoundNBT> {

    public String name;
    public final AbilityType type;
    private String superpower;
    protected JsonObject jsonObject;

    public Ability(AbilityType type) {
        this.type = type;
    }

    public boolean canActivate(PlayerEntity player) {
        return true;
    }

    @Nullable
    public List<ITextComponent> getHoveredDescription() {
        return null;
    }

    public void onActivated(PlayerEntity player) {
    }

    public void onUpdate(PlayerEntity player) {
    }

    public void onDeactivated(PlayerEntity player) {
    }

    public void toggle(PlayerEntity player, int id, boolean pressed) {
    }

    //Client Stuff
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
            JsonObject icon = JSONUtils.getJsonObject(getJsonObject(), "icon", null);
            if (icon != null) {
                String type = JSONUtils.getString(icon, "type");
                if (type.equals("texture")) {
                    ResourceLocation texture = new ResourceLocation(JSONUtils.getString(icon, "texture"));
                    int width = JSONUtils.getInt(icon, "width", 16);
                    int height = JSONUtils.getInt(icon, "height", 16);
                    int textureWidth = JSONUtils.getInt(icon, "texture_width", 256);
                    int textureHeight = JSONUtils.getInt(icon, "texture_height", 256);
                    Minecraft.getInstance().getTextureManager().bindTexture(texture);
                    AbstractGui.blit(stack, x, y, JSONUtils.getInt(icon, "u"), JSONUtils.getInt(icon, "v"), width, height, textureWidth, textureHeight);
                } else if (type.equals("item")) {
                    String item = JSONUtils.getString(icon, "item");
                    Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item))), x, y);
                }
            } else {
                Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(new ItemStack(Items.APPLE), x, y);
            }
        } else {
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(new ItemStack(Items.DIAMOND), x, y);
        }
    }


    public String getSuperpower() {
        return superpower;
    }

    public Ability setSuperpower(String superpower) {
        this.superpower = superpower;
        return this;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("AbilityType", this.type.getRegistryName().toString());
        if (this.superpower != null) {
            nbt.putString("Superpower", this.superpower);
        }
        if (this.jsonObject != null) {
            nbt.putString("JsonObject", this.jsonObject.toString());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Superpower")) {
            this.superpower = nbt.getString("Superpower");
        }
        if (nbt.contains("JsonObject")) {
            this.jsonObject = new JsonParser().parse(nbt.getString("JsonObject")).getAsJsonObject();
        }
    }

    public ITextComponent getTitle() {
        if (getJsonObject() != null && JSONUtils.hasField(getJsonObject(), "title")) {
            return ITextComponent.Serializer.getComponentFromJson(JSONUtils.getJsonObject(getJsonObject(), "title"));
        } else {
            return new TranslationTextComponent(name);
        }
    }

    public boolean isHidden() {
        return jsonObject != null && JSONUtils.getBoolean(getJsonObject(), "hidden", false);
    }

    public boolean alwaysActive() {
        return getJsonObject() != null && JSONUtils.getBoolean(getJsonObject(), "active", false);
    }

    public Ability setJsonObject(Entity entity, JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        if (entity != null && entity instanceof ServerPlayerEntity) {
            HUNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new ClientSyncAbilityCreators(entity.getEntityId(), name, jsonObject));
        }
        return this;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }
}

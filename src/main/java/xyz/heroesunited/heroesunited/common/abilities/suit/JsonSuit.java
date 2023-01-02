package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import oshi.util.tuples.Pair;
import xyz.heroesunited.heroesunited.client.events.SetupAnimEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.ConditionManager;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.HUPlayerUtil;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonSuit extends Suit {

    protected ConditionManager conditionManager = new ConditionManager();
    protected final JsonObject jsonObject;

    public JsonSuit(Map.Entry<ResourceLocation, JsonObject> map) {
        super(map.getKey());
        this.jsonObject = map.getValue();
    }

    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public Map<EquipmentSlot, Pair<ResourceLocation, SuitItem>> createItems() {
        for (EquipmentSlot slot : HUPlayerUtil.ARMOR_SLOTS) {
            if (this.jsonObject.has("slots")) {
                JsonObject slots = jsonObject.getAsJsonObject("slots");
                if (slots.has(slot.getName().toLowerCase())) {
                    this.itemList.put(slot, this.createItem(this, slot, slots));
                }
            } else {
                this.itemList.put(slot, this.createItem(this, slot));
            }
        }
        return this.itemList;
    }

    protected Pair<ResourceLocation, SuitItem> createItem(Suit suit, EquipmentSlot slot, JsonObject slots) {
        return new Pair<>(new ResourceLocation(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + GsonHelper.getAsString(slots, slot.getName().toLowerCase())), new SuitItem(suit.getSuitMaterial(), slot, new Item.Properties().stacksTo(1), suit));
    }

    @Override
    public Map<String, Ability> getAbilities(Player player) {
        Map<String, Ability> map = Maps.newHashMap();
        AbilityHelper.parseAbilityCreators(this.jsonObject, getRegistryName()).forEach(a -> map.put(a.key, a.create(player)));
        return map;
    }

    @Override
    public void onUpdate(Player player, EquipmentSlot slot) {
        super.onUpdate(player, slot);
        this.conditionManager.registerConditions(jsonObject);
    }

    @Override
    public boolean canEquip(Player player) {
        this.conditionManager.registerConditions(jsonObject);
        return super.canEquip(player) && this.conditionManager.isEnabled(player, "equip");
    }

    @Override
    public ArmorMaterial getSuitMaterial() {
        if (jsonObject.has("armor_material")) {
            JsonElement materialJson = jsonObject.get("armor_material");
            if (materialJson.isJsonPrimitive()) {
                for (ArmorMaterials material : ArmorMaterials.values()) {
                    if (material.name().toLowerCase().equals(materialJson.getAsString())) {
                        return material;
                    }
                }
            }
            return HUJsonUtils.parseArmorMaterial(materialJson.getAsJsonObject());
        }
        return super.getSuitMaterial();
    }

    @Override
    public boolean canBreathOnSpace() {
        return GsonHelper.getAsBoolean(jsonObject, "breath_in_space", super.canBreathOnSpace());
    }

    @Override
    public CreativeModeTab getItemGroup() {
        return jsonObject.has("itemGroup") ? HUJsonUtils.getItemGroup(jsonObject, "itemGroup") : super.getItemGroup();
    }

    @Override
    public List<Component> getDescription(ItemStack stack) {
        return jsonObject.has("description") ? HUJsonUtils.parseDescriptionLines(jsonObject.get("description")) : super.getDescription(stack);
    }

    @Override
    public float getScale(EquipmentSlot slot) {
        return jsonObject.has("scale") ? GsonHelper.getAsFloat(jsonObject, "scale") : super.getScale(slot);
    }

    @Override
    public void serializeNBT(CompoundTag nbt, ItemStack stack) {
        super.serializeNBT(nbt, stack);
        nbt.put("Conditions", this.getConditionManager().serializeNBT());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, ItemStack stack) {
        super.deserializeNBT(nbt, stack);
        this.getConditionManager().deserializeNBT(nbt.getCompound("Conditions"));
    }

    @Override
    public List<EquipmentAccessoriesSlot> getSlotForHide(EquipmentSlot slot) {
        List<EquipmentAccessoriesSlot> list = new ArrayList<>();
        if (jsonObject.has("hide_accessories")) {
            JsonObject jsonObject = GsonHelper.getAsJsonObject(this.jsonObject, "hide_accessories");
            for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                if (e.getValue() instanceof JsonArray && slot.equals(EquipmentSlot.byName(e.getKey()))) {
                    for (int i = 0; i < ((JsonArray) e.getValue()).size(); i++) {
                        list.add(EquipmentAccessoriesSlot.getFromSlotIndex(((JsonArray) e.getValue()).get(i).getAsInt()));
                    }
                }
            }
        }
        return list;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setupAnim(SetupAnimEvent event, EquipmentSlot slot) {
        super.setupAnim(event, slot);
        if (jsonObject.has("visibility_parts")) {
            JsonObject overrides = GsonHelper.getAsJsonObject(jsonObject, "visibility_parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                PlayerPart part = PlayerPart.byName(entry.getKey());
                if (part != null) {
                    if (entry.getValue() instanceof JsonObject json) {
                        if (slot.equals(EquipmentSlot.byName(GsonHelper.getAsString(json, "slot"))) && !GsonHelper.getAsBoolean(json, "show")) {
                            part.setVisibility(event.getPlayerModel(), false);
                        }
                    } else {
                        if (hasArmorOn(event.getEntity()) && !GsonHelper.getAsBoolean(overrides, entry.getKey())) {
                            part.setVisibility(event.getPlayerModel(), false);
                        }
                    }
                }
            }
        }
    }
}
package xyz.heroesunited.heroesunited.common.abilities.suit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import xyz.heroesunited.heroesunited.client.events.HUSetRotationAnglesEvent;
import xyz.heroesunited.heroesunited.common.abilities.Ability;
import xyz.heroesunited.heroesunited.common.abilities.AbilityHelper;
import xyz.heroesunited.heroesunited.common.abilities.JsonConditionManager;
import xyz.heroesunited.heroesunited.common.objects.container.EquipmentAccessoriesSlot;
import xyz.heroesunited.heroesunited.util.HUJsonUtils;
import xyz.heroesunited.heroesunited.util.PlayerPart;

import java.util.List;
import java.util.Map;

public class JsonSuit extends Suit {

    protected JsonConditionManager conditionManager = new JsonConditionManager();
    protected final JsonObject jsonObject;

    public JsonSuit(Map.Entry<ResourceLocation, JsonObject> map) {
        super(map.getKey());
        this.jsonObject = map.getValue();
    }

    public JsonConditionManager getConditionManager() {
        return conditionManager;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public void registerItems(IForgeRegistry<Item> e) {
        JsonObject slots = GsonHelper.getAsJsonObject(jsonObject, "slots", null);
        if (slots != null) {
            if (slots.has("head")) {
                e.register(helmet = createItem(this, EquipmentSlot.HEAD, slots));
            }
            if (slots.has("chest")) {
                e.register(chestplate = createItem(this, EquipmentSlot.CHEST, slots));
            }
            if (slots.has("legs")) {
                e.register(legs = createItem(this, EquipmentSlot.LEGS, slots));
            }
            if (slots.has("feet")) {
                e.register(boots = createItem(this, EquipmentSlot.FEET, slots));
            }
        } else {
            e.register(helmet = createItem(this, EquipmentSlot.HEAD));
            e.register(chestplate = createItem(this, EquipmentSlot.CHEST));
            e.register(legs = createItem(this, EquipmentSlot.LEGS));
            e.register(boots = createItem(this, EquipmentSlot.FEET));
        }
    }

    protected SuitItem createItem(Suit suit, EquipmentSlot slot, JsonObject slots) {
        return (SuitItem) new SuitItem(suit.getSuitMaterial(), slot, new Item.Properties().stacksTo(1).tab(suit.getItemGroup()), suit).setRegistryName(suit.getRegistryName().getNamespace(), suit.getRegistryName().getPath() + "_" + GsonHelper.getAsString(slots, slot.getName().toLowerCase()));
    }

    @Override
    public Map<String, Ability> getAbilities(Player player) {
        Map<String, Ability> map = Maps.newHashMap();
        AbilityHelper.parseAbilityCreators(jsonObject, getRegistryName()).forEach(a -> {
            Ability ability = a.getAbilityType().create(a.getKey());
            if (a.getJsonObject() != null) {
                ability.setJsonObject(player, a.getJsonObject());
            }
            map.put(ability.name, ability);
        });
        return map;
    }

    @Override
    public boolean canEquip(Player player) {
        return super.canEquip(player) && this.conditionManager.isEnabled(player, "equip");
    }

    @Override
    public ArmorMaterial getSuitMaterial() {
        if (jsonObject.has("armor_material")) {
            JsonElement materialJson = jsonObject.get("armor_material");
            ArmorMaterial material = materialJson.isJsonPrimitive() ? HUJsonUtils.getArmorMaterial(materialJson.getAsString()) : HUJsonUtils.parseArmorMaterial(materialJson.getAsJsonObject(), false);
            if (material == null)
                throw new JsonParseException("Armor material with name '" + materialJson.getAsString() + "' cannot be found!");
            return material;
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
    public List<EquipmentAccessoriesSlot> getSlotForHide(EquipmentSlot slot) {
        List<EquipmentAccessoriesSlot> list = Lists.newArrayList();
        if (jsonObject.has("hide_accessories")) {
            JsonObject jsonObject = GsonHelper.getAsJsonObject(this.jsonObject, "hide_accessories");
            for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                if (e.getValue() instanceof JsonArray && slot.equals(EquipmentSlot.byName(e.getKey()))) {
                    for (int i = 0; i < ((JsonArray) e.getValue()).size(); i++) {
                        list.add(EquipmentAccessoriesSlot.getFromSlotIndex(((JsonArray)e.getValue()).get(i).getAsInt()));
                    }
                }
            }
        }
        return list;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setRotationAngles(HUSetRotationAnglesEvent event, EquipmentSlot slot) {
        super.setRotationAngles(event, slot);
        if (jsonObject.has("visibility_parts")) {
            JsonObject overrides = GsonHelper.getAsJsonObject(jsonObject, "visibility_parts");

            for (Map.Entry<String, JsonElement> entry : overrides.entrySet()) {
                PlayerPart part = PlayerPart.getByName(entry.getKey());
                if (part != null) {
                    if (entry.getValue() instanceof JsonObject) {
                        JsonObject json = (JsonObject) entry.getValue();
                        if (slot.equals(EquipmentSlot.byName(GsonHelper.getAsString(json, "slot")))) {
                            part.setVisibility(event.getPlayerModel(), GsonHelper.getAsBoolean(json, "show"));
                        }
                    } else {
                        if (hasArmorOn(event.getPlayer())) {
                            part.setVisibility(event.getPlayerModel(), GsonHelper.getAsBoolean(overrides, entry.getKey()));
                        }
                    }
                }
            }
        }
    }
}
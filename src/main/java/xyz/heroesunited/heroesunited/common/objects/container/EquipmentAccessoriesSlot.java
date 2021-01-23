package xyz.heroesunited.heroesunited.common.objects.container;

public enum EquipmentAccessoriesSlot {
    HELMET(0),
    TSHIRT(1),
    PANTS(2),
    SHOES(3),
    JACKET(4),
    RIGHT_WRIST(5),
    LEFT_WRIST(6),
    GLOVES(7),
    WRIST(8);

    private final int slot;

    EquipmentAccessoriesSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public static EquipmentAccessoriesSlot getFromSlotIndex(int slotIndexIn) {
        for (EquipmentAccessoriesSlot slot : values()) {
            if (slot.getSlot() == slotIndexIn) {
                return slot;
            }
        }
        return null;
    }
}

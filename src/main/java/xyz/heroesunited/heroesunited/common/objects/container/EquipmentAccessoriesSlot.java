package xyz.heroesunited.heroesunited.common.objects.container;

public enum EquipmentAccessoriesSlot {
    HELMET(0),
    TSHIRT(1),
    PANTS(2),
    SHOES(3),
    JACKET(4),
    BELT(5),
    RIGHT_WRIST(6),
    LEFT_WRIST(7),
    GLOVES(8),
    WRIST(9);

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

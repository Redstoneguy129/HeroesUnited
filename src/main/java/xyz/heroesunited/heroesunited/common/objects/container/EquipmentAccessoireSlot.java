package xyz.heroesunited.heroesunited.common.objects.container;

public enum EquipmentAccessoireSlot {
    HELMET(0),
    TSHIRT(1),
    PANTS(2),
    SHOES(3),
    JACKET(4),
    RIGHT_WRIST(5),
    LEFT_WRIST(6),
    GLOVES(7);

    private final int slot;

    EquipmentAccessoireSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public static EquipmentAccessoireSlot getFromSlotIndex(int slotIndexIn) {
        for(EquipmentAccessoireSlot slot : values()) {
            if (slot.getSlot() == slotIndexIn) {
                return slot;
            }
        }
        return null;
    }
}

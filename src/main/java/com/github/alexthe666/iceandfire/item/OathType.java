package com.github.alexthe666.iceandfire.item;

public enum OathType {
    WASTE,
    TIDE,
    BARROW,
    BLACK_FROST;

    public String id() {
        return name().toLowerCase();
    }

    public static OathType byId(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (OathType type : values()) {
            if (type.name().equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }
}

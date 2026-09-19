package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;

/**
 * 1.18 {@code ArmorItem#getMaterial()} identity for IAF pieces after
 * {@code ArmorItem} was removed.
 */
public interface IafArmorIdentity {
    CustomArmorMaterial iafMaterial();
}

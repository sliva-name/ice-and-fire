/*
 * Ice and Fire's Forge 26.1 Citadel subset (LGPL).
 * Based on Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5 by Alexthe666.
 * Citadel credits LLibrary (iLexiconn and Gegy1000), used with permission.
 */
package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.network.AnimationNetwork;
import com.github.alexthe666.citadel.network.PropertiesNetwork;
import net.minecraftforge.fml.common.Mod;

/** Standalone animation bootstrap, not a complete port of Citadel. */
@Mod(Citadel.MOD_ID)
public final class Citadel {
    public static final String MOD_ID = "citadel";

    public Citadel() {
        AnimationNetwork.register();
        PropertiesNetwork.register();
    }
}

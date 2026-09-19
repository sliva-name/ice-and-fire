package com.github.alexthe666.citadel.client.model.container;

/**
 * Standalone value type from Citadel commit
 * 8018e44d8b569913ca828f31aa6c86163319e7a5; client-only annotation removed.
 */
public class TextureOffset {
    /** The x coordinate offset of the texture. */
    public final int textureOffsetX;
    /** The y coordinate offset of the texture. */
    public final int textureOffsetY;

    public TextureOffset(int textureOffsetXIn, int textureOffsetYIn) {
        this.textureOffsetX = textureOffsetXIn;
        this.textureOffsetY = textureOffsetYIn;
    }
}

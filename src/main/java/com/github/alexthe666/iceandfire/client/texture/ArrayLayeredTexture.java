package com.github.alexthe666.iceandfire.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import java.io.IOException;
import java.util.List;

/** CPU composition followed by the native reloadable-texture upload/ownership path. */
public class ArrayLayeredTexture extends ReloadableTexture {
    public final List<String> layeredTextureNames;

    public ArrayLayeredTexture(List<String> textureNames) {
        super(Identifier.parse(textureNames.getFirst()));
        layeredTextureNames = List.copyOf(textureNames);
    }

    @Override
    public TextureContents loadContents(ResourceManager manager) throws IOException {
        NativeImage result;
        try (var stream = manager.getResourceOrThrow(resourceId()).open()) {
            result = NativeImage.read(stream);
        }
        try {
            for (int layer = 1; layer < layeredTextureNames.size(); layer++) {
                try (var stream = manager.getResourceOrThrow(Identifier.parse(layeredTextureNames.get(layer))).open();
                     NativeImage image = NativeImage.read(stream)) {
                    for (int y = 0; y < Math.min(result.getHeight(), image.getHeight()); y++) {
                        for (int x = 0; x < Math.min(result.getWidth(), image.getWidth()); x++) {
                            result.setPixel(x, y, blend(result.getPixel(x, y), image.getPixel(x, y)));
                        }
                    }
                }
            }
            return new TextureContents(result, null);
        } catch (IOException | RuntimeException exception) {
            result.close();
            throw exception;
        }
    }

    /** Retains the legacy layer blend (including its squared source alpha), in ARGB. */
    public static int blend(int destination, int source) {
        float alpha = (source >>> 24) / 255.0F;
        float inverse = 1 - alpha;
        int result = 0;
        for (int shift = 0; shift <= 24; shift += 8) {
            int channel = (int) (((source >>> shift & 255) * alpha)
                + ((destination >>> shift & 255) * inverse));
            result |= Math.min(channel, 255) << shift;
        }
        return result;
    }
}

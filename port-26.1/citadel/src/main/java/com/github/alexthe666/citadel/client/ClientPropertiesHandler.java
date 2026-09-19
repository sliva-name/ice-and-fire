/*
 * Citadel subset for Ice and Fire, modified for Minecraft 26.1 / Forge 62.0.9.
 * Based on ClientProxy's properties handling by Alexthe666 / AlexModGuy,
 * https://github.com/AlexModGuy/Citadel, ref 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Citadel credits LLibrary by iLexiconn and Gegy1000, used with permission.
 * See citadel/NOTICE.md for provenance and unresolved license/distribution obligations.
 */
package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

/** Invoked on the client main thread, never loaded by the dedicated-server handler. */
public final class ClientPropertiesHandler {
    private ClientPropertiesHandler() {
    }

    public static void handle(PropertiesMessage message) {
        var level = Minecraft.getInstance().level;
        if (level != null && level.dimension().identifier().equals(message.dimension())
                && level.getEntity(message.entityId()) instanceof LivingEntity entity
                && entity.getUUID().equals(message.entityUuid())) {
            CitadelEntityData.storeTag(entity.getPersistentData(), message.tag());
        }
    }
}

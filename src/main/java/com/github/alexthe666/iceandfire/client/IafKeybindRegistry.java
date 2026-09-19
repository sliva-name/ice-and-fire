package com.github.alexthe666.iceandfire.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class IafKeybindRegistry {
    public static KeyMapping dragon_fireAttack;
    public static KeyMapping dragon_strike;
    public static KeyMapping dragon_down;
    public static KeyMapping dragon_change_view;

    public static void register(RegisterKeyMappingsEvent event) {
        dragon_fireAttack = new KeyMapping("key.dragon_fireAttack", 82, KeyMapping.Category.GAMEPLAY);
        dragon_strike = new KeyMapping("key.dragon_strike", 71, KeyMapping.Category.GAMEPLAY);
        dragon_down = new KeyMapping("key.dragon_down", 88, KeyMapping.Category.GAMEPLAY);
        dragon_change_view = new KeyMapping("key.dragon_change_view", 296, KeyMapping.Category.MISC);
        event.register(dragon_fireAttack);
        event.register(dragon_strike);
        event.register(dragon_down);
        event.register(dragon_change_view);
    }
}

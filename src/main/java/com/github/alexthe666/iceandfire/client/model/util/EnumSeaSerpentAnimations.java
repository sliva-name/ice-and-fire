package com.github.alexthe666.iceandfire.client.model.util;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState;

import java.io.IOException;

public enum EnumSeaSerpentAnimations {
    T_POSE(""),
    SWIM1("Swim1"),
    SWIM2("Swim2"),
    SWIM3("Swim3"),
    SWIM4("Swim4"),
    SWIM5("Swim5"),
    SWIM6("Swim6"),
    BITE1("Bite1"),
    BITE2("Bite2"),
    BITE3("Bite3"),
    ROAR1("Roar1"),
    ROAR2("Roar2"),
    ROAR3("Roar3"),
    DEAD("Dead"),
    JUMPING1("Jumping1"),
    JUMPING2("Jumping2");


    private final String fileSuffix;
    public TabulaModel<SeaSerpentRenderState> seaserpent_model;

    EnumSeaSerpentAnimations(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }


    /** Called from client model initialization; fail at the asset, not later during animation. */
    public static void initializeSerpentModels() {
        for (EnumSeaSerpentAnimations animation : values()) {
            String path = "/assets/iceandfire/models/tabula/seaserpent/seaserpent" + animation.fileSuffix;
            try {
                animation.seaserpent_model = new TabulaModel<>(TabulaModelHandlerHelper.loadTabulaModel(path));
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load SeaSerpent pose " + path + ".tbl", e);
            }
        }
    }
}

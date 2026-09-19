package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Render-thread inputs, independent of the entity and registry-backed troll enums. */
public class TrollRenderState extends LivingEntityRenderState {
    public Variant variant = Variant.FOREST;
    public Weapon weapon = Weapon.AXE;
    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
    // EntityTroll has no previous stone progress; retain its integer-tick progression.
    public float stoneProgress;
    public boolean stone;
    public boolean statue;

    public boolean hasWeapon() {
        return weapon != null && !stone && !statue;
    }

    public boolean hasEyes() {
        return !stone && !statue;
    }

    public Identifier bodyTexture() {
        // Only the separate statue path used the stone atlas; stone mobs kept the normal body atlas.
        return statue ? variant.stoneTexture : variant.texture;
    }

    public enum AnimationKind {
        NONE(0), SPEAK(10), ROAR(25), STRIKE_HORIZONTAL(20), STRIKE_VERTICAL(20);

        public final Animation token;

        AnimationKind(int duration) {
            token = Animation.create(duration);
        }
    }

    public enum Variant {
        FOREST("forest"), FROST("frost"), MOUNTAIN("mountain");

        public final Identifier texture;
        public final Identifier stoneTexture;
        public final Identifier eyesTexture;

        Variant(String name) {
            texture = texture("troll_" + name);
            stoneTexture = texture("troll_" + name + "_stone");
            eyesTexture = texture("troll_" + name + "_eyes");
        }
    }

    public enum Weapon {
        AXE("axe"), COLUMN("column"), COLUMN_FOREST("column_forest"),
        COLUMN_FROST("column_frost"), HAMMER("hammer"), TRUNK("trunk"), TRUNK_FROST("trunk_frost");

        public final Identifier texture;

        Weapon(String name) {
            texture = texture("weapon/weapon_" + name);
        }
    }

    private static Identifier texture(String path) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/troll/" + path + ".png");
    }
}

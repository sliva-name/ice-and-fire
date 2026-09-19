package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;

/** Snapshot of Myrmex model inputs. Extraction must overwrite caste-specific fields on every frame. */
public class MyrmexRenderState extends LivingEntityRenderState {
    public enum Caste { WORKER, SOLDIER, SENTINEL, ROYAL, QUEEN }

    /** Renderer resolves entity token identity, not duration, to these model-owned identities. */
    public enum AnimationKind {
        NONE(0), PUPA_WIGGLE(20), BITE(15), STING(15), SENTINEL_STING(25),
        GRAB(15), NIBBLE(10), SLASH(25), EGG(20), DIG_NEST(45);

        private final Animation token;

        AnimationKind(int duration) {
            token = Animation.create(duration);
        }

        public Animation token() {
            return token;
        }
    }

    public Caste caste = Caste.WORKER;
    public int growthStage = 2;
    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
    /** Integer entity tick, deliberately not ageInTicks, for queen gaster swelling. */
    public int tickCount;
    public boolean hasPassengers;
    public boolean holding;
    public boolean hiding;
    public boolean flying;
    public boolean onGround;

    // Copy current values verbatim: legacy models did not interpolate these progress fields.
    public float holdingProgress;
    public float hidingProgress;
    public float flyProgress;

    public float modelScale = 1.0F;
    public boolean passenger;
    public Identifier texture = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/myrmex/myrmex_desert_worker.png");
    public final ItemStackRenderState mouthItem = new ItemStackRenderState();
    public boolean mouthItemIsBlock;
    public boolean shiftKeyDown;

    public boolean isLayingEgg() {
        return animation == AnimationKind.EGG;
    }

    public boolean isDiggingNest() {
        return animation == AnimationKind.DIG_NEST;
    }

    /** Both juvenile slots historically use the pupa model, even stage zero. */
    public boolean usesPupaModel() {
        return growthStage == 0 || growthStage == 1;
    }
}

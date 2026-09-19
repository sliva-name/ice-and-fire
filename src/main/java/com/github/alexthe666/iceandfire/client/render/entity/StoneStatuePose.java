package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.iceandfire.client.model.ModelHippocampus;
import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Pose and transform helpers for stone statues. Citadel models skip live {@code setupAnim}
 * and stay on {@code resetToDefaultPose}, matching the 1.18 AdvancedEntityModel branch.
 */
public final class StoneStatuePose {
    public static final Identifier[] DESTROY_STAGES = new Identifier[]{
        stage(0), stage(1), stage(2), stage(3), stage(4),
        stage(5), stage(6), stage(7), stage(8), stage(9)
    };

    private StoneStatuePose() {
    }

    public static float scale(float trappedScale) {
        return trappedScale < 0.01F ? 1.0F : trappedScale;
    }

    public static int crackStage(int crackAmount) {
        return Mth.clamp(crackAmount - 1, 0, DESTROY_STAGES.length - 1);
    }

    public static Identifier crackTexture(int crackAmount) {
        if (crackAmount < 1) {
            return null;
        }
        return DESTROY_STAGES[crackStage(crackAmount)];
    }

    public static void applyTransforms(PoseStack poses, float scale, float yaw) {
        poses.scale(scale, scale, scale);
        poses.translate(0.0F, 1.5F, 0.0F);
        poses.mulPose(Axis.XP.rotationDegrees(180.0F));
        poses.mulPose(Axis.YP.rotationDegrees(yaw));
    }

    public static String entityPath(String trappedType) {
        int colon = trappedType.indexOf(':');
        return colon >= 0 ? trappedType.substring(colon + 1) : trappedType;
    }

    /**
     * @param crackPass {@code true} for the destroy overlay. Hippogryph baby scale and hippocampus
     * tack hiding run after the first draw in 1.18, so only the crack pass sees them.
     */
    public static void poseCitadel(AdvancedEntityModel<?> model, boolean crackPass, boolean baby) {
        if (model == null) {
            return;
        }
        model.resetToDefaultPose();
        if (model instanceof ModelTroll troll) {
            troll.animateStatue();
        }
        if (crackPass) {
            if (model instanceof ModelHippogryph hippogryph) {
                applyHippogryphAfterDraw(hippogryph, baby);
            }
            if (model instanceof ModelHippocampus hippocampus) {
                hideHippocampusTack(hippocampus);
            }
        }
    }

    public static void applyHippogryphAfterDraw(ModelHippogryph model, boolean baby) {
        if (baby) {
            model.Body.setShouldScaleChildren(true);
            model.Head.setShouldScaleChildren(false);
            model.Body.setScale(0.5F, 0.5F, 0.5F);
            model.Head.setScale(1.5F, 1.5F, 1.5F);
            model.Beak.setScale(0.75F, 0.75F, 0.75F);
            model.Quill_L.setScale(2.0F, 2.0F, 2.0F);
            model.Quill_R.setScale(2.0F, 2.0F, 2.0F);
            model.Body.setPos(0.0F, 18.0F, 4.0F);
        } else {
            model.Body.setScale(1.0F, 1.0F, 1.0F);
            model.Head.setScale(1.0F, 1.0F, 1.0F);
        }
        hideHippogryphTack(model);
    }

    public static void hideHippogryphTack(ModelHippogryph model) {
        model.NoseBand.showModel = false;
        model.ReinL.showModel = false;
        model.ReinR.showModel = false;
        model.ChestL.showModel = false;
        model.ChestR.showModel = false;
        model.Saddle.showModel = false;
        model.Saddleback.showModel = false;
        model.StirrupIronL.showModel = false;
        model.StirrupIronR.showModel = false;
        model.SaddleFront.showModel = false;
        model.StirrupL.showModel = false;
        model.StirrupR.showModel = false;
    }

    public static void hideHippocampusTack(ModelHippocampus model) {
        model.NoseBand.showModel = false;
        model.ReinL.showModel = false;
        model.ReinR.showModel = false;
        model.ChestL.showModel = false;
        model.ChestR.showModel = false;
        model.Saddle.showModel = false;
        model.Saddleback.showModel = false;
        model.StirrupIronL.showModel = false;
        model.StirrupIronR.showModel = false;
        model.SaddleFront.showModel = false;
        model.StirrupL.showModel = false;
        model.StirrupR.showModel = false;
    }

    public static void freezeVanilla(EntityRenderState state) {
        if (state instanceof LivingEntityRenderState living) {
            living.walkAnimationPos = 0.0F;
            living.walkAnimationSpeed = 0.0F;
            living.ageInTicks = -0.1F;
            living.yRot = 0.0F;
            living.xRot = 0.0F;
        }
    }

    private static Identifier stage(int index) {
        return Identifier.withDefaultNamespace("textures/block/destroy_stage_" + index + ".png");
    }
}

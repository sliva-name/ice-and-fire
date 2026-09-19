package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.client.render.entity.GhostRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGhost;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ParticleGhostAppearance extends Particle implements IafAppearanceParticle {
    private final ModelGhost citadel = new ModelGhost(0.0F);
    private final EntityModel<GhostRenderState> model = citadel.asEntityModel();
    private final int ghost;
    private final boolean fromLeft;

    public ParticleGhostAppearance(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, int ghost) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.gravity = 0.0F;
        this.lifetime = 15;
        this.ghost = ghost;
        this.fromLeft = worldIn.getRandom().nextBoolean();
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return IafParticleRenderTypes.APPEARANCE;
    }

    @Override
    public Optional<IafAppearanceParticleGroup.Instance> extract(Camera camera, float partialTick) {
        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return Optional.empty();
        }
        Entity entity = this.level.getEntity(this.ghost);
        if (!(entity instanceof EntityGhost ghostEntity)) {
            return Optional.empty();
        }
        float ageScale = (this.age + partialTick) / this.lifetime;
        float alpha = 0.05F + 0.5F * Mth.sin(ageScale * (float) Math.PI);
        PoseStack poses = new PoseStack();
        poses.mulPose(camera.rotation());
        if (this.fromLeft) {
            poses.mulPose(Axis.YN.rotationDegrees(150.0F * ageScale - 60.0F));
            poses.mulPose(Axis.ZN.rotationDegrees(150.0F * ageScale - 60.0F));
        } else {
            poses.mulPose(Axis.YP.rotationDegrees(150.0F * ageScale - 60.0F));
            poses.mulPose(Axis.ZP.rotationDegrees(150.0F * ageScale - 60.0F));
        }
        poses.scale(-1.0F, -1.0F, 1.0F);
        poses.translate(0.0D, 0.3F, 1.25D);

        GhostRenderState state = new GhostRenderState();
        state.animation = GhostRenderState.SCARE;
        state.animationTick = ghostEntity.getAnimationTick();
        state.partialTick = partialTick;
        state.ageInTicks = ghostEntity.tickCount + partialTick;
        state.color = ghostEntity.getColor();
        return Optional.of(new IafAppearanceParticleGroup.Instance(
            this.model, state, poses,
            IafRenderType.getGhost(RenderGhost.getGhostOverlayForType(ghostEntity.getColor())),
            ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F), 240));
    }
}

package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.client.model.ModelSiren;
import com.github.alexthe666.iceandfire.client.render.entity.RenderSiren;
import com.github.alexthe666.iceandfire.client.render.entity.SirenRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ParticleSirenAppearance extends Particle implements IafAppearanceParticle {
    private final ModelSiren citadel = new ModelSiren();
    private final EntityModel<SirenRenderState> model = citadel.asEntityModel();
    private final int sirenType;

    public ParticleSirenAppearance(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, int sirenType) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.gravity = 0.0F;
        this.lifetime = 30;
        this.sirenType = sirenType;
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return IafParticleRenderTypes.APPEARANCE;
    }

    @Override
    public Optional<IafAppearanceParticleGroup.Instance> extract(Camera camera, float partialTick) {
        float ageScale = (this.age + partialTick) / this.lifetime;
        float alpha = 0.05F + 0.5F * Mth.sin(ageScale * (float) Math.PI);
        PoseStack poses = new PoseStack();
        poses.mulPose(camera.rotation());
        poses.mulPose(Axis.XP.rotationDegrees(150.0F * ageScale - 60.0F));
        poses.scale(-1.0F, -1.0F, 1.0F);
        poses.translate(0.0D, -1.101F, 1.5D);
        SirenRenderState state = new SirenRenderState();
        state.singing = true;
        state.singProgress = 20.0F;
        state.singingPose = this.sirenType % 3;
        state.onGround = true;
        state.ageInTicks = this.age + partialTick;
        return Optional.of(new IafAppearanceParticleGroup.Instance(
            this.model, state, poses,
            RenderTypes.entityTranslucent(RenderSiren.getSirenOverlayTexture(this.sirenType)),
            ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F), 15728880));
    }
}

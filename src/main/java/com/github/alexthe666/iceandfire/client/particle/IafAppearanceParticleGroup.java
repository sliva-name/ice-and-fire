package com.github.alexthe666.iceandfire.client.particle;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;

public class IafAppearanceParticleGroup extends ParticleGroup<net.minecraft.client.particle.Particle> {
    public IafAppearanceParticleGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTickTime) {
        return new State(this.particles.stream()
            .map(particle -> particle instanceof IafAppearanceParticle appearance
                ? appearance.extract(camera, partialTickTime)
                : Optional.<Instance>empty())
            .flatMap(Optional::stream)
            .toList());
    }

    public record Instance(Model<?> model, Object state, PoseStack pose, RenderType renderType, int color, int light) {
        @SuppressWarnings("unchecked")
        void submit(SubmitNodeCollector collector) {
            collector.submitModel((Model<Object>) model, state, pose, renderType, light,
                OverlayTexture.NO_OVERLAY, color, null, 0, null);
        }
    }

    private record State(List<Instance> instances) implements ParticleGroupRenderState {
        @Override
        public void submit(SubmitNodeCollector collector, CameraRenderState camera) {
            for (Instance instance : instances) {
                instance.submit(collector);
            }
        }
    }
}

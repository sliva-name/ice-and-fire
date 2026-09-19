package com.github.alexthe666.iceandfire.client.particle;

import java.util.Optional;
import net.minecraft.client.Camera;

interface IafAppearanceParticle {
    Optional<IafAppearanceParticleGroup.Instance> extract(Camera camera, float partialTick);
}

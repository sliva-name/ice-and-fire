package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.client.render.entity.CockatriceRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import net.minecraft.world.phys.Vec3;

public class CockatriceBeamRender {

    public static final RenderType TEXTURE_BEAM = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath("iceandfire", "textures/models/cockatrice/beam.png"));

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, PoseStack.Pose pose, float x, float y, float z, int red, int green, int blue, float u, float v) {
        buffer.addVertex(matrix, x, y, z).setColor(red, green, blue, 255).setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public static void render(CockatriceRenderState entityIn, EntityRenderState targetEntity, PoseStack matrixStackIn, SubmitNodeCollector collector) {
        float f = entityIn.attackAnimationScale;
        float f1 = entityIn.beamTime;
        float f2 = f1 * 0.5F % 1.0F;
        float f3 = entityIn.eyeHeight;
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, f3, 0.0D);
        Vec3 Vector3d = getPosition(targetEntity, (double) targetEntity.boundingBoxHeight * 0.5D);
        Vec3 Vector3d1 = getPosition(entityIn, f3);
        Vec3 Vector3d2 = Vector3d.subtract(Vector3d1);
        float f4 = (float) (Vector3d2.length() + 1.0D);
        Vector3d2 = Vector3d2.normalize();
        float f5 = (float) Math.acos(Vector3d2.y);
        float f6 = (float) Math.atan2(Vector3d2.z, Vector3d2.x);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
        int i = 1;
        float f7 = f1 * 0.05F * -1.5F;
        float f8 = f * f;
        int j = 64 + (int) (f8 * 191.0F);
        int k = 32 + (int) (f8 * 191.0F);
        int l = 128 - (int) (f8 * 64.0F);
        float f9 = 0.2F;
        float f10 = 0.282F;
        float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
        float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
        float f13 = Mth.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f14 = Mth.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
        float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
        float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
        float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
        float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
        float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
        float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
        float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
        float f23 = Mth.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f24 = Mth.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f25 = Mth.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f26 = Mth.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f27 = 0.0F;
        float f28 = 0.4999F;
        float f29 = -1.0F + f2;
        float f30 = f4 * 2.5F + f29;
        // Capture parity before deferred submission; the callback reads no mutable render state.
        boolean evenTick = entityIn.tickCount % 2 == 0;
        collector.submitCustomGeometry(matrixStackIn, TEXTURE_BEAM, (pose, ivertexbuilder) -> {
            Matrix4f matrix4f = pose.pose();
            vertex(ivertexbuilder, matrix4f, pose, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(ivertexbuilder, matrix4f, pose, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(ivertexbuilder, matrix4f, pose, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(ivertexbuilder, matrix4f, pose, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(ivertexbuilder, matrix4f, pose, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(ivertexbuilder, matrix4f, pose, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(ivertexbuilder, matrix4f, pose, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(ivertexbuilder, matrix4f, pose, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (evenTick) {
                f31 = 0.5F;
            }

            vertex(ivertexbuilder, matrix4f, pose, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(ivertexbuilder, matrix4f, pose, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(ivertexbuilder, matrix4f, pose, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(ivertexbuilder, matrix4f, pose, f15, f4, f16, j, k, l, 0.5F, f31);
        });
        matrixStackIn.popPose();
    }

    private static Vec3 getPosition(EntityRenderState state, double yOffset) {
        return new Vec3(state.x, state.y + yOffset, state.z);
    }

    /**
     * Client overlay path for {@code MiscProperties.getTargetedBy}: same beam
     * geometry as the cockatrice renderer, filled from the live caster/target.
     */
    public static void renderFromEntities(net.minecraft.world.entity.LivingEntity caster, net.minecraft.world.entity.LivingEntity target, PoseStack matrixStackIn, SubmitNodeCollector collector, float partialTick) {
        CockatriceRenderState casterState = new CockatriceRenderState();
        casterState.x = caster.getX();
        casterState.y = caster.getY();
        casterState.z = caster.getZ();
        casterState.eyeHeight = caster.getEyeHeight();
        casterState.boundingBoxHeight = caster.getBbHeight();
        casterState.tickCount = caster.tickCount;
        casterState.attackAnimationScale = caster instanceof com.github.alexthe666.iceandfire.entity.EntityCockatrice cockatrice
            ? cockatrice.getAttackAnimationScale(partialTick)
            : 1.0F;
        casterState.beamTime = caster.tickCount + partialTick;
        EntityRenderState targetState = new EntityRenderState();
        targetState.x = target.getX();
        targetState.y = target.getY();
        targetState.z = target.getZ();
        targetState.boundingBoxHeight = target.getBbHeight();
        render(casterState, targetState, matrixStackIn, collector);
    }

}

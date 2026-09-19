package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class RenderFrozenState {
    private static final Identifier TEXTURE_0 = Identifier.withDefaultNamespace("textures/block/frosted_ice_0.png");
    private static final Identifier TEXTURE_1 = Identifier.withDefaultNamespace("textures/block/frosted_ice_1.png");
    private static final Identifier TEXTURE_2 = Identifier.withDefaultNamespace("textures/block/frosted_ice_2.png");
    private static final Identifier TEXTURE_3 = Identifier.withDefaultNamespace("textures/block/frosted_ice_3.png");

    public static void render(LivingEntity entity, PoseStack matrixStack, SubmitNodeCollector bufferIn, int light) {
        float sideExpand = -0.125F;
        float sideExpandY = 0.325F;
        AABB axisalignedbb1 = new AABB(-entity.getBbWidth() / 2F - sideExpand, 0, -entity.getBbWidth() / 2F - sideExpand,
            entity.getBbWidth() / 2F + sideExpand, entity.getBbHeight() + sideExpandY, entity.getBbWidth() / 2F + sideExpand);
        matrixStack.pushPose();
        renderMovingAABB(axisalignedbb1, matrixStack, bufferIn, entity, light, 255);
        matrixStack.popPose();
    }

    private static Identifier getIceTexture(int ticksFrozen) {
        if (ticksFrozen < 100) {
            if (ticksFrozen < 50) {
                if (ticksFrozen < 20) {
                    return TEXTURE_3;
                }
                return TEXTURE_2;
            }
            return TEXTURE_1;
        }
        return TEXTURE_0;
    }

    public static void renderMovingAABB(AABB boundingBox, PoseStack stack, SubmitNodeCollector bufferIn, LivingEntity entity, int light, int alpha) {
        RenderType rendertype = IafRenderType.getIce(getIceTexture(FrozenProperties.ticksUntilUnfrozen(entity)));
        bufferIn.submitCustomGeometry(stack, rendertype, (pose, vertexbuffer) -> {
            Matrix4f matrix4f = pose.pose();
        float maxX = (float) boundingBox.maxX * 0.425F;
        float minX = (float) boundingBox.minX * 0.425F;
        float maxY = (float) boundingBox.maxY * 0.425F;
        float minY = (float) boundingBox.minY * 0.425F;
        float maxZ = (float) boundingBox.maxZ * 0.425F;
        float minZ = (float) boundingBox.minZ * 0.425F;

        float maxU = maxZ - minZ;
        float maxV = maxY - minY;
        float minU = minZ - maxZ;
        float minV = minY - maxY;
        // X+
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(1.0F, 0.0F, 0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(1.0F, 0.0F, 0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(1.0F, 0.0F, 0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(1.0F, 0.0F, 0F);

        // X-
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(-1.0F, 0.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(-1.0F, 0.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(-1.0F, 0.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(-1.0F, 0.0F, 0.0F);


        maxU = maxX - minX;
        maxV = maxY - minY;
        minU = minX - maxX;
        minV = minY - maxY;
        // Z-
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, -1.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, -1.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, -1.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, -1.0F);

        // Z+
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 0.0F, 1.0F);


        maxU = maxZ - minZ;
        maxV = maxX - minX;
        minU = minZ - maxZ;
        minV = minX - maxX;
        // Y+
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);

        // Y-
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).setColor(255, 255, 255, alpha).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        vertexbuffer.addVertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).setColor(255, 255, 255, alpha).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, -1.0F, 0.0F);
        });
    }
}

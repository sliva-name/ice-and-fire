package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderAvatarEvent;

import java.util.UUID;

public class PlayerRenderEvents {
    public Identifier redTex = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/cape_fire.png");
    public Identifier redElytraTex = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/elytra_fire.png");
    public Identifier blueTex = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/cape_ice.png");
    public Identifier blueElytraTex = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/elytra_ice.png");
    public Identifier betaTex = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/cape_beta.png");
    public Identifier betaElytraTex = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/elytra_beta.png");

    public UUID[] redcapes = new UUID[]{
            UUID.fromString("59efccaf-902d-45da-928a-5a549b9fd5e0"),
            UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c")
    };
    public UUID[] bluecapes = new UUID[]{
            UUID.fromString("0ed918c8-d612-4360-b711-cd415671356f"),
            UUID.fromString("5d43896a-06a0-49fb-95c5-38485c63667f")};
    public UUID[] betatesters = new UUID[]{
    };

    public static void register() {
        RenderAvatarEvent.Pre.BUS.addListener(new PlayerRenderEvents()::playerRender);
    }

    public void playerRender(RenderAvatarEvent.Pre event) {
        AvatarRenderState state = event.getState();
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(ServerEvents.ALEX_UUID)) {
            event.getPoseStack().pushPose();
            float f2 = ((float) state.ageInTicks - 1 + 0);
            float f4 = (f2 / 20.0F) * (180F / (float) Math.PI);
            event.getPoseStack().translate(0.0F, state.boundingBoxHeight * 1.25F, 0.0F);
            event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f4));
            event.getPoseStack().pushPose();
            net.minecraft.client.renderer.item.ItemStackRenderState itemState = new net.minecraft.client.renderer.item.ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                itemState,
                new ItemStack(IafItemRegistry.WEEZER_BLUE_ALBUM.get()),
                ItemDisplayContext.GROUND,
                Minecraft.getInstance().level,
                null,
                0);
            itemState.submit(event.getPoseStack(), event.getNodeCollector(), event.getState().lightCoords,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
            event.getPoseStack().popPose();
            event.getPoseStack().popPose();
        }
    }
}

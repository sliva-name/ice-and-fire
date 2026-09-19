package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.ClientProxy;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.client.gui.IceAndFireMainMenu;
import com.github.alexthe666.iceandfire.client.particle.CockatriceBeamRender;
import com.github.alexthe666.iceandfire.client.render.entity.RenderChain;
import com.github.alexthe666.iceandfire.client.render.pathfinding.RenderPath;
import com.github.alexthe666.iceandfire.client.render.tile.RenderFrozenState;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.github.alexthe666.iceandfire.entity.props.MiscProperties;
import com.github.alexthe666.iceandfire.entity.props.SirenProperties;
import com.github.alexthe666.iceandfire.entity.util.ICustomMoveController;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
import com.github.alexthe666.iceandfire.item.IafArmorIdentity;
import com.github.alexthe666.iceandfire.item.IafArmorMaterial;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.Pathfinding;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import java.util.List;
import java.util.Random;

public class ClientEvents {

    private static final Identifier SIREN_SHADER = Identifier.parse("iceandfire:shaders/post/siren.json");

    private final Random rand = new Random();

    public static void register() {
        ClientEvents events = new ClientEvents();
        ViewportEvent.ComputeCameraAngles.BUS.addListener(events::renderWorldLastEvent);
        ViewportEvent.ComputeCameraAngles.BUS.addListener(events::onCameraSetup);
        LivingEvent.LivingTickEvent.BUS.addListener(events::onLivingUpdate);
        RenderLivingEvent.Pre.BUS.addListener(events::onPreRenderLiving);
        RenderLivingEvent.Post.BUS.addListener(events::onPostRenderLiving);
        ScreenEvent.Opening.BUS.addListener(events::onGuiOpened);
        EntityMountEvent.BUS.addListener(events::onEntityMount);
    }

    public void renderWorldLastEvent(ViewportEvent.ComputeCameraAngles event) {
        if (Pathfinding.isDebug()) {
            RenderPath.debugDraw(event.getPartialTick(), new PoseStack());
        }
    }

    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Player player = Minecraft.getInstance().player;
        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof EntityDragonBase) {
                int currentView = IceAndFire.PROXY.getDragon3rdPersonView();
                float scale = ((EntityDragonBase) player.getVehicle()).getRenderSize() / 3;
                if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_BACK ||
                        Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) {
                    if (currentView == 1) {
                        ClientProxy.DRAGON_CAMERA_PULLBACK = scale * 1.2F;
                    } else if (currentView == 2) {
                        ClientProxy.DRAGON_CAMERA_PULLBACK = scale * 3F;
                    } else if (currentView == 3) {
                        ClientProxy.DRAGON_CAMERA_PULLBACK = scale * 5F;
                    }
                }
            }
        }
    }

    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getEntity() instanceof ICustomMoveController) {
            Entity entity = event.getEntity();
            ICustomMoveController moveController = ((Entity & ICustomMoveController) event.getEntity());
            if (entity.getVehicle() != null && entity.getVehicle() == mc.player) {
                byte previousState = moveController.getControlState();
                moveController.dismount(mc.options.keyShift.isDown());
                byte controlState = moveController.getControlState();
                if (controlState != previousState) {
                    IceAndFire.sendMSGToServer(new MessageDragonControl(entity.getId(), controlState, entity.getX(), entity.getY(), entity.getZ()));
                }
            }
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.level().isClientSide()) {

                if (player.getVehicle() instanceof ICustomMoveController) {
                    Entity entity = player.getVehicle();
                    ICustomMoveController moveController = ((Entity & ICustomMoveController) player.getVehicle());
                    byte previousState = moveController.getControlState();
                    moveController.up(mc.options.keyJump.isDown());
                    moveController.down(IafKeybindRegistry.dragon_down.isDown());
                    moveController.attack(IafKeybindRegistry.dragon_strike.isDown());
                    moveController.dismount(mc.options.keyShift.isDown());
                    moveController.strike(IafKeybindRegistry.dragon_fireAttack.isDown());
                    byte controlState = moveController.getControlState();
                    if (controlState != previousState) {
                        IceAndFire.sendMSGToServer(new MessageDragonControl(entity.getId(), controlState, entity.getX(), entity.getY(), entity.getZ()));
                    }
                }
            }
            if (player.level().isClientSide() && IafKeybindRegistry.dragon_change_view.isDown()) {
                int currentView = IceAndFire.PROXY.getDragon3rdPersonView();
                if (currentView + 1 > 3) {
                    currentView = 0;
                } else {
                    currentView++;
                }
                IceAndFire.PROXY.setDragon3rdPersonView(currentView);
            }

            if (player.level().isClientSide()) {
                GameRenderer renderer = Minecraft.getInstance().gameRenderer;
                EntitySiren siren = SirenProperties.getSiren(player);

                if (IafConfig.sirenShader && siren == null && renderer != null && SIREN_SHADER.equals(renderer.currentPostEffect())) {
                    renderer.clearPostEffect();
                }

                if (siren == null)
                    return;

                final boolean isCharmed = SirenProperties.isCharmed(player);

                if (IafConfig.sirenShader && !isCharmed && renderer != null && SIREN_SHADER.equals(renderer.currentPostEffect())) {
                    renderer.clearPostEffect();
                }

                if (isCharmed) {
                    if (player.level().isClientSide() && rand.nextInt(40) == 0) {
                        IceAndFire.PROXY.spawnParticle(EnumParticles.Siren_Appearance, player.getX(), player.getY(), player.getZ(), siren.getHairColor(), 0, 0);
                    }

                    // 26.1 GameRenderer has currentPostEffect/clearPostEffect but no loadEffect(Identifier).
                    // Charm particles still spawn; post-chain apply needs a later 26.1 hook.

                }

            }
        }
    }

    public void onPreRenderLiving(RenderLivingEvent.Pre event) {
        LivingEntity entity = findRenderedEntity(event.getState());
        if (entity == null) {
            return;
        }
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof IafArmorIdentity && ((IafArmorIdentity) stack.getItem()).iafMaterial() instanceof IafArmorMaterial) {
                switch (slot) {
                    case HEAD -> {
                        if (event.getRenderer().getModel() instanceof HumanoidModel<?> humanoidModel) {
                            humanoidModel.hat.visible = false;
                        }
                    }
                    case CHEST -> {
                        if (event.getRenderer().getModel() instanceof PlayerModel playerModel) {
                            playerModel.jacket.visible = false;
                            playerModel.leftSleeve.visible = false;
                            playerModel.rightSleeve.visible = false;
                        }
                    }
                    case LEGS -> {
                        if (event.getRenderer().getModel() instanceof PlayerModel playerModel) {
                            playerModel.leftPants.visible = false;
                            playerModel.rightPants.visible = false;
                        }
                    }
                    case FEET -> {
                        if (event.getRenderer().getModel() instanceof PlayerModel playerModel) {
                            playerModel.leftLeg.visible = false;
                            playerModel.rightLeg.visible = false;
                        }
                    }
                }

            }
        }

    }

    public void onPostRenderLiving(RenderLivingEvent.Post event) {
        LivingEntity entity = findRenderedEntity(event.getState());
        if (entity == null) {
            return;
        }
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        MiscProperties.getTargetedBy(entity).forEach(caster -> {
            CockatriceBeamRender.renderFromEntities(caster, entity, event.getPoseStack(), event.getNodeCollector(), partialTick);
        });
        if (FrozenProperties.isFrozen(entity)) {
            RenderFrozenState.render(entity, event.getPoseStack(), event.getNodeCollector(), event.getState().lightCoords);
        }
        RenderChain.render(entity, partialTick, event.getPoseStack(), event.getNodeCollector(), event.getState().lightCoords);
    }

    private static LivingEntity findRenderedEntity(net.minecraft.client.renderer.entity.state.LivingEntityRenderState state) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(state.x - 0.25, state.y - 0.25, state.z - 0.25, state.x + 0.25, state.y + 0.25, state.z + 0.25);
        LivingEntity match = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : mc.level.getEntities(null, box)) {
            if (candidate instanceof LivingEntity living && living.getType() == state.entityType) {
                double dist = living.distanceToSqr(state.x, state.y, state.z);
                if (dist < best) {
                    best = dist;
                    match = living;
                }
            }
        }
        return match;
    }

    public void onGuiOpened(ScreenEvent.Opening event) {
        if (IafConfig.customMainMenu && event.getNewScreen() instanceof TitleScreen && !(event.getNewScreen() instanceof IceAndFireMainMenu)) {
            event.setNewScreen(new IceAndFireMainMenu());
        }
    }

    // TODO: add this to client side config
    public final boolean AUTO_ADAPT_3RD_PERSON = true;

    public void onEntityMount(EntityMountEvent event) {

        if (event.getEntityBeingMounted() instanceof EntityDragonBase && event.getEntityBeingMounted().level().isClientSide() && event.getEntityMounting() == Minecraft.getInstance().player) {
            EntityDragonBase dragon = (EntityDragonBase) event.getEntityBeingMounted();
            if (dragon.isTame() && dragon.isOwnedBy(Minecraft.getInstance().player)) {
                if (AUTO_ADAPT_3RD_PERSON) {
                    // Auto adjust 3rd person camera's according to dragon's size
                    IceAndFire.PROXY.setDragon3rdPersonView(2);
                }
                if (IafConfig.dragonAuto3rdPerson) {
                    if (event.isDismounting()) {
                        Minecraft.getInstance().options.setCameraType(CameraType.values()[IceAndFire.PROXY.getPreviousViewType()]);
                    } else {
                        IceAndFire.PROXY.setPreviousViewType(Minecraft.getInstance().options.getCameraType().ordinal());
                        Minecraft.getInstance().options.setCameraType(CameraType.values()[1]);
                    }
                }
            }
        }
    }
}
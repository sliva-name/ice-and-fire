package com.github.alexthe666.iceandfire.client;

import com.github.alexthe666.iceandfire.CommonProxy;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexAddRoom;
import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexStaff;
import com.github.alexthe666.iceandfire.client.gui.bestiary.GuiBestiary;
import com.github.alexthe666.iceandfire.client.model.item.IafItemModelRegistry;
import com.github.alexthe666.iceandfire.client.particle.*;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDragonArmor;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
import com.github.alexthe666.iceandfire.event.ClientEvents;
import com.github.alexthe666.iceandfire.event.PlayerRenderEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientProxy extends CommonProxy {

    public static Set<UUID> currentDragonRiders = new HashSet<UUID>();
    /** 1.18 CameraSetup move(-getMaxZoom(scale*N)) argument, applied when a camera hook exists. */
    public static float DRAGON_CAMERA_PULLBACK = 0.0F;
    private static MyrmexHive referedClientHive = null;
    private int previousViewType = 0;
    private int thirdPersonViewDragon = 0;
    private Entity referencedMob = null;
    private BlockEntity referencedTE = null;

    public static MyrmexHive getReferedClientHive() {
        return referedClientHive;
    }

    @Override
    public void setReferencedHive(MyrmexHive hive) {
        referedClientHive = hive;
    }

    @Override
    public void init() {
        // Before the first resource reload parses assets/iceandfire/items/*.json.
        IafItemModelRegistry.register();
        RegisterKeyMappingsEvent.BUS.addListener(IafKeybindRegistry::register);
        PlayerRenderEvents.register();
        ClientEvents.register();
    }

    @Override
    public void postInit() {

    }

    @Override
    public void clientInit() {
        super.clientInit();
        IafClientSetup.clientInit();
    }

    @Override
    public void spawnDragonParticle(final EnumParticles name, double x, double y, double z, double motX, double motY, double motZ, EntityDragonBase entityDragonBase) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        net.minecraft.client.particle.Particle particle = null;
        if (name == EnumParticles.DragonFire) {
            particle = new ParticleDragonFlame(world, x, y, z, motX, motY, motZ, entityDragonBase, 0);
        } else if (name == EnumParticles.DragonIce) {
            particle = new ParticleDragonFrost(world, x, y, z, motX, motY, motZ, entityDragonBase, 0);
        }
        if (particle != null) {
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    @Override
    public void spawnParticle(final EnumParticles name, double x, double y, double z, double motX, double motY, double motZ, float size) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        net.minecraft.client.particle.Particle particle = null;
        switch (name) {
            case DragonFire:
                particle = new ParticleDragonFlame(world, x, y, z, motX, motY, motZ, size);
                break;
            case DragonIce:
                particle = new ParticleDragonFrost(world, x, y, z, motX, motY, motZ, size);
                break;
            case Dread_Torch:
                particle = new ParticleDreadTorch(world, x, y, z, motX, motY, motZ, size);
                break;
            case Dread_Portal:
                particle = new ParticleDreadPortal(world, x, y, z, motX, motY, motZ, size);
                break;
            case Blood:
                particle = new ParticleBlood(world, x, y, z);
                break;
            case If_Pixie:
                particle = new ParticlePixieDust(world, x, y, z, (float) motX, (float) motY, (float) motZ);
                break;
            case Siren_Appearance:
                particle = new ParticleSirenAppearance(world, x, y, z, (int) motX);
                break;
            case Ghost_Appearance:
                particle = new ParticleGhostAppearance(world, x, y, z, (int) motX);
                break;
            case Siren_Music:
                particle = new ParticleSirenMusic(world, x, y, z, motX, motY, motZ, 1);
                break;
            case Serpent_Bubble:
                particle = new ParticleSerpentBubble(world, x, y, z, motX, motY, motZ, 1);
                break;
            case Hydra:
                particle = new ParticleHydraBreath(world, x, y, z, (float) motX, (float) motY, (float) motZ);
                break;
            default:
                break;
        }
        if (particle != null) {
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }

    @Override
    public void openBestiaryGui(ItemStack book) {
        Minecraft.getInstance().setScreen(new GuiBestiary(book));
    }

    @Override
    public void openMyrmexStaffGui(ItemStack staff) {
        Minecraft.getInstance().setScreen(new GuiMyrmexStaff(staff));
    }

    @Override
    public void openMyrmexAddRoomGui(ItemStack staff, BlockPos pos, Direction facing) {
        Minecraft.getInstance().setScreen(new GuiMyrmexAddRoom(staff, pos, facing));
    }

    @Override
    public Object getFontRenderer() {
        return Minecraft.getInstance().font;
    }

    @Override
    public int getDragon3rdPersonView() {
        return thirdPersonViewDragon;
    }

    @Override
    public void setDragon3rdPersonView(int view) {
        thirdPersonViewDragon = view;
    }

    @Override
    public int getPreviousViewType() {
        return previousViewType;
    }

    @Override
    public void setPreviousViewType(int view) {
        previousViewType = view;
    }

    @Override
    public void updateDragonArmorRender(String clear) {
        LayerDragonArmor.clearCache(clear);
    }

    @Override
    public boolean shouldSeeBestiaryContents() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344);
    }

    @Override
    public Entity getReferencedMob() {
        return referencedMob;
    }

    @Override
    public void setReferencedMob(Entity dragonBase) {
        referencedMob = dragonBase;
    }

    @Override
    public BlockEntity getRefrencedTE() {
        return referencedTE;
    }

    @Override
    public void setRefrencedTE(BlockEntity tileEntity) {
        referencedTE = tileEntity;
    }

    @Override
    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }
}

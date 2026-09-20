package com.github.alexthe666.iceandfire.client;


import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.gui.IafGuiRegistry;
import com.github.alexthe666.iceandfire.client.model.*;
import com.github.alexthe666.iceandfire.client.model.animator.FireDragonTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.animator.IceDragonTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.animator.LightningTabulaDragonAnimator;
import com.github.alexthe666.iceandfire.client.model.animator.SeaSerpentTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.*;
import com.github.alexthe666.iceandfire.client.render.entity.*;
import com.github.alexthe666.iceandfire.client.render.tile.*;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry;
import com.github.alexthe666.iceandfire.client.particle.IafAppearanceParticleGroup;
import com.github.alexthe666.iceandfire.client.particle.IafParticleRenderTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = IceAndFire.MODID)
public class IafClientSetup {

    public static TabulaModel<DragonRenderState> FIRE_DRAGON_BASE_MODEL;
    public static TabulaModel<DragonRenderState> ICE_DRAGON_BASE_MODEL;
    public static TabulaModel<SeaSerpentRenderState> SEA_SERPENT_BASE_MODEL;
    public static TabulaModel<DragonRenderState> LIGHTNING_DRAGON_BASE_MODEL;
    public static final Identifier GHOST_CHEST_LOCATION = Identifier.fromNamespaceAndPath(IceAndFire.MODID, "models/ghost/ghost_chest");
    public static final Identifier GHOST_CHEST_LEFT_LOCATION = Identifier.fromNamespaceAndPath(IceAndFire.MODID, "models/ghost/ghost_chest_left");
    public static final Identifier GHOST_CHEST_RIGHT_LOCATION = Identifier.fromNamespaceAndPath(IceAndFire.MODID, "models/ghost/ghost_chest_right");


    public static void clientInit() {
        loadClientModels();
        ParticleEngine.registerParticleGroup(IafParticleRenderTypes.APPEARANCE, IafAppearanceParticleGroup::new);
        EntityRenderers.register(IafEntityRegistry.FIRE_DRAGON.get(), x -> new RenderDragonBase(x, FIRE_DRAGON_BASE_MODEL, 0));
        EntityRenderers.register(IafEntityRegistry.ICE_DRAGON.get(), manager -> new RenderDragonBase(manager, ICE_DRAGON_BASE_MODEL, 1));
        EntityRenderers.register(IafEntityRegistry.LIGHTNING_DRAGON.get(), manager -> new RenderLightningDragon(manager, LIGHTNING_DRAGON_BASE_MODEL, 2));
        EntityRenderers.register(IafEntityRegistry.DRAGON_EGG.get(), RenderDragonEgg::new);
        EntityRenderers.register(IafEntityRegistry.DRAGON_ARROW.get(), RenderDragonArrow::new);
        EntityRenderers.register(IafEntityRegistry.DRAGON_SKULL.get(), manager -> new RenderDragonSkull(manager, FIRE_DRAGON_BASE_MODEL, ICE_DRAGON_BASE_MODEL, LIGHTNING_DRAGON_BASE_MODEL));
        EntityRenderers.register(IafEntityRegistry.FIRE_DRAGON_CHARGE.get(), manager -> new RenderDragonFireCharge(manager, true));
        EntityRenderers.register(IafEntityRegistry.ICE_DRAGON_CHARGE.get(), manager -> new RenderDragonFireCharge(manager, false));
        EntityRenderers.register(IafEntityRegistry.LIGHTNING_DRAGON_CHARGE.get(), RenderDragonLightningCharge::new);
        EntityRenderers.register(IafEntityRegistry.HIPPOGRYPH_EGG.get(), ThrownItemRenderer::new);
        EntityRenderers.register(IafEntityRegistry.HIPPOGRYPH.get(), RenderHippogryph::new);
        EntityRenderers.register(IafEntityRegistry.STONE_STATUE.get(), RenderStoneStatue::new);
        EntityRenderers.register(IafEntityRegistry.GORGON.get(), RenderGorgon::new);
        EntityRenderers.register(IafEntityRegistry.PIXIE.get(), RenderPixie::new);
        EntityRenderers.register(IafEntityRegistry.CYCLOPS.get(), RenderCyclops::new);
        EntityRenderers.register(IafEntityRegistry.SIREN.get(), RenderSiren::new);
        EntityRenderers.register(IafEntityRegistry.HIPPOCAMPUS.get(), RenderHippocampus::new);
        EntityRenderers.register(IafEntityRegistry.DEATH_WORM.get(), RenderDeathWorm::new);
        EntityRenderers.register(IafEntityRegistry.DEATH_WORM_EGG.get(), ThrownItemRenderer::new);
        EntityRenderers.register(IafEntityRegistry.COCKATRICE.get(), RenderCockatrice::new);
        EntityRenderers.register(IafEntityRegistry.COCKATRICE_EGG.get(), ThrownItemRenderer::new);
        EntityRenderers.register(IafEntityRegistry.STYMPHALIAN_BIRD.get(), RenderStymphalianBird::new);
        EntityRenderers.register(IafEntityRegistry.STYMPHALIAN_FEATHER.get(), RenderStymphalianFeather::new);
        EntityRenderers.register(IafEntityRegistry.STYMPHALIAN_ARROW.get(), RenderStymphalianArrow::new);
        EntityRenderers.register(IafEntityRegistry.TROLL.get(), RenderTroll::new);
        EntityRenderers.register(IafEntityRegistry.MYRMEX_WORKER.get(), manager -> new RenderMyrmexBase(manager, new ModelMyrmexWorker(), 0.5F));
        EntityRenderers.register(IafEntityRegistry.MYRMEX_SOLDIER.get(), manager -> new RenderMyrmexBase(manager, new ModelMyrmexSoldier(), 0.75F));
        EntityRenderers.register(IafEntityRegistry.MYRMEX_QUEEN.get(), manager -> new RenderMyrmexBase(manager, new ModelMyrmexQueen(), 1.25F));
        EntityRenderers.register(IafEntityRegistry.MYRMEX_EGG.get(), RenderMyrmexEgg::new);
        EntityRenderers.register(IafEntityRegistry.MYRMEX_SENTINEL.get(), manager -> new RenderMyrmexBase(manager, new ModelMyrmexSentinel(), 0.85F));
        EntityRenderers.register(IafEntityRegistry.MYRMEX_ROYAL.get(), manager -> new RenderMyrmexBase(manager, new ModelMyrmexRoyal(), 0.75F));
        EntityRenderers.register(IafEntityRegistry.MYRMEX_SWARMER.get(), manager -> new RenderMyrmexBase(manager, new ModelMyrmexRoyal(), 0.25F));
        EntityRenderers.register(IafEntityRegistry.AMPHITHERE.get(), RenderAmphithere::new);
        EntityRenderers.register(IafEntityRegistry.AMPHITHERE_ARROW.get(), RenderAmphithereArrow::new);
        EntityRenderers.register(IafEntityRegistry.SEA_SERPENT.get(), manager -> new RenderSeaSerpent(manager, SEA_SERPENT_BASE_MODEL));
        EntityRenderers.register(IafEntityRegistry.SEA_SERPENT_BUBBLES.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.SEA_SERPENT_ARROW.get(), RenderSeaSerpentArrow::new);
        EntityRenderers.register(IafEntityRegistry.CHAIN_TIE.get(), RenderChainTie::new);
        EntityRenderers.register(IafEntityRegistry.PIXIE_CHARGE.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.TIDE_TRIDENT.get(), RenderTideTrident::new);
        EntityRenderers.register(IafEntityRegistry.MOB_SKULL.get(), manager -> new RenderMobSkull(manager, SEA_SERPENT_BASE_MODEL));
        EntityRenderers.register(IafEntityRegistry.DREAD_SCUTTLER.get(), RenderDreadScuttler::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_GHOUL.get(), RenderDreadGhoul::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_BEAST.get(), RenderDreadBeast::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_THRALL.get(), RenderDreadThrall::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_LICH.get(), RenderDreadLich::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_LICH_SKULL.get(), RenderDreadLichSkull::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_KNIGHT.get(), RenderDreadKnight::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_QUEEN.get(), RenderDreadQueen::new);
        EntityRenderers.register(IafEntityRegistry.DREAD_HORSE.get(), RenderDreadHorse::new);
        EntityRenderers.register(IafEntityRegistry.HYDRA.get(), RenderHydra::new);
        EntityRenderers.register(IafEntityRegistry.HYDRA_BREATH.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.HYDRA_ARROW.get(), RenderHydraArrow::new);
        EntityRenderers.register(IafEntityRegistry.SLOW_MULTIPART.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.DRAGON_MULTIPART.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.CYCLOPS_MULTIPART.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.HYDRA_MULTIPART.get(), RenderNothing::new);
        EntityRenderers.register(IafEntityRegistry.GHOST.get(), RenderGhost::new);
        EntityRenderers.register(IafEntityRegistry.GHOST_SWORD.get(), RenderGhostSword::new);

        BlockEntityRenderers.register(IafTileEntityRegistry.PODIUM.get(), RenderPodium::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.IAF_LECTERN.get(), RenderLectern::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.EGG_IN_ICE.get(), RenderEggInIce::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.PIXIE_HOUSE.get(), RenderPixieHouse::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.PIXIE_JAR.get(), RenderJar::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.DREAD_PORTAL.get(), RenderDreadPortal::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.DREAD_SPAWNER.get(), RenderDreadSpawner::new);
        BlockEntityRenderers.register(IafTileEntityRegistry.GHOST_CHEST.get(), RenderGhostChest::new);

        // 1.18 injected PATTERN_DREAD into Sheets.BANNER_MATERIALS / SHIELD_MATERIALS.
        // 26.1 banner materials are registry-backed; custom patterns belong in datapack atlas entries.
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(IafGuiRegistry::register);
    }

    /**
     * Tabula geometry must exist before renderer factories run. A later
     * {@code FMLClientSetupEvent} queue is too late if 26.x constructs
     * renderers while registering them.
     */
    private static void loadClientModels() {
        if (FIRE_DRAGON_BASE_MODEL != null) {
            return;
        }
        EnumSeaSerpentAnimations.initializeSerpentModels();
        DragonAnimationsLibrary.register(EnumDragonPoses.values(), EnumDragonModelTypes.values());
        try {
            SEA_SERPENT_BASE_MODEL = new TabulaModel<>(TabulaModelHandlerHelper.loadTabulaModel("/assets/iceandfire/models/tabula/seaserpent/seaserpent"), new SeaSerpentTabulaModelAnimator());
            FIRE_DRAGON_BASE_MODEL = new TabulaModel<>(TabulaModelHandlerHelper.loadTabulaModel("/assets/iceandfire/models/tabula/firedragon/firedragon_Ground"), new FireDragonTabulaModelAnimator());
            ICE_DRAGON_BASE_MODEL = new TabulaModel<>(TabulaModelHandlerHelper.loadTabulaModel("/assets/iceandfire/models/tabula/icedragon/icedragon_Ground"), new IceDragonTabulaModelAnimator());
            LIGHTNING_DRAGON_BASE_MODEL = new TabulaModel<>(TabulaModelHandlerHelper.loadTabulaModel("/assets/iceandfire/models/tabula/lightningdragon/lightningdragon_Ground"), new LightningTabulaDragonAnimator());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Ice and Fire Tabula models", e);
        }
    }

}

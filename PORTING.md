# Minecraft 26.1 / Forge port status

## Status: implementation in progress, NOT a playable port

`port-26.1` compiles **all** Java files from `../src/main/java` against Forge
26.1-62.0.9 with Java 25, Gradle 9.3.1 and ForgeGradle 7.0.17. Incompatible
classes are not excluded. `:compileJava` is now **0 errors**. Both mod and
Citadel jar tasks still fail so an incomplete port cannot be distributed.
`:runServer` reached `Done` with **1933 recipes** (1515 vanilla + all 418
Ice and Fire recipes), **0** recipe/loot/advancement parse errors, and
**0** `/ERROR` lines. The client has not been started. GPU/animation/
statue/armor/tile play proof and official-name ATs are still unfinished.

Root Java sources are now being migrated. The original 1.18.2 build configuration
is retained, but its source compatibility is **no longer preserved**. Its earlier
successful compilation was a pre-migration baseline, not a check of today's code.

## Latest checkpoint: remapped datapack load (2026-09-19)

- Overlay loot tables now use 26.1 codecs: `looting_enchant` →
  `enchanted_count_increase` + `minecraft:looting`;
  `random_chance_with_looting` → `random_chance_with_enchanted_bonus`
  (unenchanted chance + linear `chance + multiplier`);
  `minecraft:alternative` → `any_of`; silk-touch `match_tool` uses
  `predicates.minecraft:enchantments`; `entity_properties.on_fire` →
  `predicate.flags.is_on_fire`; chest `enchant_with_levels` `treasure`
  → `options: #minecraft:on_random_loot`; `minecraft:chain` →
  `iron_chain`. Script: `.gradle/port-tools/patch_loot.py`.
- Recipe ingredients already use 26.1 strings. Missing Forge vanilla
  tags (`ingots/iron`, `string`, `feathers`, `storage_blocks/*`, …)
  are aliased to the vanilla item plus optional `#c:` tag. `minecraft:chain`
  in recipes remaps to `iron_chain`. Ghost-sword smithing is
  `smithing_transform` with optional template (same two items as 1.18).
  Advancement display icons use `id` instead of `item`.
- Dragon-forge results store `id`/`count` and only build `ItemStack`
  after component bind (`DragonForgeRecipe.ResultSpec`). `ItemStack.CODEC`
  / `new ItemStack` during recipe reload throws `Components not bound yet`
  and aborts the whole datapack.
- Evidence: `run/logs/latest.log` `Loaded 1933 recipes`, `Done (0.188s)`,
  0 iceandfire recipe/loot/advancement parse errors, 0 `/ERROR` lines.
  Gradle: `.gradle/port-tools/run-server-13.txt`. Compile:
  `.gradle/port-tools/compile-ids-8.txt`. Saved log:
  `.gradle/port-tools/run-server-13-latest.log`.
- Client (`:runClient`) reached `DONE` on NVIDIA GL 4.6 / LWJGL 3.4.1 and
  opened the existing integrated world. Crash after that was
  `EntityHippogryph` `TemptGoal` missing `minecraft:tempt_range`
  (1.18 used a hardcoded 10). Nested client events register on
  `RenderAvatarEvent.Pre.BUS` / `ViewportEvent.*` etc. (Forge 26.1
  rejects a single nested listener on `EVENT_BUS`). Loot function
  types and dummy structure pieces use `DeferredRegister` (client
  `enqueueWork` hits a frozen registry). Tabula `.tbl` loads from
  the Ice and Fire classloader and lowercase paths (26.1 pack
  scanner drops mixed-case names).
- Client is sitting on the title screen (`run-client-5`, OpenAL +
  atlases, 0 mixed-case tabula path errors, only vanilla Realms
  errors). A prior world join died on hippogryph `tempt_range`;
  that attribute is now 10. Fire-dragon cave placement crashed
  because `SWIMMING` was `defineId` on `EntityDragonBase` but only
  defined on the ice dragon; 26.1 requires every id in the class
  tree. In-world GPU/animation/statue/armor
  proof vs 1.18 is still open, as are JEI, official-name ATs,
  scribe houses, food extras, spawn-egg `ENTITY_DATA`, and jars.

## Earlier checkpoint: dedicated server start (2026-09-19)

- Block/item IDs use `Properties.setId` via `IafBlockRegistry.register` /
  `IafItemRegistry.register` ThreadLocals. Sea-serpent scale blocks go
  through the same helper. Block items register in `IafItemRegistry`
  clinit. Pixie-house tile valid-blocks drop the duplicate birch entry
  (`Set.of` rejects it). Dread-sword enchantability stays 0: sword
  tool/weapon/attributes are applied without `enchantable`. Spawn eggs
  keep a `RegistryObject` lookup (`IafSpawnEggItem`) instead of
  requiring `ENTITY_DATA` at item construction. Dread-knight shield
  `ItemStack` is built at equip time, not class init (26.1 holders are
  not component-bound during `EntityAttributeCreationEvent`).
- `:runServer` evidence: `run/logs/latest.log` prints
  `Done (1.481s)! For help, type "help"`, then
  `LOADED_FEATURES` / `LOADED_ENTITIES` and
  `Server empty for 60 seconds, pausing`. Gradle log:
  `.gradle/port-tools/run-server-9.txt`. Compile:
  `.gradle/port-tools/compile-ids-6.txt` (0 errors).
- Resources: overlay `mods.toml` / pack format 101 / mixins load.
  Tags stay singular (`item`/`block`/`entity_type`).
  `myrmex_harvestables` uses `minecraft:short_grass` (1.18
  `minecraft:grass`). `processResources` now excludes 1.18
  `recipes/` / `loot_tables/` / `advancements/`; overlay copies live
  under `recipe/` / `loot_table/` / `advancement/` with recipe
  `result.item` rewritten to `result.id` and 1.18
  `{item}/{tag}` ingredients rewritten to 26.1 strings
  (`"iceandfire:ash"`, `"#forge:ingots/silver"`). Creative tabs
  register on `Registries.CREATIVE_MODE_TAB` and refill the 1.18
  item/block split plus bestiary / hippogryph-egg / myrmex-egg
  variants. run-server-10 still logged 1515 recipes because it
  started before the ingredient rewrite; loot tables still fail
  (`minecraft:chain`, `random_chance_with_looting`,
  `looting_enchant`, `alternative`).
- Still open: client start and GPU proof vs 1.18; recipe reload
  after the ingredient rewrite; loot-table ID/condition remaps;
  JEI 26.1 API; official-name ATs; scribe village-house datapack;
  food meat/fast/effect; jars remain blocked for
  assemble/build/publish.

## Earlier checkpoint: leftover AI/items/render compile (2026-09-19)

- Targeting leftovers (`HippogryphAITarget`, `CockatriceAI*`, `DragonAITarget*`,
  `DeathWormAITarget`, `FlyingAITarget`, myrmex soldier/sentinel, Stymphalian)
  use `IafSelectors` / `Selector`. Cyclops melee uses the 1-arg
  `checkAndPerformAttack` and still stops at 6 blocks when blinded.
  Follow-owner teleports `snapTo`. Cockatrice follow drops the removed
  can-fly ctor flag.
- Item hover/arrows use 26.1 `TooltipContext` + `Consumer` and 4-arg
  `createArrow`. Lightning flesh `create`s on `ServerLevel` and `snapTo`s.
  Blindfold ticks `(stack, ServerLevel, entity, EquipmentSlot)` and still
  applies blindness on the head slot. Macuahuitl shield-disable is a
  leftover helper (`canDisableShield` is gone).
- Citadel property sync is `PropertiesNetwork.send(entity)` after writing
  the same citadel tag. Chain buffer yaw is `sampleYaw` lerp of prev/current
  (same interpolation `applyChain*` already used). Statue hollow mobs
  `EntityType.create(TagValueInput, level, LOAD).orElse(null)`. Sitting
  statues set `Pose.SITTING` and humanoid `isPassenger`.
- Nav `canNavigateGround` is public. Sea serpent nav returns false
  (water path). Sea serpent / death worm `killedEntity` still flip
  attack / heal-on-tame. Worldgen features `snapTo`; wandering cyclops
  sheep colors use `Sheep.getRandomSheepColor(world, pos)`.
- Incremental `:compileJava` is **0 errors** (was 133). Log:
  `.gradle/port-tools/compile-after-ai4.txt`. Jars stay blocked.
  Client/server have not been started.

## Earlier checkpoint: worldgen, path, leftover blocks/entities (2026-09-19)

- Worldgen helpers (`WorldGenRoost*`, caves, siren island, dread ruin)
  take `RandomSource`. Cave spheres use `ShapeBuilder` with
  `RandomSource`. Dragon cave origin is `ChunkPos.getMiddleBlockX/Z`.
  Dread portal processor is `processBlock` + `state()`/`pos()`; diamond
  still becomes the portal and brick crack odds stay 0.3 / 0.3 / 0.4.
- Pathfinding: `BlockPos.containing` / floored `Vec3i`, ladder
  `BlockState.canSurvive`, and `IafPathTypes` (subclass of
  `WalkNodeEvaluator`) for the protected vanilla path type. Chunk cache
  now supplies `enabledFeatures` and `registryAccess`.
- Podium / lectern / cocoon / jar / forge core+input+bricks / egg-in-ice
  / dread portal implement `codec()`, `useWithoutItem`, and
  `affectNeighborsAfterRemoval` (or `playerWillDestroy` returning
  `BlockState` for ice eggs). Forge analog output takes `Direction`.
  Neighbor updates take `Orientation`. Dread portal render is
  `INVISIBLE` (26.1 dropped `ENTITYBLOCK_ANIMATED`; BER still draws).
  Ice spikes use the 8-arg `updateShape` and `damageSources().cactus()`.
- Hydra / cyclops targeting uses `Selector`. Hydra breath uses
  `level()`, head Y uses `walkAnimation.speed()`, extra-head survival
  skips `BYPASSES_INVULNERABILITY`. Multipart `hurt` is
  `hurtOrSimulate`. Cyclops ride is `positionRider(Entity, MoveFunction)`
  and unliftables go through the entity-type tag. Swarmers store summoner
  UUID as STRING; `kill` takes `ServerLevel`.
- Dragon / hippogryph mate goals `snapTo` the egg and drop XP only when
  `ServerLevel` `GameRules.ENTITY_DROPS` is on. Block entities register
  on `ForgeRegistries.BLOCK_ENTITY_TYPES`. Sapphire / amethyst ore XP
  no longer comes from `getExpDrop` (gone); loot tables still drop gems
  but do not yet emit the 1.18 3–7 XP.
- Torches take `(SimpleParticleType, Properties)` with `noCollision`.
  Wall variants use `overrideLootTable` so they still drop the floor
  item (`blocks/burnt_torch`, `blocks/dread_torch`). Dread torch
  particles are unchanged. Chared path / myrmex biolight / falling
  generic blocks supply `codec()`. Dread wood lock uses `useItemOn`.
- Ghost chest uses the 26.1 ChestBlock ctor
  (type + open/close sounds + properties). Opening still spawns a
  persistent chest ghost via `startOpen(ContainerUser)` and
  `EntitySpawnReason.SPAWNER`. Custom ghost chest textures cannot
  hook `ChestRenderer.getMaterial` anymore (`ChestRenderState.material`
  is an enum); the BER is vanilla until a custom renderer is added.
  Graveyard soil ticks with `RandomSource` and the same 1/9 night
  spawn cap of 10. Cockatrice is not food-bred (`isFood` false);
  wither stare uses `damageSources().wither()` and
  `ColorParticleOption` entity-effect dust. Siren targeting is
  `Selector`. Stymphalian victor UUID syncs as STRING.
- Incremental `:compileJava` was **133 errors** (was 355) at this checkpoint.

## Earlier checkpoint: leftover tiles/items/nav, legendary items (2026-09-19)

- Citadel property packets are dimension + entity id + UUID + tag
  (`PropertiesMessage` record). NBT uses 1-arg `contains`,
  `getListOrEmpty`, `getCompoundOrEmpty`, and `UUIDUtil.CODEC`.
  `ChainOwnerIaf` key is unchanged.
- DragonUtils entity tags go through
  `TagKey.create(Registries.ENTITY_TYPE, …)`. Dimension strings use
  `identifier()`. Block blacklist compares
  `ForgeRegistries.BLOCKS.getKey`.
- Podium / lectern implement `getItems`/`setItems`. Packets load via
  `TagValueInput`. Lectern slot compare is
  `isSameItemSameComponents`. Gold pile path/update/use are the 26.1
  signatures; adding a layer still uses the selected hotbar stack.
- Menus register on `ForgeRegistries.MENU_TYPES` with
  `FeatureFlags.DEFAULT_FLAGS`. Banner `Sheets` injection is gone;
  custom patterns need datapack atlas entries.
- Hippogryph sword sweep is still
  `1 - 1/(sweeping_edge+1)` via `IafEnchantments`, plus
  `SWEEP_ATTACK` particles (26.1 removed `sweepAttack` /
  `getSweepingDamageRatio`). Gorgon / troll / myrmex egg / sea serpent
  / worker use `Selector`, `hurtServer`, `snapTo`,
  `GameRules.ENTITY_DROPS` / `MOB_GRIEFING`, `IafItemData`, and
  `positionRider(Entity, MoveFunction)`. Sea serpent boat drops use
  `getPickResult()` (closest typed item, not planks).
- Amphibious / death-worm land nav implement `canNavigateGround` and
  `getPathType(mob, pos)` with `FIRE` / `FIRE_IN_NEIGHBOR` /
  `DAMAGING`. Placement filters register on
  `Registries.PLACEMENT_MODIFIER_TYPE` with `MapCodec`.
- Legendary items: `hurtEnemy` is void, hover uses `TooltipContext` +
  `Consumer`, cooldowns take `ItemStack`, `InteractionResult` is no
  longer constructed. Lich staff repair is tag
  `iceandfire:repairs_lich_staff` (dread shard). Hydra heart still
  ticks hotbar 0-8 from `LivingTickEvent`. Food builder is
  `nutrition` / `saturationModifier` / `alwaysEdible` only (26.1
  dropped `meat` / `fast` / potion `effect` on
  `FoodProperties.Builder`). Infinity is `Enchantments.INFINITY`.
  Item description ids that used to override `getDescriptionId` now
  use `getName(ItemStack)` because the no-arg id is final.
- Incremental `:compileJava` is **355 errors** (was 561). Jars stay blocked.

## Earlier checkpoint: forge tile, statue/egg/skull, processors, dread, dragons (2026-09-19)

- Dragonforge tile implements `RecipeInput` + `DragonForgeTypeSource`.
  `getRecipeFor` / `DragonForgeRecipe.allOf` go through the server
  `RecipeManager` (client cook bar still uses synced `cookTime` and
  `getMaxCookTime` orElse 100). Slot compare is
  `ItemStack.isSameItem` / `isSameItemSameComponents`. Same 3 slots,
  same cook-time keys.
- Stone statues still store `StatueEntityType` / `StatueEntityTag`. The
  trapped tag syncs as SNBT `STRING` because `COMPOUND_TAG` is gone;
  create uses `EntitySpawnReason.CONVERSION`. Eggs keep `OwnerUUID` as
  a string accessor. Skull `Stage` / `DragonAge` still write through
  `IafItemData`. Egg drops skip
  `DamageTypeTags.BYPASSES_INVULNERABILITY` (1.18 `isBypassInvul`).
- Dread ruin / graveyard / village processors are `processBlock` +
  `pos()`/`state()` + `MapCodec.unit`. Spawner mob table and cracked
  brick/cobble odds are unchanged. Village chest loot id stays
  `iceandfire:chest/village_scribe`. `IafProcessors` registers on
  `Registries.STRUCTURE_PROCESSOR`.
- Dread targeting uses `TargetingConditions.Selector`. Equipment
  populate is `(RandomSource, DifficultyInstance)`. Illegal
  `isAlliedTo` overrides stay removed; targeting still skips dread.
  Knight shield is cyan `BASE_COLOR` plus a direct
  `iceandfire_dread` layer (1.18 cyan banner + white dread pattern).
  Knight ride offset is `getVehicleAttachmentPoint` -0.6. Lich minions
  `snapTo` the same XZ scatter. Beast wolf sounds map to the only
  remaining named growl/hurt/death fields (`WOLF_*_BABY.value()`);
  adult wolf clips now live on variants.
- Fire/lightning path malus uses `PathType.FIRE_IN_NEIGHBOR` /
  `PathType.FIRE`. Travel uses `calculateEntityAnimation(false)` and
  `applyEffectsFromBlocks`. Breath destruction uses `level()` +
  `BlockPos.containing`. Fire dragons skip soul-sand slowdown on
  `BlockTags.SOUL_SPEED_BLOCKS`. Lightning heal still keys
  `DamageTypes.LIGHTNING_BOLT`. Ice bubble-column skip uses
  `onAboveBubbleColumn(boolean, BlockPos)`.
- Cockatrice scepter hover/use/release/tick match 1.18: `PASS` after
  `startUsingItem`, use duration 1, wither 40/2 and 2 damage every 20
  ticks via `damageSources().wither()`. Beam particles are black
  `ColorParticleOption` (1.18 `ENTITY_EFFECT` 0,0,0).
- Incremental `:compileJava` is **561 errors** (was 704). Jars stay blocked.

## Earlier checkpoint: spawn, Hippocampus, Ghost, armor/trident (2026-09-19)

- Spawn placements register on `SpawnPlacementRegisterEvent` with the same
  predicates and heightmaps (`ON_GROUND` / `NO_RESTRICTIONS`). Biome spawners
  inject from `IafBiomeModifier` via `addSpawn(category, IafConfig.*SpawnRate,
  SpawnerData(type, min, max))`. Weight stays the 1.18 config rate; min/max
  stay 1-1 / 1-2 / 1-3. `MobSpawnSettings.spawners` is private, so
  `ServerAboutToStart` no longer mutates biomes (`LOADED_ENTITIES` is set
  from the modifier).
- `IafWorldRegistry` builds `Holder.direct(PlacedFeature)` (26.1 has no
  runtime `BuiltInRegistries.CONFIGURED_FEATURE` / `PlacementUtils.register`).
  Ore still size 8, copper max 128, silver max 32, against
  `STONE_ORE_REPLACEABLES` (1.18 `OreFeatures.NATURAL_STONE`). Lilies are
  `SIMPLE_BLOCK` + heightmap because `Feature.FLOWER` /
  `RandomPatchConfiguration` are gone; 1.18 already used tries=1. Spawn
  distance uses `getRespawnData().pos()`.
- Hippocampus: `equipItemIfPossible(ServerLevel, stack)` still writes the
  same inventory index; vanishing uses `Enchantments.VANISHING_CURSE`;
  swim depth-strider is the same 0-3 formula via `IafEnchantments`; limb
  swing still includes Y and the 0.4 lerp through `walkAnimation.update`.
  Inventory interact is `SUCCESS` (same as 1.18 `sidedSuccess`).
- Ghost chest statues drop no loot (`shouldDropLoot`). Daytime hide still
  uses brightness &gt; 0.5 and sky visibility (`getLightLevelDependentMagicValue`).
  Targets stay living players and villagers.
- Dragonsteel armor attributes stay on `IafArmors.humanoidArmor` (same
  material defense). Tide trident is still 12 / -2.9, riptide level from
  `Enchantments.RIPTIDE`, throw 2.5+0.5*level, spin `startAutoSpinAttack`.
  Ghost sword cooldown is 10 ticks and still shoots at half stored attack
  damage.
- Incremental `:compileJava` is **704 errors** (was 839). Jars stay blocked.

## Earlier checkpoint: ServerEvents, leftover item NBT, ClientEvents (2026-09-19)

- `ServerEvents` tag checks use `builtInRegistryHolder().is(TagKey)` (same
  IAF entity tags). Cancellable Forge 26.1 listeners return `true` instead of
  `setCanceled`. `getPlayer()` is `getEntity()`. Lightning loot tags are
  `entityTags()`. Ghost spawn still requires a player killer, 1/3 chance,
  poison, or FALL/DROWN/LAVA (via last damage / fall distance / lava / air
  because `CombatTracker.getMostSignificantFall` is private). The dead
  `RightClickItem` `instanceof EntityDragonBase` player branch is gone;
  look-down dragon `mobInteract` is unchanged.
- Leftover item NBT (`SummoningCrystal` Dragon* keys, statue
  `IAFStoneStatue*`, staff `HiveUUID`, cyclops `HurtingTicks`, egg
  `EggOrdinal`) goes through `IafItemData`. Statue place loads
  `IAFStoneStatueNBT` with `IafEntityNbt` / `ValueInput`. NBT key names
  match 1.18.
- `ClientEvents` siren shader clear is `currentPostEffect` /
  `clearPostEffect` (26.1 has no `loadEffect`). Frozen/chain/beam overlays
  resolve the live entity from render-state pose and keep the 1.18 ice AABB,
  chain draw, and cockatrice beam geometry. IAF armor still hides hat /
  jacket / sleeves / pants.
- Incremental `:compileJava` after this cluster is **839 errors** (was the
  1000-error cap). Jars stay blocked.
- `WorldUtil` chunk-loaded is `getChunk(..., FULL, false) != null` (the 26.1
  chunk-future type is gone). Dirty chunks use `markUnsaved()`. Dimension
  checks are `dimensionTypeRegistration().is(BuiltinDimensionTypes.*)`.
  Day-time gate is `getDefaultClockTime() % 24000`. Peaceful worldgen is
  difficulty only (`SPAWN_MOBS` gamerule accessor is gone).
- Gorgon temple processor is `processBlock` + `pos()`/`state()` and still
  clears water under waterloggable temple blocks / neighboring chunk water
  (MC-130584 workaround).

## Earlier checkpoint: Projectiles, item NBT, frozen overlay (2026-09-19)

- Fireball charges (`PixieCharge`, `SeaSerpentBubbles`, `HydraBreath`,
  `EntityDragonCharge` + fire/ice/lightning) use 26.1
  `Fireball(type, x,y,z, Vec3, level)` / `(type, shooter, Vec3, level)`.
  The 1.18 public `xPower`/`yPower`/`zPower` decay (0.07 / 0.1 / 0.02) is
  kept as IAF fields because vanilla now stores a single `accelerationPower`.
  Custom `tick()` still drives motion with those components.
  `ProjectileUtil.getHitResult` is `getHitResultOnMoveVector`. Pixie magic
  hit is `damageSources().indirectMagic(this, shooter)` (same 5 damage +
  levitation/glow). Dragon charge grief still uses `IafConfig.dragonGriefing`.
- Arrows are `projectile.arrow.AbstractArrow`. Shooter ctors pass the 1.18
  pickup stack + empty weapon; `getDefaultPickupItem` returns the same item.
  `inGround` is `isInGround()`. Tide trident was already on the 26.1
  `ThrownTrident` path. Ghost sword still deals magic-attributed damage
  (`indirectMagic`) at the same stored damage / crit roll (`getBaseDamage`
  is gone; IAF keeps the value it `setBaseDamage`s). Shield-break still
  runs at damage >= 3 on items with `DataComponents.BLOCKS_ATTACKS`
  (`ToolActions.SHIELD_BLOCK` is gone).
- Item custom NBT (`DragonHorn`, skull, bestiary pages, deathworm HolderID,
  gorgon Active) goes through `IafItemData` / `CUSTOM_DATA`. Horn
  save/load of the dragon compound is `IafEntityNbt` (`TagValueOutput` /
  `TagValueInput`) so the 1.18 `EntityTag` keys stay the same.
- Frozen overlay vertices are `addVertex`/`setColor`/`setUv`/`setOverlay`/
  `setLight`/`setNormal` (same ice UVs and AABB expand).
- Next: `ServerEvents` / `ClientEvents` leftover, remaining `getTag` items,
  mixins, ATs, runs, restore appearance particles. Jars stay blocked.

## Earlier checkpoint: Particles, multipart, attributes, vertex (2026-09-19)

- Quad particles (`Blood`, dragon flame/frost, dread torch/portal, hydra,
  pixie, serpent bubble, siren music) construct with a particle-atlas missing
  sprite (26.1 `SingleQuadParticle` requires one) and bind the same 1.18 PNG
  via `IafParticleSprites.layer(texture)` plus 0..1 UVs. Custom Tesselator
  `render()` is gone; vanilla `extract` draws the quad. Siren/ghost appearance
  particles compile with `NO_RENDER` until a 26.1 model `ParticleGroup` exists
  (jump-scare meshes are not drawn yet).
- Dragon / death-worm / serpent hitboxes store parent UUID as a synched
  `STRING` (`OPTIONAL_UUID` is gone). Interact is the 3-arg
  `(Player, hand, Vec3)` path; damage still forwards `damage * multiplier` to
  the parent via `hurt`.
- Config attribute overrides use `AttributeMap.assignBaseValues` (the
  `attributes` field is private). Same `IafConfig` combine + `setHealth`.
- Armor models are `HumanoidModel<HumanoidRenderState>`. Armor-stand pose
  copying is vanilla render-state now.
- Path debug `vertex`/`endVertex` is `addVertex`/`setColor`. `Level.random`
  grief rolls are `getRandom()` (same `nextBoolean` / `nextFloat` gates).
- Next: charge/projectile ctors, leftover `getTag`, mixins, ATs, runs, restore
  appearance particles. Jars stay blocked.

## Earlier checkpoint: Mounts, biomes, food, damage types (2026-09-19)

- `DefaultBiomes` colliding vanilla/Forge tags (`IS_OVERWORLD` / `IS_MOUNTAIN` /
  `IS_BADLANDS`) stay on `BiomeTags` (`minecraft:is_*`, same 1.18 strings).
  Missing Forge constants `IS_DENSE` / `IS_SPARSE` / `IS_PEAK` / `IS_SLOPE` map
  to `IS_DENSE_VEGETATION` / `IS_SPARSE_VEGETATION` / `IS_MOUNTAIN_PEAK` /
  `IS_MOUNTAIN_SLOPE`.
- `FoodUtils` is `DataComponents.FOOD` + `ItemTags.MEAT`. Hippogryph meat-heal
  and dragon eat-food bonus keep the 1.18 nutrition×10 / meat filter.
- Mount override cluster matches dragon: `hurtServer`, `getBaseExperienceReward`,
  `positionRider(Entity, MoveFunction)`, `LivingEntity getControllingPassenger`,
  `getAgeScale`, `doHurtTarget(ServerLevel, Entity)`,
  `IceAndFire.sendMSGToServer`. `Entity.hurt` / `isAlliedTo(Entity)` /
  `isLocalInstanceAuthoritative` / 1-arg `positionRider` are final. Death-worm
  “no local authority” is `isLocalClientAuthoritative()`. Tame-owner combat
  uses vanilla `TamableAnimal` teams (`iafIsAlliedTo` is the old body, not
  hooked). Tempt foods: hippogryph rabbit / cooked rabbit, pixie sugar,
  hippocampus prismarine crystals, death worm / sea serpent none.
- Damage comparisons are `source.is(DamageTypes.*)` / `DamageTypeTags.IS_FIRE`.
  `MobEffects.JUMP` / `CONFUSION` / `DIG_*` / `MOVEMENT_*` are `JUMP_BOOST` /
  `NAUSEA` / `HASTE` / `MINING_FATIGUE` / `SPEED` / `SLOWNESS`.
- `:compileJava` is still at the 1000-error cap. Hippogryph and FoodUtils are
  clean. Next: particles (`SingleQuadParticle` now needs a `TextureAtlasSprite`
  and `extract` / `getLayer`), `RenderPath` / armor vertex, leftover `getTag`,
  mixins, ATs, runs. Jars stay blocked.

## Earlier checkpoint: Entity hurt/owner/drops/loot (2026-09-19)

- `TamableAnimal.getOwnerUUID` is `IafOwners.getUUID` / `setUUID` on the 26.1
  `EntityReference`. Same UUID comparisons (owner vs rider, shared-owner
  cockatrice check, egg/jar/crystal copy).
- Item NBT `getTag`/`setTag` for dragon skulls and summoning crystals goes through
  `IafItemData` / `CustomData` on `DataComponents.CUSTOM_DATA`. Stage / type / age
  ints and the `Dragon` compound are unchanged.
- `spawnAtLocation(stack, yOffset)` / `(item, count)` is `IafDrops.spawn`. 1.18
  Y-offset vs count overloads are preserved.
- Damage uses `hurtServer(ServerLevel, source, amount)`. Static
  `DamageSource.IN_WALL` / `OUT_OF_WORLD` / `mobAttack` become `DamageTypes` /
  `damageSources()`. `displayClientMessage(msg, true|false)` is
  `sendOverlayMessage` / `sendSystemMessage`.
- Dragon XP switch is `getBaseExperienceReward(ServerLevel)` with the same 5 / 20
  / 150 / 300 / 650 values. Scale is `getAgeScale()` (`min(renderSize * 0.35, 7)`).
  Rider placement is `positionRider(Entity, MoveFunction)` with the same offsets.
  Dead-dragon "don't attack" is `canBeSeenAsEnemy()` (vanilla `isAlliedTo(Entity)`
  is final; owner teams stay on `TamableAnimal`).
- Dead-dragon loot table reads use `reloadableRegistries().getLootTable` +
  `LootParams` (`THIS_ENTITY`, `ORIGIN`, generic `DAMAGE_SOURCE`). First stack
  still wins.
- `:compileJava` is still at the 1000-error cap. `EntityDragonBase` compiles.
  Next: remaining mount overrides, render `vertex`/`transform`, leftover
  `getTag`, particles, biome tag ambiguity (`DefaultBiomes`), mixins, ATs, runs.
  Jars stay blocked.

## Earlier checkpoint: Explosion, trades, GUI extract, step height (2026-09-19)

- Custom explosions implement the 26.1 `Explosion` interface. Sampling is still
  the 1.18 16³ / 0.225 rays. Troll `BlockBreakExplosion` still skips `ItemEntity`
  and DESTROY-drops via `Block.getDrops` + `onBlockExploded`. Death-worm / dragon
  breath `BlockLaunchExplosion` still launches `FallingBlockEntity.fall` with the
  same knockback. `ProtectionEnchantment` explosion dampening is gone in 26.1
  (knockback uses undamped `d10`). `BlockInteraction.NONE` is `KEEP`. Vanilla
  `Level.explode` now takes `Level.ExplosionInteraction` (death-worm thrower uses
  `MOB`).
- Myrmex / scribe offers use `IafItemListing.getOffer(Entity, RandomSource)` and
  `IafOffers.of` (`ItemCost`). Queen eggs keep `EggOrdinal` on
  `DataComponents.CUSTOM_DATA`. Unused potion/enchant/stew listing classes compile
  against `PotionContents` / `EnchantmentHelper.enchantItem` /
  `SuspiciousStewEffects`. Village house pool mutation is not wired (26.1 pools
  are frozen registries); scribe house injection is a later datapack hook.
- Container GUIs use `extractBackground` / `extractLabels` / `extractRenderState`,
  `GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, ...)`, and
  `InventoryScreen.extractEntityInInventoryFollowsMouse`. Dragon GUI height stays
  214, podium 133 (5-arg screen ctor). Lectern page buttons and book flip math
  are the same; 3D book uses `graphics.book`. Buttons are `Button.builder` or
  subclasses implementing `extractContents`.
- `flyingSpeed` / `maxUpStep` writes are `iafFlyingSpeed` / `iafMaxUpStep` plus
  `maxUpStep()` overrides (dragon still returns `getStepHeight()`).
- `:compileJava` is still at the 1000-error cap, but the previous
  Material/GUI/Explosion/VillagerTrades/`flyingSpeed` symbols are gone. Remaining
  cluster: render `vertex`/`PoseStack.transform`, `displayClientMessage`,
  leftover `getTag`/`load(CompoundTag)`, override mismatches, mixins, ATs, runs.
  Jars stay blocked.

## Earlier checkpoint: Material, spawn, MobType, BlockEntityType (2026-09-19)

- 1.18 `Material` / `getMaterial()` checks go through `IafMaterials` (same sand /
  dirt / stone / wood / plant / water buckets as the 1.18 materials). Dragon fire /
  ice / lightning block transforms still use those buckets.
- `PlayMessages.SpawnEntity` is Forge 26.1 `net.minecraftforge.network.packets.SpawnEntity`.
  No-arg `getAddEntityPacket()` overrides that only wrapped
  `NetworkHooks.getEntitySpawningPacket` are removed; vanilla
  `getAddEntityPacket(ServerEntity)` plus `setCustomClientFactory` keep the same
  client factories (`EntityType` + `Level` delegates).
- `NetworkHooks.openGui` is `ServerPlayer.openMenu` (dragon / hippogryph /
  hippocampus inventories).
- `BlockEntityType.Builder` is `new BlockEntityType<>(factory, Set.of(blocks))` with
  the same 1.18 block lists (pixie house still registers birch twice).
- `CapabilityItemHandler.ITEM_HANDLER_CAPABILITY` is `ForgeCapabilities.ITEM_HANDLER`.
- `MobType` overrides are datapack `entity_type` tags: dread humanoids/horse/beast/
  lich/knight + ghost/gorgon → `undead` + `sensitive_to_smite`; myrmex + dread
  scuttler → `arthropod` + `sensitive_to_bane_of_arthropods`; hippocampus / sea
  serpent → `aquatic` + `sensitive_to_impaling`. Lich necromancy still branches on
  those tags. Tide trident still deals 12 damage and +2 pierce; enchantment bonus
  is `EnchantmentHelper.modifyDamage`.
- `EntityType.Builder.build` takes `ENTITIES.key(name)`. Leftover `Entity.level`
  field reads on AI/items use `level()`; PathNavigation / BlockEntity / Minecraft
  keep their `level` fields.
- Next cluster: GUI `extractRenderState` / `PoseStack` blit, `Explosion` as
  interface, `VillagerTrades`, `flyingSpeed` / `maxUpStep` fields, mixins, runs.
  Jars stay blocked.

## Earlier checkpoint: Persistence, accessors, Identifier (2026-09-19)

- Entity/tile persistence uses 26.1 `ValueInput`/`ValueOutput`. Scalar NBT reads use
  `getIntOr`/`getBooleanOr`/`getStringOr`/`getFloatOr` with the 1.18 defaults
  (`0` / `false` / `""`). UUID keys go through `UUIDUtil.CODEC` (same int-array
  format as 1.18 `putUUID`). Chest inventories keep the `Items` list + `Slot` byte.
  Myrmex village inventory stays a list of stacks without slots; offers use
  `MerchantOffers.CODEC`. Home positions still write `HomeAreaX/Y/Z` + `HomeDimension`.
- `defineSynchedData` takes `SynchedEntityData.Builder`. `Entity.level` / `isClientSide`
  / `onGround` go through accessors. PathNavigation and BlockEntity still use their
  protected `level` field.
- Restriction API is `getHomePosition` / `getHomeRadius` / `hasHome` / `setHomeTo`.
  Dragon radius is still `IafConfig.dragonWanderFromHomeDistance`; cockatrice stays 30.
  `setTame(flag)` is `setTame(flag, false)` so 1.18 does not gain taming side-effects.
- 1.18 `Entity.noCulling = true` is `affectedByCulling() == false` on dragon / sea
  serpent / death worm / skull renderers.
- `ResourceLocation` is `Identifier`; `TranslatableComponent` is `Component.translatable`;
  `MobSpawnType` is `EntitySpawnReason` (`SPAWN_EGG` → `SPAWN_ITEM_USE`);
  `BlockPathTypes` is `PathType`. `finalizeSpawn` dropped the extra CompoundTag.
- `:compileJava` dropped from the previous 1000-error cap to the mid-400s. Remaining
  cluster: `Material`/`getMaterial`, `NetworkHooks`, `PlayMessages`, `MobType`,
  leftover GUI/item APIs, mixins, runs. Jars stay blocked.

## Earlier checkpoint: Structures, JEI host, loot codecs, events (2026-09-19)

- Jigsaw structures are 26.1 `Structure` types (`graveyard`/`gorgon_temple`/`mausoleum`)
  with the 1.18 start math: 5-block rotation offsets, min of four
  `WORLD_SURFACE_WG` corners, Y+1/+2, depths 2/3/5, same template pools.
  Config gates still abort generation. Default structure-set JSON uses the
  1.18 formula (spacing 32, sep 16, salt 342226450, weights 48/30/16).
- JEI plugin stays in-tree against a local 1.18 API host (`port-26.1/jei-api`):
  same three forge categories, slots 64,29 / 82,29 / 144,30, same recipe ids.
- Loot customize functions are `MapCodec`s registered at the 1.18 names.
  Dragon/serpent `run()` counts and variant swaps are unchanged.
- Event remaps: `LivingTickEvent`, `LivingChangeTargetEvent`,
  `EntityInteractSpecific`, `FinalizeSpawn`, `BlockEvent`/`ScreenEvent.Opening`,
  `ViewportEvent.ComputeCameraAngles`. Dragon camera still computes the 1.18
  `scale*1.2/3/5` pullbacks (`ClientProxy.DRAGON_CAMERA_PULLBACK`).
- Gorgon/dragon damage is datapack `DamageType` with the 1.18 `message_id`s;
  gorgon still tags `bypasses_armor` + `bypasses_effects`.
- Feature injection is a Forge biome modifier calling the same
  `IafWorldRegistry.addFeatures` tests.
- The previous 152 missing-type errors (JEI, `StructureFeature`, loot
  serializers, those events) are gone. `:compileJava` now reaches the
  next wave (capped at 1000): `Entity.level` / `isClientSide` accessors,
  CompoundTag Optionals, `ValueInput`/`ValueOutput`, leftover GUI
  `PoseStack` blit, mixins, runs. Jars stay blocked.

## Earlier checkpoint: Networking, JOML/GUI, entity packages (2026-09-19)

- `IceAndFire.NETWORK_WRAPPER` is 26.1 `ChannelBuilder` +
  `IafNetwork.create()` (same 23 play packets, 1.18 write/read order).
  Handlers take `CustomPayloadEvent.Context`. Sends use
  `PacketDistributor.SERVER/ALL/PLAYER`. `DistExecutor` is
  `FMLEnvironment.dist` + `createProxy()`. Creative tabs compile with
  `CreativeModeTab.builder()` (still unregistered).
- JOML/`Axis` remaps, `Widget`→`Renderable`, `BookModel` package,
  `GUIColoredBlit` `Tesselator.begin`/`addVertex`/`drawWithShader`.
- Keybinds: `RegisterKeyMappingsEvent` + `KeyMapping.Category`.
- `IafClientSetup` dropped `ShaderInstance`/`ItemBlockRenderTypes`/
  `ItemProperties`/`TextureStitchEvent.Pre`. Custom pipelines already
  live in `IafRenderType`. Block layers and item predicates must move
  to model/atlas JSON to keep 1.18 cutout/bow/horn/crystal/trident
  visuals. Ghost-chest stitch sprites likewise.
- `IItemRenderProperties`/`initializeClient` removed (gorgon, gauntlet,
  trident, troll weapon, TEISR). Equipment/special models later.
- Projectiles: `arrow`/`throwableitemprojectile`/`hurtingprojectile`
  packages. Dragon charge keeps the 1.18 0.07 normalized `xPower` on
  the subclass; `Fireball` ctor takes `Vec3`.
- Particles: `SingleQuadParticle` / `getGroup()` /
  `ParticleRenderType.SINGLE_QUADS` (custom `render()` still needs
  `extract()`).
- `GameRules.get(MOB_GRIEFING/ENTITY_DROPS/SPAWN_MOBS/MOB_DROPS)`.
- Entity package moves: villager, boat, polar bear, wolf, sheep,
  water animal, zombified piglin, golem.
- Title splash overlay uses `extractRenderState` (table/bestiary blit
  pending). Path-debug `MRenderTypes` maps to `RenderTypes.lines/debugQuads`.
- Citadel `ICustomCollisions` restored for death-worm sand pass-through.
- Full `:compileJava` still fails (~167): structures
  (`StructureFeature`/`JigsawConfiguration`), JEI, loot function codecs,
  remaining events, mixins, runs.

## Earlier checkpoint: Registration, myrmex trades, item use/armor (2026-09-19)

- `IceAndFire` uses Forge EventBus 7 `BusGroup`: DeferredRegisters
  (including `IafSoundRegistry.SOUNDS`) call `register(BusGroup)`.
  `FMLCommonSetupEvent` / `FMLLoadCompleteEvent` / `FMLClientSetupEvent`
  use `getBus(modBus).addListener`. Server lifecycle uses
  `ServerAboutToStartEvent.BUS` (priority `LOWEST`, same as 1.18) and
  `ServerStartedEvent.BUS`. Configured-feature registration moved into
  common-setup `enqueueWork` with the existing village/loot init.
- Sounds are `SoundEvent.createVariableRangeEvent` + DeferredRegister;
  field types stay `SoundEvent` so gold-pile `SoundType` is unchanged.
- Block items register on `IafItemRegistry.ITEMS` during its static init
  (same `StandingAndWallBlockItem` / `BlockItemWithRender` / `BlockItem`
  split; wall torches skipped). `Item.Properties.tab` is gone; creative
  tabs are still pending. Wall standing items use `Direction.DOWN` like
  vanilla torches.
- Myrmex trades keep the same offer tables via `IafItemListing` +
  `IafOffers` (`ItemCost`). Egg caste is `CustomData` `EggOrdinal`.
  Unused 1.18 potion/enchant/stew listing copies were never wired and
  were dropped. `EntityMyrmexBase.teleport` still `resetCustomer()`
  first. Scribe lists stay on `IafVillagerRegistry.addScribeTrades`;
  26.1 has no `VillagerTradesEvent` so they are not injected yet.
- Hippocampus inventory opens with `player.openMenu`; capabilities use
  `ForgeCapabilities.ITEM_HANDLER`. `Saddleable` is gone; saddle flags
  stay on entity data (`isSaddleable` still false).
- `ArmorItem` / `SwordItem` / `InteractionResultHolder` / `UseAnim` are
  gone. Armor uses `Item.Properties.humanoidArmor` with Citadel
  `CustomArmorMaterial.toArmorMaterial` (FEET/LEGS/CHEST/HEAD defense;
  native 13/15/16/11 durability multipliers). Swords use
  `Properties.sword`. Item `use` returns `InteractionResult`.
  `AbstractHorse` moved to `animal.equine`.
- `compileIafOffersTest` passed. Full `:compileJava` still fails (GUIs,
  networking `NetworkEvent`, JEI, worldgen `StructureFeature`, mixins,
  runs).

## Earlier checkpoint: Dragon spawn, inventory, pathfinding APIs (2026-09-19)

- `EntityDragonBase` / `EntityHippocampus` no longer implement 1.18
  `ContainerListener`. 26.1 `SimpleContainer` has no `addListener`;
  `IafListeningContainer` fires the same server `updateAttributes` /
  saddle-equip callback from `setChanged` after the initial item copy.
- `finalizeSpawn` is 4-arg `EntitySpawnReason` (no `CompoundTag`). Spawn
  bodies are unchanged: dragon gender/age/variant/heal/hunger, hippocampus
  saddle sound after tick 20. `SPAWN_EGG` is now `SPAWN_ITEM_USE`.
- `BlockPathTypes` is `PathType` (`DANGER_FIRE`→`FIRE_IN_NEIGHBOR`,
  `DAMAGE_FIRE`→`FIRE`, `DAMAGE_OTHER`→`DAMAGING`). `IafPathTypes.hasDanger`
  matches 1.18 `getDanger() != null` (FIRE / neighbor fire / DAMAGING /
  neighbor damaging / LAVA; not `DAMAGE_CAUTIOUS`). Death-worm evaluator uses
  `getPathType` / `getTarget`. Amphibious/worm land stand-checks use
  `getPathTypeOfMob(PathfindingContext, ...)` (26.1 sizes come from the mob
  BB, not the old 9-arg door flags).
- `NetworkHooks.getEntitySpawningPacket` is gone. Vanilla
  `getAddEntityPacket(ServerEntity)` already routes
  `IEntityAdditionalSpawnData` through `ForgeHooks.getEntitySpawnPacket`.
- Stuck-path damage uses `damageSources().generic()` instead of custom
  `EntityDamageSource("Stuck-damage")`. Debug path packets are removed
  (`DebugPackets` is gone). `TranslatableComponent` / `ResourceLocation` /
  `DirectionProperty` on this cluster are `Component` / `Identifier` /
  `HORIZONTAL_FACING`. Ghost chest open-stat is `Stat<Identifier>`.
- `compileIafListeningContainerTest`, `compileIafPathTypesTest`,
  `compileSpawnApiProbe` passed. `SavedData` now uses `SavedDataType` codecs
  (`IafWorldData`, `DragonPosWorldData`, `MyrmexWorldData`) with the same
  1.18 field names; file ids are namespaced. `MobType` overrides are entity
  type tags (`undead`/`arthropod`/`aquatic` plus smite/bane/impaling). Tide
  trident bonus uses `EnchantmentHelper.modifyDamage`. Empty pixie
  `initializeClient` was removed (`IBlockRenderProperties` is gone). Full
  `:compileJava` still fails (Forge EventBus 7 registration, ArmorItem,
  JEI, mixins, runs).

## Earlier checkpoint: Dragonforge recipes and Material (2026-09-19)

- `DragonForgeRecipe` is a 26.1 `Recipe<RecipeInput>`: `assemble` has no registry
  lookup, ids live on `RecipeHolder`, serializers are
  `RecipeSerializer(MapCodec, StreamCodec)` records. JSON still reads
  `dragon_type` / `input` / `blood` / `cook_time` / `result`. Result accepts both
  native `{ "id" }` and the 1.18 `{ "item", "count" }` shape. Matching is still
  slot 0 + blood + forge type. `isSpecial()` stays true. `getAllRecipesFor` is
  gone; callers use `DragonForgeRecipe.allOf`. The tile implements `RecipeInput`.
  Recipe-book toast now returns the recipe result (1.18 always used the fire
  core; the recipe is special-only so the book never showed it).
- `Material` is gone. Block constructors take `MapColor` and
  `Properties.of().mapColor(...)`. Runtime checks go through `IafMaterials`
  (tags + `isAir` / `isSolid` / `blocksMotion` / fluids) so the same vanilla
  buckets still match: sand includes gravel and concrete powder, dirt excludes
  grass blocks, stone covers ores/cobble/sandstone/terracotta/nether. Roost
  `DIRT && GRAVEL` stays a dead branch like 1.18. Falling-block dust color now
  takes level/pos. `BlockGenericStairs` copies the model block then forces
  hardness 20.
- `compileRecipeApiProbe`, `compileMaterialApiProbe` and
  `compileDragonForgeRecipeTest` passed. Full `:compileJava` still fails.
  `EntityDragonBase`, remaining tiles/items, JEI, mixins and runs are unported.

## Earlier checkpoint: Dread humanoids and stone statues (2026-09-19)

- Dread humanoids (ghoul/knight/lich/thrall/queen), scuttler and lich skull now consume
  extracted render state. `ModelBipedBase` is an `ArmedModel`; item-in-hand uses an
  arm-only Citadel translate. Armor overlays keep copied poses. Ghoul/scuttler scale
  through native living scale only (the 1.18 extra `scale()` hook would double in 26.1).
- Leftover static models (`ModelTrollWeapon`, `ModelTideTrident`, gorgon heads,
  `ModelCube`, `ModelDeathWormGauntlet`) now take `EntityRenderState` with empty
  `setupAnim`; gauntlet lunge is still applied by the special renderer.
- `RenderStoneStatue` is `EntityRenderer<EntityStoneStatue, StoneStatueRenderState>`.
  Citadel statues still skip live `setupAnim` (reset / `animateStatue` / after-draw
  hippogryph baby scale and hippocampus tack hide on the crack pass). Vanilla statues
  keep `setupAnim` at age `-0.1`. Troll statues use the stone atlas. Hydra heads stay
  a second pass after a rest body. Cached models keep the 1.18 `showModel` mutation.
  The first-frame `putIfAbsent` null is fixed so NBT fakes are used immediately.
- `dreadHumanoidModelTest` 122. `dreadScuttlerModelTest` 606. `stoneStatueModelTest`
  438. `trollModelTest` 3,247 after restoring `ICustomStatueModel` on `ModelTroll`.
  Full `:compileJava` still fails. Custom explosions now implement the 26.1
  `Explosion` interface (`shouldAffectBlocklikeEntities`, `ServerLevel` `level()`,
  `KEEP` instead of `NONE`). `explode()`/`finalizeExplosion()` stay as local
  methods. Falling-block launch now actually adds the entity (1.18 constructed
  it and never spawned it). Blast protection no longer dampens knockback
  (`ProtectionEnchantment.getExplosionKnockbackAfterDampener` is gone).
  Recipes and `Material` have since moved; `EntityDragonBase` remains unported.
- GPU, live statue extraction, dispatcher lookup and trapped-entity NBT are not tested.

## Earlier checkpoint: Citadel persistence and Hippogryph (2026-09-19)

- The Citadel subset now includes the missing persistence/material types that
  `PropertiesNetwork`, IAF entity props and `CitadelPersistenceTest` already
  called: `CitadelEntityData` on Forge persistent data (`CitadelData`),
  `PropertiesMessage` (dimension, entity id, UUID, deep-copied tag),
  `CustomArmorMaterial` and `CustomToolMaterial` with native conversions.
  `setCitadelTag` snapshots and sends to tracking players plus self. Death
  cloning still drops transient data; non-death clone copies it. Live tracking
  delivery, save/reload and multiplayer are not tested.
- `ModelHippogryph` now consumes `HippogryphRenderState`. The renderer had
  already moved to extracted state while the model still took `EntityHippogryph`,
  so `asEntityModel()` could not compile. Keyframe tokens match the render-state
  identities. Adult reuse restores baby scale and `scaleChildren`.
- Shared 5-argument `progressRotation`/`progressPosition` overloads (divisor 20)
  live on `AdvancedEntityModel`. Duplicate private wrappers that hid them were
  removed. `ModelDragonBase` is bound to `EntityRenderState`.
- Review fixes: hippocampus rainbow names use `Component.getString()` rather
  than `toString()`; chain-tie atlas uses the constructor's 32x32 size;
  generic glowing layers pass `outlineColor` like the other 8-argument eye
  submits; humanoid armor slot indices no longer use `EquipmentSlot.getIndex()`.
- `settings.gradle` applies Foojay toolchain resolver 1.0.0 so JDK 25 can be
  provisioned when the Gradle JVM is 17+.
- `:citadel:check` passed (all five suites). `citadelPersistenceTest` 26 checks.
  `hippogryphModelTest` 3,012. `dreadBeastModelTest` 534,770. `trollModelTest`
  3,247. `amphithereModelTest` 8,514. `git diff --check` passed for this batch.
  Full `:compileJava` was not re-counted; it still fails. First remaining
  living-renderer error remains `RenderDreadGhoul`.

Siren, Ghost, Cockatrice, Death Worm, Hippocampus, Stymphalian Bird and chain-tie
models/renderers were already on extracted state from earlier unpublished work.
They have no dedicated source-only suites except as compiled production sources.
Armor items, remaining Dread humanoids, dragons as live entities, JEI, mixins
and game runs are still unported.

## Earlier checkpoint: base tools (2026-09-18)

- Five base tools (`ItemModSword/Axe/Pickaxe/Shovel/Hoe`) and
  `DragonSteelOverrides` now use native `ToolMaterial`, item components,
  server-side combat effects and the consumer-based tooltip API. Sword/pickaxe
  Forge tool actions and axe/shovel/hoe block interactions are retained.
- `DragonSteelTier` no longer depends on removed Forge tier sorting. Its native
  incorrect-block tag is empty by default (no tier-based drop restrictions);
  mining efficiency still requires the corresponding vanilla mineable tag.
  Repair ingredients are supplied by new singular-path item tags.
- All 15 ordinary tool materials use native records. Upstream Citadel takes
  damage before speed, unlike the native record: silver remains damage 1,
  speed 11. The local Citadel adapter's argument order was corrected too.
- The 45 base-tool registrations pass explicit item IDs. Other item registrations
  and specialized weapons remain unported. Dragonsteel damage/durability are
  applied at item construction; config changes require restarting.
- Mechanical entity API updates were applied to 24 entity files; these do not
  complete those entities' persistence, AI or rendering migrations.
- Uncapped all-source `compileJava` reports **7,069 errors**, versus the previous
  7,521 checkpoint. Log: `.gradle/port-tools/tools-final.log`. No diagnostics
  name the five base tools, `DragonSteelTier` or `DragonSteelOverrides`;
  `IafItemRegistry` still has 54. This is NOT a successful production compile.
- `dreadBeastModelTest` passed again (534,770 checks). `citadelPersistenceTest`
  passed 26 checks, including six new tool-adapter assertions for argument
  order, durability, enchantability, harvest/repair tags and absent repair data.
- No live registration, datapack loading, anvil repair or combat integration
  has been tested. Resources/runs are still not wired; no playable jar is built.

## Earlier checkpoint: Dread Beast (2026-09-18)

- `ModelDreadBeast` now consumes `DreadBeastRenderState` through the current
  Citadel native adapter, without the entity-bound `ModelDragonBase`. Original
  28-part geometry, bite keyframes, spawn motion and procedural walking remain.
- `RenderDreadBeast` now uses the three-parameter native renderer, extracted
  animation timing/variant, identifiers and the shared glowing layer. Native
  living-state scale replaces the old extra scale hook to avoid double scaling.
- Beast's spawn override and its necromancy call use the actual target
  four-argument `finalizeSpawn` and `EntitySpawnReason` API.
- `dreadBeastModelTest` passed: 534,770 low-level assertions covering keyframes,
  fractional timing, resets, walking/tail clamp, adapter synchronization and
  finite CPU geometry. The real model/state/statue interface/glowing layer
  compile against target dependencies and current Citadel, without entity stubs
  or old mod binaries. Renderer/entity integration, texture selection, deferred
  layer submission and GPU/statue rendering are NOT validated by this probe.
- Full `compileJava` still fails at the 1000-error cap, first at
  `RenderDreadGhoul.java:12`. Log: `.gradle/port-tools/dread-beast-final.log`.
  Absence of Beast diagnostics in that capped log is not proof its renderer or
  entity compiles. `EntityDreadBeast` still has legacy persistence, synced-data,
  combat and targeting APIs; its base also depends on other unported Dread mobs.

Earlier dragon/persistence work also passed source-only probes (see
`.gradle/port-tools/validated-agent-checkpoint.log`): dragon model/texture
3,930,641 checks, lightning geometry 264, persistence/codec/material primitives
20, plus the five Citadel and seven model suites. These do not validate live
network lifecycle, entity saves, production dragon layers or rider mixin wiring.
The implementation inventory below predates that batch and is not exhaustive.

## Implemented

- Local `port-26.1/citadel` dependency, without old Citadel/JEI binaries.
- Animation tokens, entity interface, timing handler, Forge EventBus 7 events,
  and client-bound animation transport. Array-index packets retain fixed-width
  integers; the new channel is not legacy-wire-compatible. Tracking players and
  self receive updates. Late-tracker catch-up is not implemented.
- Client-bound Citadel properties transport (`citadel:properties` protocol 1)
  plus persistent-data `CitadelData`. Packets identify dimension, entity id and
  UUID. `CustomArmorMaterial`/`CustomToolMaterial` keep upstream argument order
  and convert to native records when given repair/asset tags.
- Tabula archive/JSON loading and model, cube, group and animation containers.
  The mod helper uses the new loader. `TabulaModel<S>` now constructs recursive
  cube/group geometry, name/identifier lookup, default poses and native adapters,
  with extracted-state animator callbacks. Pinned legacy behavior intentionally
  ignores editor scale/hidden/opacity/group-mirror fields; runtime box scale works.
  Animation-container playback is not implemented.
- Basic/advanced model parts with cube geometry, UVs, hierarchy, default poses
  and animation helpers. `asEntityModel()` adapts to native 26.1 render-state
  models; old live-entity model subclasses still need migration. Rebuild the
  adapter after geometry changes. Native rendering does not invoke custom part
  render overrides. Defaults now restore offsets/scale as well as rotation and
  position; offsets use model-pixel units.
- Keyframe `ModelAnimator` with explicit extracted animation/tick/partial-tick
  inputs, sine interpolation, static frames and reset frames. Gorgon model,
  renderer and eye layer now consume render state with the native adapter.
  Five-argument progress helpers default to the legacy 20-tick divisor.
- Dragon fire/world-damage and generic grief events plus 16 dispatch sites moved
  to typed cancellable buses. Duplicate staging event files were removed.
- Egg model, dragon/myrmex egg renderers, podium and frozen-egg renderers use
  extracted state and native model submission. Ordinary podium items use native
  item render states. Entities and block entities underneath remain unported.
- Hydra body/head models, renderer and layers migrated to native render states.
  Nine head slots have independent extracted progress arrays and indexed adapters;
  severed neck visibility is reset on state reuse. Head placement and body-only
  emissive rendering preserve the legacy layout. The immediate statue bridge
  still draws real geometry, but the unported statue renderer's native-adapter
  discovery/casts remain incompatible; statue integration is not complete.
- All seven Myrmex caste/growth models now consume extracted state, with native
  adapters, identity-based animation selection and current-value progress fields.
  The renderer switches adult/pupa adapters during submission and resolves the
  worker's carried item before drawing. Both juvenile stages retain the legacy
  pupa geometry selection. Carried-item rendering is restricted to adults to
  avoid the legacy juvenile-model cast failure. Texture selection matches built-in
  castes, including hidden sentinels; custom entity texture overrides are not
  consulted yet. Statue adapter integration remains unported.
- Pixie, jar and house models/renderers use extracted states and native item/model
  submission. Jar color 5 retains the original color-0 fallback. House/item TEISR
  integration remains unported.
- Siren, Ghost, Cockatrice (adult/chick plus beam), Death Worm, Hippocampus,
  Hippogryph, Stymphalian Bird and chain-tie models/renderers use extracted
  state and native adapters. Hippocampus/Hippogryph saddle layers preserve
  legacy no-outline translucent overlays. Ghost keeps custom daytime alpha and
  shopping-list pages. These paths are not visually validated.
- Troll, Amphithere and Cyclops models/renderers use extracted animation state.
  Troll stone entities retain the normal body atlas; statue poses select stone
  and hide the weapon. Ordinary stone/weaponless states suppress layers, not the
  body rig. Amphithere reads buffer history through getters, without reflection;
  the legacy pitch-buffer/yaw-history quirk is preserved. `IFChainBuffer` uses
  the target delta tracker in its remaining legacy apply methods.
- SeaSerpent Tabula animation, renderer and ancient layer use extracted state,
  native adapters and deferred submission. All 16 pose assets are loaded with
  fail-fast errors. Shared Tabula math is typed without changing its formulae.
  Client model construction is typed; the general client setup, entity and skull
  renderer remain unported. The ancient pipeline preserves no-depth-write
  translucent behavior but is not visually validated.
- Six arrow renderers use native arrow render state. Their entity source paths
  have migrated projectile constructors, spawning and affected world/save APIs;
  these entities are not independently compile- or gameplay-validated. Hydra
  shield break notification now uses the active hand slot rather than CHEST.
- `RenderPodiumItemEvent` declaration migrated; its formerly disabled dispatch
  remains disabled to avoid introducing unrelated behavior changes.

See [Citadel provenance and distribution restrictions](port-26.1/citadel/NOTICE.md).
This is a narrow modified subset, not an official or complete Citadel release.
License-version verification and applicable license texts remain release blockers.

## Verified on 2026-09-19

| Check | Result |
| --- | --- |
| Target `:citadel:compileJava` | Passed |
| Target `:citadel:check` | Passed; all five plain-main suites |
| `:citadelPersistenceTest` | Passed; 26 checks |
| `:hippogryphModelTest` | Passed; 3,012 checks, source-only model/state compilation |
| `:dreadBeastModelTest` | Passed; 534,770 checks |
| `:trollModelTest` / `:amphithereModelTest` | Passed; 3,247 / 8,514 checks |
| `git diff --check` (this batch) | Passed |
| `compileIafListeningContainerTest` / `compileIafPathTypesTest` / `compileSpawnApiProbe` | Passed |

## Previously verified on 2026-09-18

| Check | Result |
| --- | --- |
| Pre-migration original `compileJava`, Java 17 / Forge 1.18.2 | Passed in 5m 39s; 82 warnings |
| Target `:citadel:compileJava` | Passed |
| Target `:citadel:check` | Passed in 12s; all four plain-main suites executed |
| `:hydraModelTest :citadel:check` | Passed in 9s; 1,024 Hydra checks and all four Citadel suites |
| `:myrmexModelTest` | Passed in 8s; all seven current model sources compiled and tested against target APIs |
| Latest five Citadel suites plus seven source-only model suites | Passed in 19s |
| `:citadel:tabulaModelTest` | 192 assets / 19,766 cubes; 693,289 low-level assertions |
| Pixie / Troll / Amphithere / Cyclops model tests | 605 / 3,247 / 8,514 / 2,403 checks passed |
| `:seaSerpentModelTest` | 266,866 checks across all 16 real poses; ancient layer and IFChainBuffer also compile |
| `:dreadBeastModelTest` | Passed; 534,770 checks, source-only model/state/layer compilation |
| Full target `:compileJava` after Beast changes | Failed in 8s; first error `RenderDreadGhoul.java:12`, 1000-error cap |
| `git diff --check` | Passed |

The keyframe animator suite passed 3,337 checks. The model suite ran 81 checks
against real 26.1 classes (geometry, transforms,
poses and native adapter). Tabula tests loaded 192 bundled `.tbl` assets / 19,766
cubes and checked malformed input, defaults and resource lifecycle. Animation
tests cover identity, real event buses and 28 codec vectors, including truncated
packets. They do **not** prove live handler advancement, client index rejection,
network tracking delivery or multiplayer behavior.

Targeted egg-renderer compilation also passed against cached target APIs and
old compiled mod dependencies. That is not proof the full source dependency
chain works. No client or dedicated server has been launched. No visual check,
save/reload test or multiplayer test has passed. Native-access/JOML warnings
remain. The 1000-error cap is not a count of independent defects.

The Hydra probe compiles the current body/head models, state and statue interface
against target APIs and the current Citadel subset, without old mod binaries or
entity stubs. It tests poses, visibility, per-head progress, reuse and adapter
setup. It does not validate entity extraction, layer submission, statue
integration or visual placement. Full `compileJava` still includes all sources.

The Myrmex source-only probe covers seven models, declared keyframe timelines,
fractional ticks, finite transforms, native adapter pose synchronization/reuse,
mouth attachment transforms, passengers, royal flight, sentinel holding/hiding,
and queen egg/dig behavior. Its 45,724,284 low-level assertions include repeated
per-part comparisons, not that many independent scenarios. No old mod binaries
or entity stubs are used. Entity extraction, renderer model selection, item
resolution/submission, textures and statue integration are not runtime-tested.

The newer model probes also compile current source only, without entity stubs or
old mod binaries. SeaSerpent tests cover pose interpolation, keyframes, native
pose synchronization and reuse. These numerical assertion totals are not counts
of independent gameplay scenarios. Entity extraction, textures, deferred layer
execution, shadows, client registration and special item/statue paths remain
unverified. A lack of errors for a file in capped full-build output does not prove
that file compiles with all its dependencies.

## Reproduce

Set `JAVA_HOME` to JDK 25, then from `port-26.1`:

```text
./gradlew :citadel:check citadelPersistenceTest hydraModelTest myrmexModelTest pixieModelTest trollModelTest amphithereModelTest cyclopsModelTest seaSerpentModelTest --no-daemon --console=plain
./gradlew dreadBeastModelTest hippogryphModelTest dreadHumanoidModelTest dreadScuttlerModelTest stoneStatueModelTest explosionApiTest --no-daemon --console=plain
./gradlew :compileJava --no-daemon --console=plain
```

All five Citadel regression suites are plain Java entry points invoked by the standard
Citadel `test`/`check` lifecycle, rather than JUnit-discovered tests. They can also
run separately as `:citadel:animationApiTest`, `:citadel:tabulaAssetsTest`, and
`:citadel:modelFoundationTest`, `:citadel:modelAnimatorTest` and
`:citadel:tabulaModelTest`. Root source-only model tasks are invoked separately.

On Windows Command Prompt or PowerShell use `gradlew.bat` or `./gradlew.bat`.
Gradle itself can start on JDK 17; compilation uses a Java 25 toolchain.
`settings.gradle` applies Foojay resolver 1.0.0 so a missing JDK 25 can be
downloaded. No machine-specific Java paths are committed. First execution
downloads Gradle, the toolchain and game dependencies. The wrapper comes from
the official Forge MDK:
https://maven.minecraftforge.net/net/minecraftforge/forge/26.1-62.0.9/forge-26.1-62.0.9-mdk.zip

Ignored diagnostic logs are under `.gradle/port-tools/`, including
`baseline-compile.log`, `target-events-compile.log`, `target-podium-compile.log`,
`target-gorgon-checkpoint.log`, `target-focused-hydra.log`, and
`target-hydra-checkpoint.log`, `target-myrmex-compile.log`,
`model-checkpoint.log`, `tabula-checkpoint.log`, `sea-serpent-checkpoint.log`
and `target-full-checkpoint.log`.

## Remaining work

1. `:compileJava` is 0 errors. Remaining live-entity/special renderers still
   need GPU/client proof (armor items, leftover tiles, particles, GUIs,
   `RenderChain`). Tabula animation-container playback is unimplemented.
   Stone statues are extracted but not GPU-validated.
2. Wire persistence into remaining entities and prove save/load, player cloning
   and tracking on a live client/server. Port navigation/collision helpers.
3. Prove remaining Forge registration/events/networking, Minecraft entities,
   save APIs, block entities, inventories, items and content on a running game.
   Armor uses `humanoidArmor` / Citadel material adapter, not 1.18 `ArmorItem`.
4. Resolve optional JEI integration against a verified Forge 26.1 API. It remains
   in compilation without a target dependency, not silently excluded.
5. Migrate access transformers (currently old SRG names and disabled), mixins,
   metadata (`mods.toml` is still 1.18), tags, worldgen and resource formats;
   point `sourceSets.main.resources` at `../src/main/resources` and wire game
   runs. Recipes and `Material` already use 26.1 APIs.
6. Resolve licensing obligations and pass client/server startup, visual
   rendering, save/reload and multiplayer checks before enabling jars.

Do not use the migration project to open existing worlds. There is no working
26.1 mod artifact at this stage.

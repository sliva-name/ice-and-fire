/**
 * Standalone Tabula JSON data subset for the Ice and Fire Forge 26.1 port.
 *
 * <h2>Pinned provenance</h2>
 * Source: AlexModGuy/Citadel, commit
 * {@code 8018e44d8b569913ca828f31aa6c86163319e7a5}, under
 * {@code src/main/java/com/github/alexthe666/citadel/client/model/}.
 * The five Tabula DTOs retain gegy1000's author/since credits and all upstream
 * JSON field names, types, getters and public constructors. The handler retains
 * pau101's author/since credit. TextureOffset and JsonUtils had no author header
 * in the pinned source; no new attribution or license is inferred.
 *
 * <h2>Port changes</h2>
 * Model/cube DTOs gain private no-argument constructors so Gson preserves field
 * defaults without Unsafe. Explicit JSON values, including cubeCount and nulls,
 * are not rewritten. Cube/group nesting and animation sets remain mutable as
 * upstream; parentIdentifier is stored, not synthesized from the hierarchy.
 * TextureOffset loses only Forge side annotations. JsonUtils retains the generic
 * JSON API, excludes Minecraft item registry methods, drops side/nullability
 * annotations, and replaces Commons Lang's middle abbreviation with equivalent
 * JDK string operations. JsonUtils is available for compatibility, not required
 * by the reflective model parser.
 *
 * <p>TabulaModelHandler retains INSTANCE, the JSON stream and classpath overloads,
 * cube traversal/lookups and JsonDeserializationContext. It uses UTF-8, preserves
 * caller ownership of raw JSON streams, adds an owning ZIP-stream API, closes
 * classpath resources, and reports missing/invalid archives as IOExceptions.
 * Empty/null model JSON is rejected. Traversal keeps upstream ordering while
 * avoiding intermediate lists; lookups tolerate missing names/identifiers.
 *
 * <h2>Deliberately excluded</h2>
 * Block-domain registration, resource-manager hooks, ItemTransform adapters,
 * TabulaModelBlock, BakedTabulaModel, VanillaTabulaModel, renderer/model classes
 * and their transform/matrix helpers. Animation execution is a separate port;
 * only the original nested animation JSON DTOs belong to this subset.
 *
 * <h2>Original Ice and Fire integration inspected</h2>
 * TabulaModelHandlerHelper extracts model.json and invokes the JSON-stream API.
 * Its leading-slash ClassLoader lookup is not valid for a normal class loader;
 * new callers can use the handler's correctly normalized classpath API instead.
 * IafClientSetup, DragonAnimationsLibrary, EnumSeaSerpentAnimations and
 * LayerDragonEyes load base/pose models with optional .tbl suffixes. The helper
 * and rendering call sites are intentionally not modified here.
 *
 * <p>Dependency: Gson plus the JDK only (validated with Gson 2.13.2 and JDK 25).
 * The plain-main TabulaAssetsTest accepts one or more resource directories and
 * exercises all .tbl files recursively, plus synthetic hierarchy/animation and
 * error/lifecycle cases. Gradle task wiring is left to the parent port.
 */
package com.github.alexthe666.citadel.client.model.container;

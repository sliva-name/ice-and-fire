# Citadel subset: provenance and distribution status

This is an incomplete, modified subset for the Ice and Fire Minecraft 26.1 /
Forge 62.0.9 migration, not an official Citadel release or a general-purpose
replacement. Jar production is deliberately blocked.

## Upstream attribution

- Citadel by Alexthe666 / AlexModGuy: https://github.com/AlexModGuy/Citadel
- Reference revision: `8018e44d8b569913ca828f31aa6c86163319e7a5`.
- Citadel credits LLibrary, by iLexiconn and Gegy1000, used with permission.
  Existing per-class author credits are retained.
- The original mod's dependency is Curse Maven file `3783098` (Citadel 1.11.3),
  artifact SHA-1 `82d17242b5f330b06833dc72a32d0ae8b5b9e46b`.
  The pinned repository revision's metadata says 1.11.1. It is a reference
  source, **not a verified source archive of the entire 1.11.3 release**.

## Local modifications

Animation identity and timing conventions are retained, while events use
Forge EventBus 7 and synchronization uses a new `citadel:animation` protocol 1
channel. Packets go to tracking players and the entity itself rather than every
player. Client decoding checks entity type and animation indices. There is no
legacy wire compatibility or late-tracker animation catch-up.

Tabula loading/containers are adapted for the bundled assets and Gson, without
Minecraft rendering dependencies. Models use real 26.1 cube geometry and a
native render-state adapter instead of the old live-entity model contract.
Geometry must be complete before constructing that adapter; custom part render
overrides are not invoked by native model rendering. Default-pose restoration
also restores offsets and scale; offsets render in model-pixel units.
Regression tests and the isolated Java 25/Forge build are local additions.

## License verification required before distribution

Citadel distribution metadata declares the GNU Lesser General Public License
without a version; the historical CurseForge listing declares LGPLv3. The
inspected historical repository/artifact did not supply the license text.
LLibrary's upstream license is LGPL 2.1. These facts do not establish a single
verified license version for every extracted component.

The Ice and Fire root LGPLv3 license does not by itself resolve the Citadel /
LLibrary licensing history. This notice is attribution and a record of the
remaining verification task, not a substitute license or a grant of rights.
Before releasing this subset, confirm the applicable version(s), include the
corresponding unmodified license texts and notices, and satisfy their source
and modification-disclosure obligations. Do not remove the packaging guard
until that work and runtime validation are complete.

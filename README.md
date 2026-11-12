# Chest Tracker — NeoForge 26.3

Unofficial NeoForge port of [Chest Tracker](https://github.com/ponuing/ChestTracker) for Minecraft 26.3.

## Requirements

- Minecraft 26.3
- NeoForge 26.3.0.102 or newer
- Java 25
- [YetAnotherConfigLib (YACL) 3.9.7+26.3 for NeoForge](https://modrinth.com/mod/yacl)

## Build

```shell
./gradlew build
```

The built JAR is written to `build/libs/`.

The optional integrations are compiled against the production mod jars, which are not committed. Gradle
downloads them from Modrinth into `libs/` on the first build (task `downloadIntegrationJars`, checksums
pinned in `build.gradle`):

| Mod | Jar |
|---|---|
| Jade | `Jade-mc26.3-NeoForge-26.3.5.jar` |
| JEI | `jei-26.3-neoforge-31.17.0.109.jar` |
| Shulker Box Tooltip | `shulkerboxtooltip-neoforge-5.4.3+26.3.jar` |
| Xaero's Minimap | `xaerominimap-neoforge-26.3-26.6.2.jar` |
| Xaero's World Map | `xaeroworldmap-neoforge-26.3-1.47.2.jar` |

`libs/` is git-ignored. The jars are used for compilation (`compileOnly`) and for the dev client
(`localRuntime`) only; nothing from them is shaded into the output.

## Port status

Core container memory, storage, search, inventory controls, configuration and world highlights are ported. World labels are currently disabled. Litematica, WTHIT and Searchables integrations are not included in this build.

Optional integrations with Jade, Shulker Box Tooltip, JEI and Xaero's Minimap/World Map are included and can be toggled individually in the config screen under Compatibility:

- **Jade** — remembered container contents in the tooltip.
- **Shulker Box Tooltip** — client-side ender chest preview built from the Memory Bank.
- **JEI** — press the search key on a hovered ingredient (ingredient list, bookmarks, recipe slots).
- **Xaero's Minimap / World Map** — search results become temporary waypoints labelled with the amount and item name.

Press `T` while hovering an item in any inventory to find matching saved containers. The inventory closes only when a match exists. This key can be changed in Minecraft's Controls settings.

## 26.3 port notes

- Rendering migrated to the RenderPearl/Blaze3D split (`com.mojang.renderpearl.*`), the submit-node world renderer and `GuiGraphicsExtractor`.
- Screens are now reached through `Minecraft#gui` (`gui.screen()`, `gui.setScreen(...)`), the tab list through `gui.hud`, and toasts through `gui.toastManager()`.
- Input ids come from SDL instead of GLFW (`InputConstants.Type.KEYBOARD`, SDL mouse button ids).
- Dye-coloured items moved into `ColorCollection` (`Items.BED.pick(DyeColor.RED)`).
- World labels stay disabled, so the old `LevelRenderer` mixin was dropped. Highlights are submitted through NeoForge's `SubmitCustomGeometryEvent`; the extra Iris-only immediate-mode pass was removed because `MultiBufferSource` no longer exists.

## Credits and license

Chest Tracker was created by JackFred and is maintained by ponuing. This repository is an unofficial port. The Chest Tracker source and assets remain licensed under LGPL-3.0; see `src/main/resources/LICENSE_chesttracker`.

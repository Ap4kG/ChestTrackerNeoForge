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

## Port status

Core container memory, storage, search, inventory controls, configuration and world highlights are ported. World labels are currently disabled. Optional Litematica, Jade, WTHIT, ShulkerBoxTooltip and Searchables integrations are not included in this build.

Press `T` while hovering an item in any inventory to find matching saved containers. The inventory closes only when a match exists. This key can be changed in Minecraft's Controls settings.

## 26.3 port notes

- Rendering migrated to the RenderPearl/Blaze3D split (`com.mojang.renderpearl.*`), the submit-node world renderer and `GuiGraphicsExtractor`.
- Screens are now reached through `Minecraft#gui` (`gui.screen()`, `gui.setScreen(...)`), the tab list through `gui.hud`, and toasts through `gui.toastManager()`.
- Input ids come from SDL instead of GLFW (`InputConstants.Type.KEYBOARD`, SDL mouse button ids).
- Dye-coloured items moved into `ColorCollection` (`Items.BED.pick(DyeColor.RED)`).
- World labels stay disabled, so the old `LevelRenderer` mixin was dropped. Highlights are submitted through NeoForge's `SubmitCustomGeometryEvent`; the extra Iris-only immediate-mode pass was removed because `MultiBufferSource` no longer exists.

## Credits and license

Chest Tracker was created by JackFred and is maintained by ponuing. This repository is an unofficial port. The Chest Tracker source and assets remain licensed under LGPL-3.0; see `src/main/resources/LICENSE_chesttracker`.

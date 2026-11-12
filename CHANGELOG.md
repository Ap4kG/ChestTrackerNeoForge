Version 3.0.0 is the first build for Minecraft 26.3. The major version bump is for the mod integrations that now ship with the port — Jade, Shulker Box Tooltip, JEI and Xaero's maps. This is a fresh port rather than an incremental update, so read the notes below before reporting anything that used to work differently.

### Added

- Support for Minecraft 26.3 and NeoForge 26.3.0.102.
- **Jade** integration: what a container held when you last opened it is shown in Jade's tooltip, taken from the current Memory Bank.
- **Shulker Box Tooltip** integration: the ender chest preview is built client-side from the Memory Bank instead of asking the server, so it keeps working on Realms and on servers without the mod's server side.
- **JEI** integration: hover an ingredient — in the ingredient list, in bookmarks or in a recipe slot — and press the search key (`T` by default) to look it up in your memories.
- **Xaero's Minimap / World Map** integration: search results are added as temporary waypoints labelled with the amount and the item name (`34x Block of Honey`), and are removed again when the in-world highlight fades.
- Compatibility settings for every integration above, next to the existing Shulker Box Tooltip and WTHIT toggles.

### Changed

- Rendering moved to the RenderPearl/Blaze3D split (`com.mojang.renderpearl.*`), the submit-node world renderer and `GuiGraphicsExtractor`.
- Screens are now opened through `Minecraft#gui`, toasts through `gui.toastManager()`.
- Input ids come from SDL instead of GLFW.
- Dye-coloured item lookups go through `ColorCollection`.
- The Chest Tracker key now defaults to the backtick key; rebind it in Controls if you prefer the old binding.
- The search key now also works with JEI hovered ingredients and outside container screens.
- Mod metadata now points at this repository for the issue tracker and project URL.

### Fixed

- World highlights crashed the client on 26.3 because the custom render pipelines declared no color target state. Both highlight pipelines now set one explicitly.
- Highlights were skipped while an Iris shader pack was active ("no Iris shader mapping for pipeline"). The highlight pipelines now inherit the shader mappings of the closest vanilla pipelines.

### Removed / not included

- World labels (container names in the world) are disabled for now; the `LevelRenderer` mixin that drew them was dropped.
- Litematica, WTHIT and Searchables integrations are not included in this build.

Requires Java 25, NeoForge 26.3.0.102+ and YACL 3.9.7+26.3. Jade, Shulker Box Tooltip, JEI and Xaero's Minimap are optional.

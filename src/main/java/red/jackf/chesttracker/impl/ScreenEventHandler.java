package red.jackf.chesttracker.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ScreenEvent;
import red.jackf.chesttracker.api.gui.ScreenBlacklist;
import red.jackf.chesttracker.impl.compat.mods.jei.JeiIntegration;
import red.jackf.chesttracker.impl.config.ChestTrackerConfig;
import red.jackf.chesttracker.impl.gui.invbutton.CTButtonScreenDuck;
import red.jackf.chesttracker.impl.gui.invbutton.InventoryButtonFeature;
import red.jackf.chesttracker.impl.gui.util.CTTitleOverrideDuck;
import red.jackf.chesttracker.impl.memory.MemoryBankAccessImpl;
import red.jackf.chesttracker.impl.memory.MemoryKeyImpl;
import red.jackf.chesttracker.impl.memory.key.OverrideInfo;
import red.jackf.chesttracker.impl.providers.InteractionTrackerImpl;
import red.jackf.chesttracker.impl.providers.ProviderHandler;
import red.jackf.chesttracker.impl.providers.ScreenCloseContextImpl;
import red.jackf.chesttracker.impl.providers.ScreenOpenContextImpl;
import red.jackf.whereisit.client.api.events.ShouldIgnoreKey;
import red.jackf.whereisit.api.SearchRequest;
import red.jackf.whereisit.client.api.events.SearchInvoker;
import red.jackf.whereisit.client.api.events.SearchRequestPopulator;

import java.util.Optional;

public final class ScreenEventHandler {
    private ScreenEventHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenInit(ScreenEvent.Init.Pre event) {
        Screen screen = event.getScreen();
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            ProviderHandler.INSTANCE.getCurrentProvider().ifPresent(provider -> {
                ScreenOpenContextImpl openContext = ScreenOpenContextImpl.createFor(containerScreen);
                provider.onScreenOpen(openContext);
                ((CTButtonScreenDuck) containerScreen).chesttracker$setContext(openContext);

                if (ChestTrackerConfig.INSTANCE.instance().gui.useCustomNameInGUIs) {
                    MemoryBankAccessImpl.INSTANCE.getLoadedInternal().ifPresent(bank -> {
                        if (openContext.getTarget() != null) {
                            Optional<MemoryKeyImpl> key = bank.getKeyInternal(openContext.getTarget().memoryKey());
                            if (key.isPresent()) {
                                OverrideInfo override = key.get().overrides().get(openContext.getTarget().position());
                                if (override != null && override.getCustomName() != null) {
                                    ((CTTitleOverrideDuck) containerScreen).chesttracker$setTitleOverride(Component.literal(override.getCustomName()));
                                } else {
                                    ((CTTitleOverrideDuck) containerScreen).chesttracker$clearTitleOverride();
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof PauseScreen) MemoryBankAccessImpl.INSTANCE.save();
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (screen instanceof AbstractContainerScreen<?>) {
            InventoryButtonFeature.onScreenOpen(client, screen, event.getScreen().width, event.getScreen().height)
                    .ifPresent(event::addListener);
        }
    }

    @SubscribeEvent
    public static void onScreenKeyPressedPre(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (ShouldIgnoreKey.EVENT.invoker().shouldIgnoreKey()) return;
        if (ChestTracker.SEARCH_HOVERED_ITEM == null || !ChestTracker.SEARCH_HOVERED_ITEM.matches(event.getKeyEvent())) return;

        ItemStack target = ItemStack.EMPTY;

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            var hoveredSlot = ((CTButtonScreenDuck) containerScreen).chesttracker$getHoveredSlot();
            if (hoveredSlot != null && hoveredSlot.hasItem()) target = hoveredSlot.getItem();
        }

        // Nothing in the container under the cursor - fall back to JEI, which also covers its recipe screen
        // and the ingredient list overlay drawn over other menus.
        if (target.isEmpty() && ModList.get().isLoaded(JeiIntegration.MOD_ID))
            target = JeiIntegration.getHoveredItem(screen).orElse(ItemStack.EMPTY);

        if (target.isEmpty()) return;

        SearchRequest request = new SearchRequest();
        SearchRequestPopulator.addItemStack(request, target, SearchRequestPopulator.Context.RECIPE);
        SearchInvoker.doSearchIfFound(request);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
        Screen screen = event.getScreen();
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (screen instanceof AbstractContainerScreen<?>) {
            if (ShouldIgnoreKey.EVENT.invoker().shouldIgnoreKey()) return;
            if (ChestTracker.OPEN_GUI != null && ChestTracker.OPEN_GUI.matches(event.getKeyEvent())) {
                ChestTracker.openInGame(client, screen);
            }
        }
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        Screen screen = event.getScreen();
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            if (!ScreenBlacklist.isBlacklisted(screen.getClass())) {
                if (!ChestTracker.shouldSkipProvider()) {
                    ProviderHandler.INSTANCE.getCurrentProvider().ifPresent(provider ->
                            provider.onScreenClose(ScreenCloseContextImpl.createFor(containerScreen)));
                    InteractionTrackerImpl.INSTANCE.clear();
                } else {
                    ChestTracker.clearSkipProvider();
                }
            } else {
                ChestTracker.LOGGER.debug("Blacklisted screen class, ignoring");
            }
        }
    }
}

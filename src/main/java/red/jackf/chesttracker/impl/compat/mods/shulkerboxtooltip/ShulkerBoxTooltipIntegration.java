package red.jackf.chesttracker.impl.compat.mods.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.neoforge.ShulkerBoxTooltipPlugin;
import net.neoforged.fml.ModContainer;

import java.util.function.Supplier;

/**
 * Registers {@link ChestTrackerShulkerBoxTooltip} with Shulker Box Tooltip. Kept in its own class so that
 * nothing referencing the Shulker Box Tooltip API is loaded unless that mod is present.
 */
public class ShulkerBoxTooltipIntegration {
    private ShulkerBoxTooltipIntegration() {
    }

    public static void register(ModContainer container) {
        container.registerExtensionPoint(ShulkerBoxTooltipPlugin.class,
                (Supplier<ShulkerBoxTooltipPlugin>) () -> new ShulkerBoxTooltipPlugin(ChestTrackerShulkerBoxTooltip::new));
    }
}

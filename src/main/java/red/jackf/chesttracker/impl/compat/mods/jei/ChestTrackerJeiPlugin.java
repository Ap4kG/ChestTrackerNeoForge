package red.jackf.chesttracker.impl.compat.mods.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import red.jackf.chesttracker.impl.ChestTracker;

/**
 * Lets the player search for whatever ingredient is hovered in JEI (ingredient list, bookmarks or a recipe
 * slot) using Chest Tracker's "search hovered item" key. Registration happens through JEI's annotation scan,
 * so this class is only ever loaded when JEI is installed.
 */
@JeiPlugin
public class ChestTrackerJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return ChestTracker.id("jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JeiIntegration.setRuntime(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable() {
        JeiIntegration.setRuntime(null);
    }
}

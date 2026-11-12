package red.jackf.chesttracker.impl.compat.mods.jei;

import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import red.jackf.chesttracker.impl.config.ChestTrackerConfig;

import java.util.Optional;

/**
 * Bridge to the JEI runtime. {@link #getHoveredItem(Screen)} resolves the ingredient under the cursor the same way
 * JEI's own keybinds do, so it also works inside JEI's recipe screen and on the ingredient list overlay.
 */
public class JeiIntegration {
    public static final String MOD_ID = "jei";

    @Nullable
    private static IJeiRuntime runtime;

    private JeiIntegration() {
    }

    static void setRuntime(@Nullable IJeiRuntime runtime) {
        JeiIntegration.runtime = runtime;
    }

    public static boolean isAvailable() {
        return runtime != null && ChestTrackerConfig.INSTANCE.instance().compatibility.jeiIntegration;
    }

    public static Optional<ItemStack> getHoveredItem(Screen screen) {
        if (!isAvailable()) return Optional.empty();

        Minecraft client = Minecraft.getInstance();
        MouseHandler mouse = client.mouseHandler;
        double mouseX = mouse.getScaledXPos(client.getWindow());
        double mouseY = mouse.getScaledYPos(client.getWindow());

        return runtime.getScreenHelper()
                .getClickableIngredientUnderMouse(screen, mouseX, mouseY)
                .map(clickable -> clickable.getTypedIngredient().getItemStack())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(stack -> !stack.isEmpty())
                .findFirst();
    }
}

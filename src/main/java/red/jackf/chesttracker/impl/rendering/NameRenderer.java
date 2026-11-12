package red.jackf.chesttracker.impl.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;

/** Container-name world rendering is disabled in the NeoForge 26.3 port. */
public final class NameRenderer {
    private NameRenderer() {}

    public static void setup() {}

    public static void scheduleLabels() {}

    public static boolean hasScheduledLabels() { return false; }

    public static void renderLabels(PoseStack poseStack, Camera camera) {}

    public static void clearScheduledLabels() {}
}

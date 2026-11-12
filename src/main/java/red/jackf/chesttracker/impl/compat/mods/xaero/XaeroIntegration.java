package red.jackf.chesttracker.impl.compat.mods.xaero;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import red.jackf.chesttracker.impl.config.ChestTrackerConfig;
import red.jackf.chesttracker.impl.util.Strings;
import red.jackf.whereisit.api.SearchResult;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.TemporaryWaypointHandler;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Mirrors Chest Tracker search results onto the Xaero minimap / world map as temporary waypoints. They are removed
 * again once the in-world highlights expire, so nothing is left behind in the player's waypoint list.
 */
public class XaeroIntegration {
    public static final String MOD_ID = "xaerominimap";

    private static final List<CreatedWaypoint> CREATED = new ArrayList<>();

    private XaeroIntegration() {
    }

    private record CreatedWaypoint(WaypointSet set, Waypoint waypoint) {
    }

    public static void onResultsAdded(Collection<SearchResult> results) {
        if (results.isEmpty()) return;
        if (!ChestTrackerConfig.INSTANCE.instance().compatibility.xaeroIntegration) return;

        WaypointSet set = currentWaypointSet();
        if (set == null) return;
        TemporaryWaypointHandler handler = currentHandler();
        if (handler == null) return;
        MinimapWorld world = currentWorld();
        if (world == null) return;

        for (SearchResult result : results) {
            BlockPos pos = result.pos();
            Set<Waypoint> before = identitySet(set);
            handler.createTemporaryWaypoint(world, pos.getX(), pos.getY(), pos.getZ());

            for (Waypoint waypoint : set.getWaypoints()) {
                if (before.contains(waypoint)) continue;
                if (waypoint.getX() != pos.getX() || waypoint.getY() != pos.getY() || waypoint.getZ() != pos.getZ())
                    continue;
                applyLabel(waypoint, result);
                CREATED.add(new CreatedWaypoint(set, waypoint));
                break;
            }
        }
    }

    /**
     * Replaces Xaero's generic "Waypoint" label with the item amount and name, e.g. {@code 34x Block of Honey}.
     */
    private static void applyLabel(Waypoint waypoint, SearchResult result) {
        ItemStack item = result.item();
        String itemName = item == null ? null : item.getHoverName().getString();

        if (itemName == null || itemName.isBlank()) {
            Component containerName = result.name();
            if (containerName == null) return;
            itemName = containerName.getString();
            waypoint.setName(itemName);
            waypoint.setSymbol(symbolFor(itemName));
            return;
        }

        int count = result.count();
        waypoint.setName(count > 1 ? Strings.commaSeparated(count) + "x " + itemName : itemName);
        waypoint.setSymbol(symbolFor(itemName));
    }

    private static String symbolFor(String label) {
        for (int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if (Character.isLetterOrDigit(c)) return String.valueOf(Character.toUpperCase(c));
        }
        return "X";
    }

    public static void clearWaypoints() {
        if (CREATED.isEmpty()) return;

        for (CreatedWaypoint created : CREATED) {
            created.set().remove(created.waypoint());
        }
        CREATED.clear();
    }

    private static Set<Waypoint> identitySet(WaypointSet set) {
        Set<Waypoint> waypoints = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Waypoint waypoint : set.getWaypoints()) waypoints.add(waypoint);
        return waypoints;
    }

    @Nullable
    private static MinimapSession currentSession() {
        if (!ModList.get().isLoaded(MOD_ID)) return null;
        XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
        if (session == null) return null;
        var processor = session.getMinimapProcessor();
        return processor == null ? null : processor.getSession();
    }

    @Nullable
    private static MinimapWorld currentWorld() {
        MinimapSession session = currentSession();
        return session == null ? null : session.getWorldManager().getCurrentWorld();
    }

    @Nullable
    private static WaypointSet currentWaypointSet() {
        MinimapWorld world = currentWorld();
        return world == null ? null : world.getCurrentWaypointSet();
    }

    @Nullable
    private static TemporaryWaypointHandler currentHandler() {
        MinimapSession session = currentSession();
        if (session == null) return null;
        var waypointSession = session.getWaypointSession();
        return waypointSession == null ? null : waypointSession.getTemporaryHandler();
    }
}

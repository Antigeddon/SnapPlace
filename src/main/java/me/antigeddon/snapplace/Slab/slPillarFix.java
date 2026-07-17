package me.antigeddon.snapplace.Slab;

import me.antigeddon.snapplace.bDebug;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class slPillarFix {

    public static class PillarState {
        public final List<Block> affected = new ArrayList<>();
        public final List<Material> types = new ArrayList<>();
        public final List<Byte> data = new ArrayList<>();
    }
    public static PillarState scan(Player player, Block target) {
        bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_SCAN_START, "Starting scan from " + target.getLocation().toVector());
        PillarState state = new PillarState();
        Block current = target;

        while (current != null) {

            if (current.getY() <= 0) {
                break;
            }

            Block below = current.getRelative(BlockFace.DOWN);

            if (below.getY() >= current.getY()) {
                break;
            }

            if (below.getType() == Material.STEP && below.getData() == current.getData()) {
                state.affected.add(below);
                state.types.add(below.getType());
                state.data.add(below.getData());
                current = below;
            } else {
                break;
            }
        }

        bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_SCAN_COMPLETE, "Found " + state.affected.size() + " slab(s) below.");
        return state;
    }

    public static void restoreBlocks1(Player player, PillarState state, boolean packetOnly) {
        if (state == null || state.affected.isEmpty()) {
            bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_RESTORE1_NODATA, "");
            return;
        }

        bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_RESTORE1_START, "Temporarily clearing " + state.affected.size() + " slab(s).");
        for (Block b : state.affected) {
            if (!packetOnly) {
                setBlockNMS(b, 0, (byte) 0, true);
            } else {
                player.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0);
            }
        }
    }

    public static void restoreBlocks2(Player player, PillarState state, boolean packetOnly) {
        if (state == null || state.affected.isEmpty()) {
            bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_RESTORE2_NODATA, "");
            return;
        }

        bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_RESTORE2_START, "Restoring " + state.affected.size() + " slab(s) to original state.");
        for (int i = 0; i < state.affected.size(); i++) {
            Block b        = state.affected.get(i);
            Material type  = state.types.get(i);
            byte meta      = state.data.get(i);

            if (!packetOnly) {
                setBlockNMS(b, type.getId(), meta, true);
            } else {
                player.sendBlockChange(b.getLocation(), type, meta);
            }
        }

        bDebug.debug(player, bDebug.DebugType.SLAB_PILLARFIX_RESTORE2_SUCCESS, "");
    }

    // No Block Update when pillar is set
    public static void setBlockNMS(Block block, int typeId, byte data, boolean notify) {
        int localY = block.getY();
        if (localY < 0 || localY > 127) return;

        net.minecraft.server.WorldServer nmsWorld = ((org.bukkit.craftbukkit.CraftWorld) block.getWorld()).getHandle();
        net.minecraft.server.Chunk chunk = nmsWorld.getChunkAt(block.getX() >> 4, block.getZ() >> 4);

        int localX = block.getX() & 15;
        int localZ = block.getZ() & 15;

        int index = (localX << 11) | (localZ << 7) | localY;
        int nibbleIndex = index >> 1;
        int parity = index & 1;

        chunk.b[index] = (byte) typeId;

        if (parity == 0) {
            chunk.e.a[nibbleIndex] = (byte) ((chunk.e.a[nibbleIndex] & 240) | (data & 15));
        } else {
            chunk.e.a[nibbleIndex] = (byte) ((chunk.e.a[nibbleIndex] & 15) | ((data & 15) << 4));
        }
        if (notify)
            nmsWorld.notify(block.getX(), block.getY(), block.getZ());
    }
}
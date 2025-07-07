package me.antigeddon.snapplace.Slab;

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

    private static final Map<UUID, List<Block>> playerAffectedBlocks = new HashMap<>();
    private static final Map<UUID, List<Material>> playerOriginalTypes = new HashMap<>();
    private static final Map<UUID, List<Byte>> playerOriginalData = new HashMap<>();

    public static void checkAndStoreStepLoop(Player player, Block target) {
        UUID playerId = player.getUniqueId();
        List<Block> affected = new ArrayList<>();
        List<Material> types = new ArrayList<>();
        List<Byte> data = new ArrayList<>();

        Block current = target;

        while (current != null) {
            Block below = current.getRelative(BlockFace.DOWN);
            if (below.getType() == Material.STEP && below.getData() == current.getData()) {
                affected.add(below);
                types.add(below.getType());
                data.add(below.getData());
                current = below;
            } else {
                break;
            }
        }

        playerAffectedBlocks.put(playerId, affected);
        playerOriginalTypes.put(playerId, types);
        playerOriginalData.put(playerId, data);
    }

    public static void restoreBlocks1(Player player) {
        UUID playerId = player.getUniqueId();
        List<Block> affected = playerAffectedBlocks.get(playerId);
        if (affected == null) return;
        for (Block b : affected) {
            b.setType(Material.AIR);
            player.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0);
        }
    }

    public static void restoreBlocks2(Player player) {
        UUID playerId = player.getUniqueId();
        List<Block> affected = playerAffectedBlocks.get(playerId);
        List<Material> types = playerOriginalTypes.get(playerId);
        List<Byte> data      = playerOriginalData.get(playerId);
        if (affected == null || types == null || data == null) return;

        for (int i = 0; i < affected.size(); i++) {
            Block b        = affected.get(i);
            Material type  = types.get(i);
            byte meta      = data.get(i);
            b.setType(type);
            if (type == Material.STEP || type == Material.DOUBLE_STEP) {
                b.setData(meta);
            }
            player.sendBlockChange(b.getLocation(), type, meta);
        }

        playerAffectedBlocks.remove(playerId);
        playerOriginalTypes .remove(playerId);
        playerOriginalData  .remove(playerId);
    }

}

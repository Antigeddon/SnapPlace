package me.antigeddon.snapplace.Slab;

import me.antigeddon.snapplace.Place.bPlaceOnInteractable;
import me.antigeddon.snapplace.Place.bBlockType;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class slAntiMerge implements Listener {

    @EventHandler
    public void onSlabPlace(PlayerInteractEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);
        boolean bEnable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        boolean placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);

        if (event.isCancelled())
            return;

        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getItem() == null)
            return;

        if (!enable)
            return;

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterslabs"))
            return;

        Material itemType = event.getItem().getType();

        if (itemType != Material.STEP && itemType != Material.DOUBLE_STEP)
            return;

        ItemStack inHand = event.getItem();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null)
            return;

        if (bBlockType.isClickable(clickedBlock.getType())) {
            if (!player.isSneaking() || !bEnable || !placeOn)
                return;

            if (!player.isOp() &&
                    !player.hasPermission("SnapPlace.betterplacements.interactables"))
                return;
        }

        if (clickedBlock.getType() == Material.SNOW)
            return;

        if (event.getBlockFace() == BlockFace.UP && itemType == Material.STEP)
            return;

        BlockFace face = event.getBlockFace();
        Block target = clickedBlock.getRelative(face);
        Block below = target.getRelative(BlockFace.DOWN);

        if (below.getType() != Material.STEP)
            return;

        byte handData = inHand.getData().getData();
        byte belowData = below.getData();

        if (belowData != handData)
            if (!(belowData == 0 && itemType == Material.DOUBLE_STEP))
                return;

        Block above = below.getRelative(BlockFace.UP);
        Material aboveType = above.getType();
        byte aboveData = above.getData();

            if (!bBlockType.isFluid(aboveType)) {
                return;
            }

            if (event.getClickedBlock().getRelative(BlockFace.UP).equals(below)) {
                event.setCancelled(true);
                return;
            }

            if (bBlockType.isEntityBlockingBlock(above.getLocation(), player, above.getType(), handData)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);

            slPillarFix.checkAndStoreStepLoop(player, below);

            below.setType(Material.AIR);

            if (itemType == Material.DOUBLE_STEP) {
                above.getWorld().getBlockAt(target.getX(), above.getY(), above.getZ()).setTypeIdAndData(43, handData, true);

            } else {
                above.getWorld().getBlockAt(target.getX(), above.getY(), above.getZ()).setTypeIdAndData(44, handData, true);
            }

            slPillarFix.restoreBlocks1(player);

            target.getWorld().getBlockAt(target.getX(), target.getY() - 1, target.getZ()).setTypeIdAndData(44, belowData, true);

            slPillarFix.restoreBlocks2(player);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendBlockChange(above.getLocation(), itemType, handData);
                p.sendBlockChange(target.getLocation(), itemType, handData);
                p.sendBlockChange(below.getLocation(), itemType, belowData);
            }

            BlockState targetBlockState = target.getState();
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                    target,
                    targetBlockState,
                    clickedBlock,
                    inHand,
                    player,
                    true);
            org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);
            if (placeEvent.isCancelled()) {
                above.getWorld().getBlockAt(target.getX(), above.getY(), above.getZ()).setTypeIdAndData(aboveType.getId(), aboveData, false);
                player.sendBlockChange(below.getLocation(), 44, belowData);
                return;
            }
            bPlaceOnInteractable.removeOneItemFromHand(player);
    }

    @EventHandler
    public void onSlabBreak(BlockBreakEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);

        Player player = event.getPlayer();

        if (!enable)
            return;

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterslabs"))
            return;

        Block block = event.getBlock();
        int typeId = block.getTypeId();
        byte data = block.getData();

        if (block.getType() != Material.STEP && block.getType() != Material.DOUBLE_STEP)
            return;

        Block below = block.getRelative(0, -1, 0);

        if (below.getType() != Material.STEP)
            return;

        if (event.isCancelled()) {
            slPillarFix.checkAndStoreStepLoop(player, block);
            slPillarFix.restoreBlocks1(player);
            player.sendBlockChange(block.getLocation(), typeId, data);
            slPillarFix.restoreBlocks2(player);
        }
    }
}
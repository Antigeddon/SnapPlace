package me.antigeddon.snapplace.Slab;

import me.antigeddon.snapplace.Place.bPlaceOnInteractable;
import me.antigeddon.snapplace.Place.bBlockType;
import me.antigeddon.snapplace.bDebug;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class slAntiMerge implements Listener {

    @EventHandler(priority = Event.Priority.High, ignoreCancelled = true)
    public void onSlabPlace(PlayerInteractEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);
        boolean bEnable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        boolean placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);

        if (event.isCancelled()) {
            bDebug.debug(event.getPlayer(), bDebug.DebugType.SLAB_MERGING_EVENT_CANCELLED,
                    "Cancelled by " + event.getEventName());
            return;
        }

        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_WRONG_ACTION, "Action = " + event.getAction());
            return;
        }

        if (event.getItem() == null) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_NO_ITEM, "");
            return;
        }

        if (!enable) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_DISABLED, "");
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterslabs")) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_NO_PERMISSION, "");
            return;
        }

        Material itemType = event.getItem().getType();

        if (itemType != Material.STEP && itemType != Material.DOUBLE_STEP) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_NOT_SLAB, "ItemType = " + itemType);
            return;
        }

        ItemStack inHand = event.getItem();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_CLICKED_NULL, "");
            return;
        }

        if (bBlockType.isClickable(clickedBlock.getType())) {
            if (!player.isSneaking() || !bEnable || !placeOn) {
                bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_CLICKED_INTERACTABLE_NO_SNEAK, "Sneaking = " + player.isSneaking() + ", bEnable = " + bEnable + ", placeOn = " + placeOn);
                return;
            }

            if (!player.isOp() &&
                    !player.hasPermission("SnapPlace.betterplacements.interactables")) {
                bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_NO_PERMISSION_INTERACT, "");
                return;
            }
        }

        if (clickedBlock.getType() == Material.SNOW) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_CLICKED_SNOW, "");
            return;
        }

        if (event.getBlockFace() == BlockFace.UP && itemType == Material.STEP) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_FACE_UP_FOR_BETWEEN, "Face = " + event.getBlockFace());
            return;
        }

        BlockFace face = event.getBlockFace();
        Block target = clickedBlock.getRelative(face);
        Block below = target.getRelative(BlockFace.DOWN);

        if (below.getType() != Material.STEP) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_NO_SLAB_BELOW, "BelowBlockType = " + below.getType());
            return;
        }

        byte handData = inHand.getData().getData();
        byte belowData = below.getData();

        if (belowData != handData)
            if (!(belowData == 0 && itemType == Material.DOUBLE_STEP)) {
                bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_DATA_MISMATCH, "HandData = " + handData + ", BelowBlockData = " + belowData);
                return;
            }

        Block above = below.getRelative(BlockFace.UP);
        Material aboveType = above.getType();
        byte aboveData = above.getData();

        if (!bBlockType.isFluid(aboveType)) {
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_TARGET_BLOCKED, "AboveBlockType = " + aboveType);
            return;
        }

        if (event.getClickedBlock().getRelative(BlockFace.UP).equals(below)) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_INVALID_GEOMETRY, "");
            return;
        }

        if (bBlockType.isEntityBlockingBlock(above.getLocation(), player, above.getType(), handData)) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_ENTITY_BLOCKING, "");
            return;
        }

        event.setCancelled(true);

        BlockState targetBlockState = target.getState();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                target,
                targetBlockState,
                clickedBlock,
                inHand,
                player,
                true);

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

        org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            above.getWorld().getBlockAt(target.getX(), above.getY(), above.getZ()).setTypeIdAndData(aboveType.getId(), aboveData, false);
            player.sendBlockChange(below.getLocation(), 44, belowData);
            bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_PLACE_CANCELLED, "");
            return;
        }

        bPlaceOnInteractable.removeOneItemFromHand(player);
        bDebug.debug(player, bDebug.DebugType.SLAB_MERGING_SUCCESS, "");
    }

    @EventHandler
    public void onSlabBreak(BlockBreakEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);

        Player player = event.getPlayer();

        if (!enable) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BREAK_MERGING_DISABLED, "");
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterslabs")) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BREAK_MERGING_NO_PERMISSION, "");
            return;
        }

        Block block = event.getBlock();
        int typeId = block.getTypeId();
        byte data = block.getData();

        if (block.getType() != Material.STEP && block.getType() != Material.DOUBLE_STEP)
            return;

        Block below = block.getRelative(0, -1, 0);

        if (below.getType() != Material.STEP) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BREAK_MERGING_NO_SLAB_BELOW, "BelowBlockType " + below.getType());
            return;
        }

        bDebug.debug(player, bDebug.DebugType.SLAB_BREAK_MERGING_EVENT, "Event = " + event.isCancelled());

        if (event.isCancelled()) {
            slPillarFix.checkAndStoreStepLoop(player, block);
            slPillarFix.restoreBlocks1(player);
            player.sendBlockChange(block.getLocation(), typeId, data);
            slPillarFix.restoreBlocks2(player);
            bDebug.debug(player, bDebug.DebugType.SLAB_BREAK_MERGING_CANCELLED_RESTORE, "");
        }
    }
}
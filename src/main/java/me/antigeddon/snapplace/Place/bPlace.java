package me.antigeddon.snapplace.Place;

import me.antigeddon.snapplace.Slab.slPlaceBetween;
import me.antigeddon.snapplace.bDebug;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Rails;

public class bPlace implements Listener {

    private final boolean enable;
    private final boolean pumpkin;
    private final boolean sneak;
    private final boolean placeOn;
    private final boolean rail;
    private final boolean sneakRail;

    private static final int[][] ADJACENT_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public bPlace() {
        this.enable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        this.pumpkin = bMain.getPluginConfig().getBoolean("better-placements.placeable-on-walls-and-roofs.enable", true);
        this.sneak = bMain.getPluginConfig().getBoolean("better-placements.placeable-on-walls-and-roofs.need-sneaking", true);
        this.placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);
        this.rail = bMain.getPluginConfig().getBoolean("better-placements.orientable-rails.enable", true);
        this.sneakRail = bMain.getPluginConfig().getBoolean("better-placements.orientable-rails.need-sneaking", false);
    }

    @EventHandler(priority = Event.Priority.High, ignoreCancelled = true)
    public void onSneakPlace(PlayerInteractEvent event) {

        if (event.isCancelled()) {
            bDebug.debug(event.getPlayer(), bDebug.DebugType.WRPLACE_FIRST_EVENT_CANCELLED,
                    "Cancelled by " + event.getEventName());
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            bDebug.debug(event.getPlayer(), bDebug.DebugType.WRPLACE_ACTION,
                    "Action = " + event.getAction());
            return;
        }

        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();

        if (inHand == null) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_NO_ITEM, "Player is not holding anything");
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterplacements.wallandroof")) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_NO_PERMISSION, "Missing permission: SnapPlace.betterplacements.wallandroof");
            return;
        }

        Material itemType = inHand.getType();

        if (!enable || !pumpkin) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_FEATURE_DISABLED, "enable = " + enable + ", placeable-on-walls-and-roofs.enable = " + pumpkin);
            return;
        }

        if (!player.isSneaking() && sneak) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_NOT_SNEAKING, "");
            return;
        }

        if (!(itemType == Material.FENCE || itemType == Material.JACK_O_LANTERN || itemType == Material.PUMPKIN)) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_UNSUPPORTED_ITEM, "ItemType = " + itemType);
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_CLICKED_NULL, "");
            return;
        }

        if (bBlockType.isClickable(clickedBlock.getType())) {
            if (!player.isSneaking() || !placeOn || !player.hasPermission("SnapPlace.betterplacements.interactables")) {
                bDebug.debug(player, bDebug.DebugType.WRPLACE_CLICKABLE_BLOCK, "ClickedBlockType = " + clickedBlock.getType());
                return;
            }
        }

        BlockFace face = event.getBlockFace();
        Block targetBlock = clickedBlock.getRelative(face);

        if ((face == BlockFace.UP && targetBlock.getY() <= clickedBlock.getY()) ||
                (face == BlockFace.DOWN && targetBlock.getY() >= clickedBlock.getY())) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_BLOCK_TARGET_OUTSIDE, "");
            return;
        }

        if (targetBlock.getY() >= 127 && !bBlockType.isBuildableAt127(itemType)) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_BLOCK_Y127, "ItemType = " + itemType);
            return;
        }

        Material targetType = targetBlock.getType();
        byte targetData = targetBlock.getData();
        Block blockUnder = targetBlock.getRelative(BlockFace.DOWN);

        Material underType = (targetBlock.getY() == 0) ? Material.AIR : blockUnder.getType();

        if (!bBlockType.isNotSolid(underType)) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_BLOCK_UNDER_SOLID, "UnderBlockType = " + underType);
            return;
        }

        if (bBlockType.isFluid(clickedBlock.getType())) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_CLICKED_FLUID, "ClickedBlockType = " + clickedBlock.getType());
            return;
        }

        if (!bBlockType.isFluid(targetType)) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_TARGET_NOT_FLUID, "BlockType = " + targetType);
            return;
        }

        if (bBlockType.isEntityBlockingBlock(targetBlock.getLocation(), player, itemType, targetBlock.getData())) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_ENTITY_BLOCKING, "");
            return;
        }

        event.setCancelled(true);

        BlockState replacedBlockState = targetBlock.getState();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                targetBlock,
                replacedBlockState,
                clickedBlock,
                inHand,
                player,
                true
        );

        byte direction = 0;
        if (itemType == Material.PUMPKIN || itemType == Material.JACK_O_LANTERN) {
            float yaw = player.getLocation().getYaw();
            direction = getPumpkinDirection(yaw);
        }

        boolean isCancelled = bBlockType.placeEventPlacingSimulation(targetBlock, itemType, direction, placeEvent);

        if (isCancelled) {
            bDebug.debug(player, bDebug.DebugType.WRPLACE_PLACE_CANCELLED, "");
            return;
        }

        if (itemType == Material.PUMPKIN || itemType == Material.JACK_O_LANTERN) {
            targetBlock.setTypeIdAndData(itemType.getId(), direction, true);
            bDebug.debug(player, bDebug.DebugType.WRPLACE_DIRECTION_SET, "PumpkinDirection = " + direction);
        } else {
            // Fence
            targetBlock.setTypeIdAndData(itemType.getId(), (byte) 0, true);
        }

        bPlaceOnInteractable.removeOneItemFromHand(player);
        if (!bBlockType.isClickable(clickedBlock.getType())) {
            slPlaceBetween.swingArm(player);
        }

        bDebug.debug(player, bDebug.DebugType.WRPLACE_SUCCESS, "BlockType = " + itemType + ", BlockData = " + targetBlock.getData());
    }



    public static byte getPumpkinDirection(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 45 && yaw < 135) {
            return 3;
        } else if (yaw >= 135 && yaw < 225) {
            return 0;
        } else if (yaw >= 225 && yaw < 315) {
            return 1;
        } else {
            return 2;
        }
    }

    @EventHandler(priority = Event.Priority.High, ignoreCancelled = true)
    public void onRailPlace(BlockPlaceEvent event) {

        if (event.isCancelled()) {
            bDebug.debug(event.getPlayer(), bDebug.DebugType.RAIL_FIRST_EVENT_CANCELLED, "Cancelled by " + event.getEventName());
            return;
        }

        if (!enable || !rail) {
            bDebug.debug(event.getPlayer(), bDebug.DebugType.RAIL_FEATURE_DISABLED, "");
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (!isRail(block)) {
            bDebug.debug(player, bDebug.DebugType.RAIL_NOT_RAIL, "BlockType = " + block.getType());
            return;
        }

        if (!player.isSneaking() && sneakRail) {
            bDebug.debug(player, bDebug.DebugType.RAIL_NOT_SNEAKING, "");
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterplacements.rails")) {
            bDebug.debug(player, bDebug.DebugType.RAIL_NO_PERMISSION, "Missing permission: SnapPlace.betterplacements.rails");
            return;
        }

        byte orientation = hasAdjacentRail(block, player.getLocation().getYaw());

        if (orientation != -1) {
            if (isRail(block)) {
                BlockState snapshot = block.getState();
                if (block.getType() == Material.POWERED_RAIL) {
                    snapshot.setData(new org.bukkit.material.PoweredRail(block.getType(), orientation));
                } else if (block.getType()  == Material.DETECTOR_RAIL) {
                    snapshot.setData(new org.bukkit.material.DetectorRail(block.getType() , orientation));
                } else {
                    snapshot.setData(new org.bukkit.material.Rails(block.getType() , orientation));
                }

                snapshot.update(true);
                bDebug.debug(player, bDebug.DebugType.RAIL_ORIENTED, "RailOrientation = " + orientation);
            } else {
                bDebug.debug(player, bDebug.DebugType.RAIL_WRONG, "PlacedBlock = " + block.getType() + ", PlacedData = " + block.getData());
            }
        } else {
            bDebug.debug(player, bDebug.DebugType.RAIL_NO_ORIENTATION, "");
        }
    }

    public static boolean isRail(Block block) {
        if (block == null)
            return false;

        Material type = block.getType();
        return type == Material.RAILS ||
                type == Material.POWERED_RAIL ||
                type == Material.DETECTOR_RAIL;
    }

    public static byte hasAdjacentRail(Block block, float yaw) {

        if (block == null)
            return -1;

        Block blockAbove = block.getRelative(0, 1, 0);
        Block blockBelow = block.getRelative(0, -1, 0);

        for (int[] dir : ADJACENT_DIRECTIONS) {
            if (isRail(block.getRelative(dir[0], 0, dir[1])) ||
                    isRail(blockAbove.getRelative(dir[0], 0, dir[1])) ||
                    isRail(blockBelow.getRelative(dir[0], 0, dir[1]))) {
                return -1;
            }
        }

        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        return ((yaw >= 45 && yaw <= 135) || (yaw >= 225 && yaw <= 315)) ? (byte) 1 : (byte) 0;
    }
}
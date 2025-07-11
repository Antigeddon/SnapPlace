package me.antigeddon.snapplace.Place;

import me.antigeddon.snapplace.Slab.slPlaceBetween;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class bPlace implements Listener {

    @EventHandler
    public void onSneakPlace(PlayerInteractEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        boolean pumpkin = bMain.getPluginConfig().getBoolean("better-placements.placeable-on-walls-and-roofs.enable", true);
        boolean sneak = bMain.getPluginConfig().getBoolean("better-placements.placeable-on-walls-and-roofs.need-sneaking", true);
        boolean placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();

        if (inHand == null)
            return;

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterplacements.wallandroof"))
            return;

        Material itemType = inHand.getType();

        if (!enable || !pumpkin)
            return;

        if (!player.isSneaking() && sneak)
            return;

        if (!(itemType == Material.FENCE || itemType == Material.JACK_O_LANTERN || itemType == Material.PUMPKIN))
            return;

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null)
            return;

        if (bBlockType.isClickable(clickedBlock.getType()))
            if (!player.isSneaking() || !placeOn || !player.hasPermission("SnapPlace.betterplacements.interactables"))
                return;

        BlockFace face = event.getBlockFace();
        Block targetBlock = clickedBlock.getRelative(face);
        Material targetType = targetBlock.getType();
        byte targetData = targetBlock.getData();
        Block blockUnder = targetBlock.getRelative(BlockFace.DOWN);

        if (!bBlockType.isNotSolid(blockUnder.getType()))
            return;

        if (bBlockType.isFluid(clickedBlock.getType()))
            return;

        if (!bBlockType.isFluid(targetType))
            return;

        if (bBlockType.isEntityBlockingBlock(targetBlock.getLocation(), player, itemType, targetBlock.getData()))
            return;

        event.setCancelled(true);

        targetBlock.setType(itemType);

        if (itemType == Material.PUMPKIN || itemType == Material.JACK_O_LANTERN) {
            float yaw = player.getLocation().getYaw();
            byte direction = getPumpkinDirection(yaw);
            targetBlock.setData(direction);
        }

        BlockState replacedBlockState = targetBlock.getState();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                targetBlock,
                replacedBlockState,
                clickedBlock,
                inHand,
                player,
                true
        );

        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            targetBlock.setType(targetType);
            targetBlock.setData(targetData);
            return;
        }

        bPlaceOnInteractable.removeOneItemFromHand(player);
        if (!bBlockType.isClickable(clickedBlock.getType()))
            slPlaceBetween.swingArm(player);
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

    @EventHandler
    public void onRailPlace(BlockPlaceEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        boolean rail = bMain.getPluginConfig().getBoolean("better-placements.orientable-rails.enable", true);
        boolean sneak = bMain.getPluginConfig().getBoolean("better-placements.orientable-rails.need-sneaking", false);

        if (!enable || !rail)
            return;

        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (!isRail(block))
            return;

        if (!player.isSneaking() && sneak)
            return;

        if (event.isCancelled())
            return;

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterplacements.rails"))
            return;

        byte orientation = hasAdjacentRail(block, player.getLocation().getYaw());

        if (orientation != -1) {
            block.setData(orientation);
            block.getState().update(true);
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
        if (block == null) return -1;

        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        Block blockAbove = block.getRelative(0, 1, 0);
        Block blockBelow = block.getRelative(0, -1, 0);

        for (int[] dir : directions) {
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
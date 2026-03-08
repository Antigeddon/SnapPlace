package me.antigeddon.snapplace.Place;

import me.antigeddon.snapplace.bDebug;
import me.antigeddon.snapplace.bMain;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class bPlaceOnInteractable implements Listener {

    private static final Set<Material> INTERACTIVE_BLOCKS = Collections.unmodifiableSet(EnumSet.of(
            Material.WORKBENCH,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.CHEST,
            Material.NOTE_BLOCK,
            Material.DISPENSER,
            Material.WOODEN_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.BED_BLOCK,
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.CAKE_BLOCK,
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.TRAP_DOOR
    ));

    @EventHandler(priority = Event.Priority.Highest, ignoreCancelled = true)
    public void onSneakInteract(PlayerInteractEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        boolean placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);
        boolean slEnable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);
        boolean sEnable = bMain.getPluginConfig().getBoolean("snow-layers.enable", true);
        boolean replace = bMain.getPluginConfig().getBoolean("snow-layers.replace-snow-with-itself", true);
        boolean pumpkin = bMain.getPluginConfig().getBoolean("better-placements.placeable-on-walls-and-roofs.enable", true);

        Player player = event.getPlayer();

        if (event.isCancelled()) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_EVENT_CANCELLED,
                    "Cancelled by " + event.getEventName());
            return;
        }

        if (!event.getAction().toString().contains("RIGHT_CLICK")) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_WRONG_ACTION,
                    "Action = " + event.getAction());
            return;
        }

        if (!enable || !placeOn) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_DISABLED,
                    "Enable = " + enable + ", PlaceOn = " + placeOn);
            return;
        }

        Block clicked = event.getClickedBlock();

        if (clicked == null) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_NO_BLOCK, "");
            return;
        }

        if (event.getBlockFace() == null) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_NO_FACE, "");
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterplacements.interactables")) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_NO_PERMISSION,
                    "Missing permission: SnapPlace.betterplacements.interactables");
            return;
        }

        BlockFace face = event.getBlockFace();
        Material type = clicked.getType();
        ItemStack inHand = player.getItemInHand();
        Block target = clicked.getRelative(face);
        Material targetType = target.getType();
        byte targetData = target.getData();
        Block underTarget = target.getRelative(BlockFace.DOWN);
        Material underType = underTarget.getType();

        if (inHand == null || inHand.getType() == Material.AIR) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_NO_ITEM,"InHandItemType = " + (inHand == null ? "null" : inHand.getType()));
            return;
        }

        Material itemType = inHand.getType();

        if (!player.isSneaking()) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_NO_SNEAK, "");
            return;
        }

        if (!INTERACTIVE_BLOCKS.contains(type)) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_NO_INTERACTIVE, "ClickedType = " + type);
            return;
        }

        if (!isPlaceableItem(itemType) || itemType == Material.PISTON_EXTENSION || itemType == Material.PISTON_MOVING_PIECE) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.INTERACT_INVALID_BLOCK, "ItemType = " + itemType);
            return;
        }

        Material placedBlock;
        byte data;

        if (itemType == Material.SIGN) {
            placedBlock = (face == BlockFace.UP) ? Material.SIGN_POST : Material.WALL_SIGN;
            bDebug.debug(player, bDebug.DebugType.INTERACT_SIGN_CONVERT, "PlacedBlock = " + placedBlock);

        } else {
            placedBlock = convertItemToBlock(itemType);
            bDebug.debug(player, bDebug.DebugType.INTERACT_ITEM_CONVERT, "Item = " + itemType + ", Block = " + placedBlock);
        }

        if (bBlockType.isFragileInteractableButSigns(type)) {
            if (bBlockType.isNotDoorAndBedCompatible(placedBlock) && itemType != Material.SIGN) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_INCOMPATIBLE_DOOR_BED_BUT_SIGN, "ClickedBlock = " + type + ", PlacedBlock = " + placedBlock);
                return;
            }
        }

        if (bBlockType.isVeryFragileInteractable(type)) {
            if (bBlockType.isNotDoorAndBedCompatible(placedBlock) || itemType == Material.SIGN) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_INCOMPATIBLE_DOOR_BED, "ClickedBlock = " + type + ", PlacedBlock = " + placedBlock);
                return;
            }
        }

        if (inHand.getData() != null && inHand.getData().getData() > 0) {
            data = inHand.getData().getData();

        } else {
            data = getDirectionalData(itemType, placedBlock, face, player.getLocation().getYaw(), player.getLocation().getPitch(), target, player);
            if (data == -1) {
                data = getPlacementData(itemType, placedBlock, face, clicked, type);
            }
        }

        bDebug.debug(player, bDebug.DebugType.INTERACT_ITEM_DATA, "ItemData = " + data);

        if (placedBlock != Material.PAINTING) {
            if (!bBlockType.isFluid(targetType)) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_TARGET_NOT_FLUID, "TargetBlock = " + targetType);
                return;
            }
        }

        if (target.getType() == placedBlock) {
            if (bBlockType.isFluid(placedBlock)) {
                byte targetFluidData = target.getData();

                if (targetFluidData == data) {
                    bDebug.debug(player, bDebug.DebugType.INTERACT_SAME_FLUID, "Data = " + data);
                    event.setCancelled(true);
                    return;
                }

                if (placedBlock == Material.SNOW) {
                    if (!sEnable && replace) {
                        // Continue

                    } else {
                        event.setCancelled(true);
                        bDebug.debug(player, bDebug.DebugType.INTERACT_SNOW_REPLACE, "");
                        return;
                    }
                }

            } else if (target.getData() == data) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_SAME_DATA, "Data = " + data);
                return;
            }
        }

        if (slEnable) {
            if (placedBlock == Material.STEP || placedBlock == Material.DOUBLE_STEP) {
                if (underType == Material.STEP && underType.getData() != itemType.getData()) {
                    event.setCancelled(true);
                    bDebug.debug(player, bDebug.DebugType.INTERACT_SLAB, "UnderBlockType = " + underType + ", UnderData = " + underType.getData());
                    return;
                }
            }
        }

        if ((placedBlock == Material.RED_ROSE || placedBlock == Material.YELLOW_FLOWER || placedBlock == Material.LONG_GRASS || placedBlock == Material.SAPLING)) {
            if (!bBlockType.isGroundPlant(underType)) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_FLOWER, "UnderBlockType = " + underType);
                return;
            }
        }

        if (placedBlock == Material.SUGAR_CANE_BLOCK) {
            if (!bBlockType.isGroundPlant(underType) && underType != Material.SOIL && underType != Material.SUGAR_CANE_BLOCK) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_SUGAR, "UnderBlockType = " + underType);
                return;
            }

            if (underType != Material.SUGAR_CANE_BLOCK && !isWaterAdjacent(underTarget)) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_SUGAR_WATER, "");
                return;
            }
        }

        if (placedBlock == Material.CROPS) {
            if (underType != Material.SOIL) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_CROPS, "UnderBlockType = " + underType);
                return;
            }
        }

        if (placedBlock == Material.CHEST) {
            if (wouldCreateIllegalChest(target)) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_CHEST, "");
                return;
            }
        }

        if (placedBlock == Material.CACTUS) {
            Material[] adjacentTypes = new Material[] {
                    target.getRelative(BlockFace.NORTH).getType(),
                    target.getRelative(BlockFace.SOUTH).getType(),
                    target.getRelative(BlockFace.EAST).getType(),
                    target.getRelative(BlockFace.WEST).getType()
            };

            for (Material mat : adjacentTypes) {
                if (bBlockType.isBuildableNotCactusFriendly(mat)) {
                    event.setCancelled(true);
                    bDebug.debug(player, bDebug.DebugType.INTERACT_CACTUS, "AdjacentTypes = " + mat);
                    return;
                }
            }

            if (underType != Material.SAND && underType != Material.CACTUS) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_CACTUS_GROUND, "UnderBlockType = " + underType);
                return;
            }
        }

        if (placedBlock == Material.DEAD_BUSH) {
            if (underType != Material.SAND) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_BUSH, "UnderBlockType = " + underType);
                return;
            }
        }

        if (placedBlock == Material.BROWN_MUSHROOM || placedBlock == Material.RED_MUSHROOM) {
            if (!bBlockType.isGroundMushroomAndSnow(underType)) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_MUSHROOM, "UnderBlockType = " + underType);
                return;
            }

        } else if (placedBlock == Material.SNOW) {
            if (!bBlockType.isGroundMushroomAndSnow(underType) && underType != Material.DOUBLE_STEP) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_SNOW_GROUND, "UnderBlockType = " + underType);
                return;
            }
        }

        if (placedBlock == Material.CAKE_BLOCK) {
            if (!bBlockType.canSupportCake(underType)) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_CAKE, "UnderBlockType = " + underType);
                return;
            }
        }

        if (bBlockType.isTorches(placedBlock) && face == BlockFace.DOWN) {
            if (bBlockType.isNotSolid(underType) && underType != Material.FENCE) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_TORCH, "UnderBlockType = " + underType);
                return;
            }

        } else if (bBlockType.isFragileNeedGroundWhenWallPlaced(placedBlock, data)) {
            if (bBlockType.isNotSolid(underType) &&
                    !((placedBlock == Material.PUMPKIN || placedBlock == Material.JACK_O_LANTERN || placedBlock == Material.FENCE)
                            && (player.hasPermission("SnapPlace.betterplacements.wallandroof") || player.isOp())
                            && pumpkin))
            {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_PUMPKIN, "UnderBlockType = " + underType);
                return;
            }
        }


        if (itemType == Material.BUCKET) {
            if (bBlockType.isFluid(targetType) && targetType != Material.SNOW && targetType != Material.AIR && targetType != Material.FIRE && target.getData() == 0) {

                Material bucketType = (targetType == Material.WATER || targetType == Material.STATIONARY_WATER)
                        ? Material.WATER_BUCKET
                        : Material.LAVA_BUCKET;

                PlayerBucketFillEvent fillEvent = new PlayerBucketFillEvent(
                        player,
                        target,
                        face,
                        bucketType,
                        player.getItemInHand()
                );

                org.bukkit.Bukkit.getPluginManager().callEvent(fillEvent);

                if (fillEvent.isCancelled()) {
                    event.setCancelled(true);
                    bDebug.debug(player, bDebug.DebugType.INTERACT_FILL_BUCKET_CANCELLED, "");
                    return;
                }

                event.setCancelled(true);
                target.setType(Material.AIR);
                player.setItemInHand(new ItemStack(bucketType, 1));
                bDebug.debug(player, bDebug.DebugType.INTERACT_FILL_BUCKET_SUCCESS, "ItemType = " + bucketType);

            }

            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.INTERACT_FILL_BUCKET_INVALID_TARGET, "TargetBlock = " + targetType);
            return;
        }

        if (itemType == Material.WATER_BUCKET || itemType == Material.LAVA_BUCKET) {
            PlayerBucketEmptyEvent emptyEvent = new PlayerBucketEmptyEvent(
                    player,
                    target,
                    face,
                    itemType,
                    inHand
            );

            org.bukkit.Bukkit.getPluginManager().callEvent(emptyEvent);

            if (emptyEvent.isCancelled()) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_UNFILL_CANCELLED, "");
                return;
            }
        }

        if (itemType == Material.BED) {

            if (face != BlockFace.UP) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_BED_TOP_FACE, "Face = " + face);
                return;
            }

            byte baseData = getDirectionalData(itemType, placedBlock, face, player.getLocation().getYaw(), player.getLocation().getPitch(), target, player);

            if (baseData == -1) baseData = 0;

            Block otherPart = null;

            switch (baseData) {
                case 0:
                    otherPart = target.getRelative(BlockFace.WEST);
                    break;
                case 1:
                    otherPart = target.getRelative(BlockFace.NORTH);
                    break;
                case 2:
                    otherPart = target.getRelative(BlockFace.EAST);
                    break;
                case 3:
                    otherPart = target.getRelative(BlockFace.SOUTH);
                    break;
            }

            if (otherPart == null || !bBlockType.isFluid(otherPart.getType())) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_BED_SECOND_BLOCK,
                        "SecondTargetType = " + (otherPart == null ? "null" : otherPart.getType()));
                return;
            }

            Block blockBelowOtherPart = otherPart.getRelative(BlockFace.DOWN);

            if (bBlockType.isNotSolid(blockBelowOtherPart.getType())) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_BED_SECOND_UNDER_BLOCK, "SecondUnderTargetType " + blockBelowOtherPart.getType());
                return;
            }
            event.setCancelled(true);
            BlockState targetBlockState = target.getState();
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                    target,
                    targetBlockState,
                    clicked,
                    inHand,
                    player,
                    true);

            target.getWorld().getBlockAt(target.getX(), target.getY(), target.getZ()).setTypeIdAndData(placedBlock.getId(), baseData, true);
            otherPart.getWorld().getBlockAt(otherPart.getX(), otherPart.getY(), otherPart.getZ()).setTypeIdAndData(placedBlock.getId(), (byte) (baseData | 0x8), true);

            org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);

            if (placeEvent.isCancelled()) {
                target.setType(targetType);
                target.setData(targetData);
                otherPart.setType(targetType);
                otherPart.setData(targetData);
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_BED_PLACE_CANCELLED, "");
                return;
            }

            removeOneItemFromHand(player);
            bDebug.debug(player, bDebug.DebugType.INTERACT_BED_SUCCESS, "TargetBlock " + target.getType() + ", TargetData = " + target.getData()
                    + ", SecondTargetBlock = " + otherPart.getType() + ", SecondTargetData = " + otherPart.getData());
            return;
        }

        if (itemType == Material.WOOD_DOOR || itemType == Material.IRON_DOOR) {
            Block top = target.getRelative(BlockFace.UP);

            if (face != BlockFace.UP) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_DOOR_FACE, "Face = " + face);
                return;
            }

            if (!bBlockType.isFluid(top.getType())) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_DOOR_SECOND_PART_NOT_FLUID, "AboveTargetBlock = " + top.getType());
                return;
            }

            byte baseData = getDirectionalData(itemType, placedBlock, face, player.getLocation().getYaw(), player.getLocation().getPitch(), target, player);

            if (baseData == -1) baseData = 0;
            event.setCancelled(true);

            BlockState targetBlockState = target.getState();
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                    target,
                    targetBlockState,
                    clicked,
                    inHand,
                    player,
                    true);

            target.getWorld().getBlockAt(target.getX(), target.getY(), target.getZ()).setTypeIdAndData(placedBlock.getId(), baseData, true);
            top.getWorld().getBlockAt(top.getX(), top.getY(), top.getZ()).setTypeIdAndData(placedBlock.getId(),  (byte) (baseData | 0x8), true);

            org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);

            if (placeEvent.isCancelled()) {
                target.setType(targetType);
                target.setData(targetData);
                top.setType(targetType);
                top.setData(targetData);
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_DOOR_PLACE_CANCELLED, "");
                return;
            }

            removeOneItemFromHand(player);
            bDebug.debug(player, bDebug.DebugType.INTERACT_DOOR_SUCCESS, "TargetBlock " + target.getType() + ", TargetData = " + target.getData()
                    + ", SecondTargetBlock = " + top.getType() + ", SecondTargetData = " + top.getData());
            return;
        }

        if (itemType == Material.PAINTING) {

            if (face == BlockFace.UP || face == BlockFace.DOWN) {
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_PAINTING_FACE, "Face = " + face);
                return;
            }

            event.setCancelled(true);

            if (spawnPainting(clicked.getLocation(), data, player, clicked, face)) {
                removeOneItemFromHand(player);
            }

            bDebug.debug(player, bDebug.DebugType.INTERACT_PAINTING_SUCCESS, "BlockLocation = " + clicked.getLocation());
            return;
        }

        if (itemType == Material.FLINT_AND_STEEL) {

            event.setCancelled(true);
            BlockIgniteEvent igniteEvent = new BlockIgniteEvent(
                    target,
                    BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL,
                    player);

            BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                    target,
                    target.getState(),
                    clicked,
                    inHand,
                    player,
                    true);

            target.setTypeIdAndData(placedBlock.getId(), data, true);

            org.bukkit.Bukkit.getPluginManager().callEvent(igniteEvent);
            if (igniteEvent.isCancelled()) {
                target.setType(targetType);
                target.setData(targetData);
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_IGNITE_CANCELLED, "");
                return;
            }
            org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);
            if (placeEvent.isCancelled()) {
                target.setType(targetType);
                target.setData(targetData);
                event.setCancelled(true);
                bDebug.debug(player, bDebug.DebugType.INTERACT_FLINT_PLACE_CANCELLED, "");
                return;
            }

            removeOneItemFromHand(player);
            bDebug.debug(player, bDebug.DebugType.INTERACT_FLINT_SUCCESS, "PlacedBlock = " + target.getType() + ", PlacedData = " + target.getData());
            return;
        }

        if (bBlockType.isEntityBlockingBlock(target.getLocation(), player, placedBlock, data)) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.INTERACT_ENTITY_BLOCKING, "");
            return;
        }

        if (data == -1) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.INTERACT_INVALID, "Data = " + data);
            return;
        }

        event.setCancelled(true);

        Block block = target.getWorld().getBlockAt(target.getX(), target.getY(), target.getZ());

        BlockState targetBlockState = target.getState();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                target,
                targetBlockState,
                clicked,
                inHand,
                player,
                true);

        if (bBlockType.isRail(placedBlock)) {
            block.setType(placedBlock);
            block.getState().update(true);

            if (data != -2) {
                block.setData(data);
                block.getState().update(true);
            }

        } else if (placedBlock == Material.FURNACE || placedBlock == Material.BURNING_FURNACE || placedBlock == Material.DISPENSER || placedBlock == Material.TORCH) {
            block.setTypeIdAndData(placedBlock.getId(), data, true);
            block.setData(data);
            block.getState().update(true);

        } else {
            block.setTypeIdAndData(placedBlock.getId(), data, true);
        }

        org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            block.setType(targetType);
            block.setData(targetData);
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.INTERACT_PLACE_CANCELLED, "");
            return;
        }

        if (itemType == Material.WATER_BUCKET || itemType == Material.LAVA_BUCKET) {
            block.setType(itemType == Material.WATER_BUCKET ? Material.WATER : Material.LAVA);
            block.setData((byte) 0);
        }

        org.bukkit.World bukkitWorld = block.getWorld();

        if (bukkitWorld.getEnvironment() == org.bukkit.World.Environment.NETHER && itemType == Material.WATER_BUCKET)
            block.setType(Material.AIR);

        if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)
            triggerEmptySignEvent(block, player);

        removeOneItemFromHand(player);
        bDebug.debug(player, bDebug.DebugType.INTERACT_SUCCESS, "PlacedBlock = " + block.getType() + ", PlacedData = " + block.getData());
    }

    private byte getPlacementData(Material itemType, Material type, BlockFace face, Block clickedBlock, Material clickedType) {
        Block blockUnderClicked = clickedBlock.getRelative(BlockFace.DOWN);

        switch (type) {
            case TORCH:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
                if (bBlockType.isInteractableBlackList(clickedType)) {
                    if (face != BlockFace.UP) {
                        return 5;

                    } else {
                        return -1;
                    }
                }
                switch (face) {
                    case NORTH: return 2;
                    case SOUTH: return 1;
                    case EAST:  return 4;
                    case WEST:  return 3;
                    case UP:
                    case DOWN:
                        return 5;
                    default:
                        return -1;
                }

            case CACTUS:
                if (face == BlockFace.UP) {
                    return -1;

                } else {
                    return 0;
                }

            case SUGAR_CANE_BLOCK:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case DEAD_BUSH:
            case CROPS:
                switch (face) {
                    case NORTH:
                    case SOUTH:
                    case EAST:
                    case WEST:
                    case DOWN:
                        return 0;
                        default:
                            return -1;
                }

            case STONE_BUTTON:
                switch (face) {
                    case NORTH: return 2;
                    case SOUTH: return 1;
                    case EAST:  return 4;
                    case WEST:  return 3;
                    default:
                        return -1;
                }

            case TRAP_DOOR:
                switch (face) {
                    case NORTH: return 2;
                    case SOUTH: return 3;
                    case EAST:  return 0;
                    case WEST:  return 1;
                    default:
                        return -1;
                }

            case LADDER:
                if (face == BlockFace.DOWN) {
                    if (!bBlockType.isNotSolid(blockUnderClicked.getRelative(BlockFace.WEST).getType()))  return 2;
                    if (!bBlockType.isNotSolid(blockUnderClicked.getRelative(BlockFace.EAST).getType()))  return 3;
                    if (!bBlockType.isNotSolid(blockUnderClicked.getRelative(BlockFace.SOUTH).getType())) return 4;
                    if (!bBlockType.isNotSolid(blockUnderClicked.getRelative(BlockFace.NORTH).getType())) return 5;
                    return -1;
                }

                if (bBlockType.isInteractableBlackList(clickedType))
                    return -1;

                switch (face) {
                    case NORTH: return 4;
                    case SOUTH: return 5;
                    case EAST:  return 2;
                    case WEST:  return 3;
                    default:
                        return -1;
                }

            case SAPLING:
            case LONG_GRASS:
                if (face == BlockFace.UP) {
                    return -1;
                }

            case WALL_SIGN:
                if (itemType == Material.SIGN) {
                    switch (face) {
                        case NORTH: return 4;
                        case SOUTH: return 5;
                        case WEST:  return 3;
                        case EAST:  return 2;
                        default:    return -1;
                    }
                }
                return 0;

            case LEVER:
                switch (face) {
                    case NORTH: return 2;
                    case SOUTH: return 1;
                    case WEST:  return 3;
                    case EAST:  return 4;
                    default:    return -1;
                }

            case PAINTING:
                switch (face) {
                    case NORTH: return 1;
                    case SOUTH: return 3;
                    case WEST:  return 2;
                    case EAST:  return 0;
                    default:
                        return -1;
                }

            default:
                return 0;
        }
    }

    private boolean isWaterAdjacent(Block block) {
        Material type1 = block.getRelative(1, 0, 0).getType();
        Material type2 = block.getRelative(-1, 0, 0).getType();
        Material type3 = block.getRelative(0, 0, 1).getType();
        Material type4 = block.getRelative(0, 0, -1).getType();

        return isWater(type1) || isWater(type2) || isWater(type3) || isWater(type4);
    }

    private boolean isWater(Material material) {
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }

    private byte getDirectionalData(Material itemType, Material type, BlockFace face, float yaw, float pitch, Block block, Player player) {
        boolean rail = bMain.getPluginConfig().getBoolean("better-placements.orientable-rails.enable",true);

        int direction = Math.round(yaw / 90f) & 3;

        if (type == Material.LEVER) {
            if (Objects.requireNonNull(face) == BlockFace.UP) {
                return (direction % 2 == 0) ? (byte) 5 : 6;
            }
        }

        if (bBlockType.isRail(type)) {

            if (!rail || (!player.hasPermission("SnapPlace.betterplacements.rails") && !player.isOp())) {
                bDebug.debug(player, bDebug.DebugType.INTERACT_RAIL, "BlockData = 0");
                return -2;
            }

            byte orientation = bPlace.hasAdjacentRail(block, yaw);
            if (orientation == -1) {
                return -2;

            } else {
                return orientation;
            }
        }

        if (type == Material.DIODE_BLOCK_OFF || type == Material.DIODE_BLOCK_ON) {
            switch (direction) {
                case 0: return 2;
                case 1: return 3;
                case 2: return 0;
                case 3: return 1;
            }
        }

        if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK) {
            switch (direction) {
                case 0: return 1;
                case 1: return 2;
                case 2: return 3;
                case 3: return 0;
            }
        }

        if (type == Material.COBBLESTONE_STAIRS || type == Material.WOOD_STAIRS) {
            switch (direction) {
                case 0: return 2;
                case 1: return 1;
                case 2: return 3;
                case 3: return 0;
            }
        }

        if (type == Material.BED_BLOCK) {
            switch (direction) {
                case 0: return 0;
                case 1: return 1;
                case 2: return 2;
                case 3: return 3;
            }
        }

        if (type == Material.PUMPKIN || type == Material.JACK_O_LANTERN) {
            switch (direction) {
                case 0: return 2;
                case 1: return 3;
                case 2: return 0;
                case 3: return 1;
            }
        }

        if (type == Material.FURNACE || type == Material.BURNING_FURNACE || type == Material.DISPENSER) {
                switch (direction) {
                    case 0: return 2;
                    case 1: return 5;
                    case 2: return 3;
                    case 3: return 4;
                }
            }

        if (type == Material.PISTON_BASE || type == Material.PISTON_STICKY_BASE)
            return getPistonDirection(yaw, pitch);

        if (itemType == Material.SIGN) {
            if (type == Material.SIGN_POST) {
                yaw = (yaw % 360 + 360) % 360;
                yaw = (yaw + 180) % 360;
                return (byte) (Math.round(yaw / 22.5f) & 15);
            }
        }

        if (itemType == Material.BED) {
            if (face != BlockFace.UP) return -1;

            switch (direction) {
                case 0: return 0;
                case 1: return 1;
                case 2: return 2;
                case 3: return 3;
            }
        }

        if (itemType == Material.WOOD_DOOR || itemType == Material.IRON_DOOR) {
            if (face != BlockFace.UP)
                return -1;

            switch (direction) {
                case 0: return 1;
                case 1: return 2;
                case 2: return 3;
                case 3: return 0;
            }
        }

        return -1;
    }

    private byte getPistonDirection(float yaw, float pitch) {
        if (pitch <= -45) {
            return 0;
        }

        if (pitch >= 45) {
            return 1;
        }

        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 45 && yaw < 135) {
            return 5;
        } else if (yaw >= 135 && yaw < 225) {
            return 3;
        } else if (yaw >= 225 && yaw < 315) {
            return 4;
        } else {
            return 2;
        }
    }

    private Material convertItemToBlock(Material itemType) {
        switch (itemType) {
            case REDSTONE: return Material.REDSTONE_WIRE;
            case DIODE: return Material.DIODE_BLOCK_OFF;
            case CAKE: return Material.CAKE_BLOCK;
            case WATER_BUCKET: return Material.STATIONARY_WATER;
            case LAVA_BUCKET: return Material.STATIONARY_LAVA;
            case FLINT_AND_STEEL: return Material.FIRE;
            case SUGAR_CANE: return Material.SUGAR_CANE_BLOCK;
            case WOOD_DOOR: return Material.WOODEN_DOOR;
            case IRON_DOOR: return Material.IRON_DOOR_BLOCK;
            case BED: return Material.BED_BLOCK;
            default: return itemType;
        }
    }

    private boolean isPlaceableItem(Material material) {
        if (material.isBlock()) return true;

        switch (material) {
            case REDSTONE:
            case DIODE:
            case CAKE:
            case WOOD_DOOR:
            case IRON_DOOR:
            case SIGN:
            case BED:
            case WATER_BUCKET:
            case LAVA_BUCKET:
            case PAINTING:
            case BUCKET:
            case FLINT_AND_STEEL:
            case SUGAR_CANE:
                return true;
            default:
                return false;
        }
    }

    public boolean wouldCreateIllegalChest(Block target) {
        BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        List<Block> adjacentChests = new ArrayList<>();

        for (BlockFace dir : directions) {
            Block adjacent = target.getRelative(dir);

            if (adjacent.getType() == Material.CHEST) {
                adjacentChests.add(adjacent);
            }
        }

        if (adjacentChests.size() >= 2)
            return true;

        if (adjacentChests.isEmpty())
            return false;

        Block chestToCheck = adjacentChests.get(0);

        for (BlockFace dir : directions) {
            Block nearby = chestToCheck.getRelative(dir);

            if (nearby.equals(target))
                continue;
            if (nearby.getType() == Material.CHEST)
                return true;
        }

        return false;
    }


    public static void removeOneItemFromHand(Player player) {

        ItemStack inHand = player.getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            bDebug.debug(player, bDebug.DebugType.INTERACT_EMPTY_HAND_FATAL, "ItemType = null or AIR");
            return;
        }

        Object data = inHand.getData();
        String dataInfo;
        try {
            dataInfo = (data != null) ? data.toString() : "null";
        } catch (Exception e) {
            dataInfo = "invalid";
        }

        Material type = inHand.getType();

        if (type == Material.WATER_BUCKET || type == Material.LAVA_BUCKET) {
            player.setItemInHand(new ItemStack(Material.BUCKET, 1));
            bDebug.debug(player, bDebug.DebugType.INTERACT_EMPTY_BUCKET_HAND, "ItemType = " + inHand.getType());
            return;
        }

        if (type == Material.FLINT_AND_STEEL) {
            short durability = inHand.getDurability();
            durability++;
            int maxDurability = 65;

            if (durability >= maxDurability) {

                int amount = inHand.getAmount();
                if (amount > 1) {
                    inHand.setAmount(amount - 1);
                    inHand.setDurability((short)0);
                    bDebug.debug(player, bDebug.DebugType.INTERACT_FLINT_MINUS, "ItemType = " + inHand.getType() + ", ItemData = " + dataInfo);

                } else {
                    player.getInventory().setItemInHand(null);
                    bDebug.debug(player, bDebug.DebugType.INTERACT_FLINT_BREAK, "");
                }

            } else {
                inHand.setDurability(durability);
            }

            return;
        }

        int newAmount = inHand.getAmount() - 1;

        if (newAmount <= 0) {
            player.setItemInHand(null);
            bDebug.debug(player, bDebug.DebugType.INTERACT_HAND_ZERO, "");

        } else {
            inHand.setAmount(newAmount);
            player.setItemInHand(inHand);
            bDebug.debug(player, bDebug.DebugType.INTERACT_HAND_MINUS,
                    "ItemType = " + inHand.getType() +
                            ", ItemData = " + dataInfo +
                            ", ItemAmount = " + inHand.getAmount());
        }
    }

    public static boolean spawnPainting(Location loc, byte direction, Player player, Block clicked, BlockFace face) {

        if (direction == -1)
            return false;

        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        EntityPainting painting = new EntityPainting(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), direction);

        if (!painting.h())
            return false;

        PaintingPlaceEvent placeEvent = new PaintingPlaceEvent(
                (org.bukkit.entity.Painting) painting.getBukkitEntity(),
                player,
                clicked,
                face
        );

        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            return false;
        }

        world.addEntity(painting);
        return true;
    }

    public static void triggerEmptySignEvent(Block placedBlock, Player player) {
        if (placedBlock == null || player == null)
            return;

        Material type = placedBlock.getType();
        if (type != Material.SIGN_POST && type != Material.WALL_SIGN)
            return;

        String[] emptyLines = {"", "", "", ""};
        SignChangeEvent signEvent = new SignChangeEvent(placedBlock, player, emptyLines.clone());
        Bukkit.getPluginManager().callEvent(signEvent);
    }
}
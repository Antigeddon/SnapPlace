package me.antigeddon.snapplace.Slab;

import me.antigeddon.snapplace.Place.bBlockType;
import me.antigeddon.snapplace.Place.bPlaceOnInteractable;
import me.antigeddon.snapplace.bDebug;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;

public class slPlaceBetween implements Listener {
    private final boolean enable;
    private final boolean between;
    private final boolean sneak;
    private final boolean bEnable;
    private final boolean placeOn;

    public slPlaceBetween() {
        this.enable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);
        this.between = bMain.getPluginConfig().getBoolean("better-slabs.place-between.enable", true);
        this.sneak = bMain.getPluginConfig().getBoolean("better-slabs.place-between.need-sneaking", true);
        this.bEnable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        this.placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);
    }

    @EventHandler(priority = Event.Priority.High, ignoreCancelled = true)
    public void onSlabRightClick(PlayerInteractEvent event) {

        if (event.isCancelled()) {
            bDebug.debug(event.getPlayer(), bDebug.DebugType.SLAB_BETWEEN_EVENT_CANCELLED,
                    "Cancelled by " + event.getEventName());
            return;
        }

        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (!event.getAction().toString().contains("RIGHT_CLICK")) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_WRONG_ACTION, "Action = " + event.getAction());
            return;
        }

        if (!enable || !between) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_DISABLED, "Enable = " + enable + ", Between = " + between);
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterslabs")) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_NO_PERMISSION, "");
            return;
        }

        if (clicked == null) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_CLICKED_NULL, "");
            return;
        }

        if (clicked.getType() == Material.SNOW) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_CLICKED_SNOW, "");
            return;
        }

        if (!player.isSneaking() && sneak)
            if (!(event.getBlockFace() == BlockFace.UP && bBlockType.isFluid(clicked.getRelative(BlockFace.UP).getType()))) {
                bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_NO_SNEAK, "");
                return;
            }

        ItemStack inHand = player.getItemInHand();

        if (inHand == null || inHand.getType() != Material.STEP) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_NOT_HOLDING_SLAB, "ItemType = " + (inHand == null ? "null" : inHand.getType()));
            return;
        }

        BlockFace face = event.getBlockFace();
        Block target = clicked.getRelative(face);

        if ((face == BlockFace.UP && target.getY() <= clicked.getY()) ||
                (face == BlockFace.DOWN && target.getY() >= clicked.getY())) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_BLOCK_TARGET_OUTSIDE, "");
            return;
        }

        Material targetType = target.getType();
        Block slab = null;

        if (clicked.getType() == Material.STEP && face == BlockFace.UP) {
            slab = clicked;

        } else if (targetType == Material.STEP) {
            slab = target;
        }

        if (slab == null) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_SLAB_NULL, "");
            return;
        }

        if (slab.getY() == 127 && !bBlockType.isBuildableAt127(Material.DOUBLE_STEP)) {
            event.setCancelled(true);
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_BLOCK_Y127, "ItemType = " + inHand.getType());
            return;
        }

        if (bBlockType.isClickable(clicked.getType())) {
            if (!player.isSneaking() || !bEnable || !placeOn) {
                bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_CLICKED_INTERACTABLE_NO_SNEAK, "Sneaking = " + player.isSneaking() + ", bEnable = " + bEnable + ", placeOn = " + placeOn);
                return;
            }

            if (!player.isOp() &&
                    !player.hasPermission("SnapPlace.betterplacements.interactables")) {
                bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_NO_PERMISSION_INTERACT, "");
                return;
            }
        }

        MaterialData slabdata = slab.getState().getData();
        MaterialData inHandData = inHand.getData();

        if (!slabdata.getClass().equals(inHandData.getClass()) || slabdata.getData() != inHandData.getData()) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_DATA_MISMATCH, "HandData = " + inHandData.getData() + ", TargetSlabData = " + slabdata.getData());
            return;
        }

        if (bBlockType.isEntityBlockingBlock(slab.getLocation(), player, Material.DOUBLE_STEP, inHandData.getData())) {
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_ENTITY_BLOCKING, "");
            return;
        }

        event.setCancelled(true);

        BlockState slabBlockState = slab.getState();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                slab,
                slabBlockState,
                clicked,
                inHand,
                player,
                true);

        boolean isCancelled = bBlockType.placeEventPlacingSimulation(
                slab,
                Material.DOUBLE_STEP,
                inHandData.getData(),
                placeEvent);

        if (isCancelled) {
            slPillarFix.PillarState state = slPillarFix.scan(player, slab);
            slPillarFix.restoreBlocks1(player, state, true);
            player.sendBlockChange(slab.getLocation(), Material.STEP, inHandData.getData());
            slPillarFix.restoreBlocks2(player, state, true);
            bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_PLACE_CANCELLED, "");
            return;
        }

        slPillarFix.PillarState state = slPillarFix.scan(player, slab);

        slPillarFix.setBlockNMS(slab, 0, (byte) 0, true);

        slPillarFix.restoreBlocks1(player, state, false);
        slab.getWorld().getBlockAt(slab.getX(), slab.getY(), slab.getZ()).setTypeIdAndData(43, inHandData.getData(), true);
        slPillarFix.restoreBlocks2(player, state, false);

        for (Player p : player.getServer().getOnlinePlayers())
            p.sendBlockChange(slab.getLocation(), Material.DOUBLE_STEP, inHandData.getData());

        bPlaceOnInteractable.removeOneItemFromHand(player);
        bDebug.debug(player, bDebug.DebugType.SLAB_BETWEEN_SUCCESS, "SlabType = " + slab.getType() + ", SlabData = " + slab.getData());


        Material aboveType = Material.AIR;
        boolean canSwing = true;

        // The animation is blocked client side at 126 too
        if (slab.getY() < 126) {
            canSwing = false;
            Block aboveBlock = slab.getRelative(BlockFace.UP);
            aboveType = aboveBlock.getType();

            // Stone is just a dummy to trigger 1x1x1
            if (bBlockType.isEntityBlockingBlock(aboveBlock.getLocation(), player, Material.STONE, (byte) 0)) {
                canSwing = true;
            }
        }


        if (!bBlockType.isFluid(aboveType) || canSwing || slab == target)
            if (!bBlockType.isClickable(clicked.getType()))
                swingArm(player);
    }

    public static void swingArm(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Packet18ArmAnimation packet = new Packet18ArmAnimation(entityPlayer, 1);
        entityPlayer.netServerHandler.sendPacket(packet);
    }
}
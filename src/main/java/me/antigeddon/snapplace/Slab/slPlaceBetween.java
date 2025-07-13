package me.antigeddon.snapplace.Slab;

import me.antigeddon.snapplace.Place.bBlockType;
import me.antigeddon.snapplace.Place.bPlaceOnInteractable;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
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

    @EventHandler
    public void onSlabRightClick(PlayerInteractEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("better-slabs.enable", true);
        boolean between = bMain.getPluginConfig().getBoolean("better-slabs.place-between.enable", true);
        boolean sneak = bMain.getPluginConfig().getBoolean("better-slabs.place-between.need-sneaking", true);
        boolean bEnable = bMain.getPluginConfig().getBoolean("better-placements.enable", true);
        boolean placeOn = bMain.getPluginConfig().getBoolean("better-placements.place-on-interactables", true);

        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (!event.getAction().toString().contains("RIGHT_CLICK"))
            return;

        if (!enable || !between)
            return;

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.betterslabs"))
            return;

        if (clicked == null)
            return;

        if (clicked.getType() == Material.SNOW)
            return;

        if (!player.isSneaking() && sneak)
            if (!(event.getBlockFace() == BlockFace.UP && bBlockType.isFluid(clicked.getRelative(BlockFace.UP).getType())))
                return;

        BlockFace face = event.getBlockFace();
        Block target = clicked.getRelative(face);
        Material targetType = target.getType();
        ItemStack inHand = player.getItemInHand();

        Block slab = null;

        if (clicked.getType() == Material.STEP && face == BlockFace.UP) {
            slab = clicked;

        } else if (targetType == Material.STEP) {
            slab = target;
        }

        if (slab == null)
            return;

        if (bBlockType.isClickable(clicked.getType())) {
            if (!player.isSneaking() || !bEnable || !placeOn)
                return;

            if (!player.isOp() &&
                    !player.hasPermission("SnapPlace.betterplacements.interactables"))
                return;
        }

        if (inHand == null || inHand.getType() != Material.STEP)
            return;

        MaterialData slabdata = slab.getState().getData();
        MaterialData inHandData = inHand.getData();

        if (!slabdata.getClass().equals(inHandData.getClass()) || slabdata.getData() != inHandData.getData())
            return;

        if (bBlockType.isEntityBlockingBlock(slab.getLocation(), player, Material.DOUBLE_STEP, inHandData.getData()))
            return;

        event.setCancelled(true);

        BlockState slabBlockState = slab.getState();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                slab,
                slabBlockState,
                clicked,
                inHand,
                player,
                true);

        slPillarFix.checkAndStoreStepLoop(player, slab);

        slab.setType(Material.AIR);
        slPillarFix.restoreBlocks1(player);
        slab.getWorld().getBlockAt(slab.getX(), slab.getY(), slab.getZ()).setTypeIdAndData(43, inHandData.getData(), true);
        slPillarFix.restoreBlocks2(player);

        for (Player p : player.getServer().getOnlinePlayers())
            p.sendBlockChange(slab.getLocation(), Material.DOUBLE_STEP, inHandData.getData());
        org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            slPillarFix.checkAndStoreStepLoop(player, slab);
            slab.setType(Material.AIR);
            slPillarFix.restoreBlocks1(player);
            slab.getWorld().getBlockAt(target.getX(), slab.getY(), slab.getZ()).setTypeIdAndData(44, inHandData.getData(), false);
            player.sendBlockChange(slab.getLocation(), 44, inHandData.getData());
            slPillarFix.restoreBlocks2(player);
            return;
        }

        bPlaceOnInteractable.removeOneItemFromHand(player);

        Block above = slab.getRelative(BlockFace.UP);
        Material aboveType = above.getType();

        if (!bBlockType.isFluid(aboveType) || slab == target)
            if (!bBlockType.isClickable(clicked.getType()))
                swingArm(player);
    }

    public static void swingArm(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Packet18ArmAnimation packet = new Packet18ArmAnimation(entityPlayer, 1);
        entityPlayer.netServerHandler.sendPacket(packet);
    }
}
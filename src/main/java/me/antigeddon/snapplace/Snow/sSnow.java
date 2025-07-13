package me.antigeddon.snapplace.Snow;

import me.antigeddon.snapplace.Place.bPlaceOnInteractable;
import me.antigeddon.snapplace.Place.bBlockType;
import me.antigeddon.snapplace.bMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class sSnow implements Listener {

    @EventHandler
    public void onSnowLayerInteract(PlayerInteractEvent event) {
        boolean enable = bMain.getPluginConfig().getBoolean("snow-layers.enable", true);
        boolean replace = bMain.getPluginConfig().getBoolean("snow-layers.replace-snow-with-itself", true);
        boolean sneak = bMain.getPluginConfig().getBoolean("snow-layers.need-sneaking", false);

        if (!event.getAction().toString().contains("RIGHT_CLICK"))
            return;

        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        ItemStack inHand = player.getItemInHand();

        if (clicked == null)
            return;

        Material type = clicked.getType();
        byte data = clicked.getData();
        Block adjacent = clicked.getRelative(event.getBlockFace());

        if (inHand == null || inHand.getType() != Material.SNOW)
            return;

        if (adjacent.getType() == Material.SNOW || type == Material.SNOW) {
            if (!enable) {
                if (replace) {
                    return;
                }
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        }

        if (type != Material.SNOW)
            return;

        if (!player.isSneaking() && sneak) {
            event.setCancelled(true);
            return;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.snowlayers")) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlockFace() != BlockFace.UP) {
            event.setCancelled(true);
            return;
        }

        // Max Height
        if (data == 7 || data == 15) {
            event.setCancelled(true);
            return;
        }

        // HitBox
        if (data == 2 || (data == 10)) {
            if (bBlockType.isEntityBlockingBlock(clicked.getLocation(), player, Material.SNOW, data)) {
                event.setCancelled(true);
                return;
            }
        }

        event.setCancelled(true);

        byte newData = (byte) (data + 1);

        clicked.getWorld().getBlockAt(clicked.getX(), clicked.getY(), clicked.getZ()).setTypeIdAndData(78, newData, true);
        for (Player p : Bukkit.getOnlinePlayers())
            p.sendBlockChange(clicked.getLocation(), Material.SNOW, newData);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(clicked, clicked.getState(), clicked.getRelative(BlockFace.SELF), inHand, player, true);

        Bukkit.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            clicked.getWorld().getBlockAt(clicked.getX(), clicked.getY(), clicked.getZ()).setTypeIdAndData(type.getId(), data, false);
            event.setCancelled(true);
            return;
        }

        bPlaceOnInteractable.removeOneItemFromHand(player);

    }
}
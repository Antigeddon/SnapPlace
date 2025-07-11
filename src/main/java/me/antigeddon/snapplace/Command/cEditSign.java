package me.antigeddon.snapplace.Command;

import me.antigeddon.snapplace.bMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class cEditSign implements Listener, CommandExecutor {

    private final bMain plugin;
    private final Map<UUID, String[]> pendingSignEdits = new HashMap<>();
    private final Map<UUID, Integer> pendingTasks = new HashMap<>();

    public cEditSign(bMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean enabled = bMain.getPluginConfig().getBoolean("edit-sign.enable", true);
        int timeoutSeconds = bMain.getPluginConfig().getInt("edit-sign.command-timeout", 40);

        if (!(sender instanceof Player)) {
            sender.sendMessage("[SnapPlace] This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!enabled) {
            player.sendMessage(plugin.pName() + "§cSign editing is disabled.");
            return true;
        }

        if (!player.isOp() &&
                !player.hasPermission("SnapPlace.editsign")) {
            player.sendMessage(plugin.pName() + "§cYou don't have permission to do that.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("/editsign l1:(§7<Text>§f) l2:(§7<Text>§f) l3:(§7<Text>§f) l4:(§7<Text>§f).");
            return true;
        }

        List<String> rebuilt = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean inParens = false;

        for (String a : args) {
            if (!inParens) {
                if (a.contains("(") && !a.contains(")")) {
                    buffer.setLength(0);
                    buffer.append(a);
                    inParens = true;

                } else {
                    rebuilt.add(a);
                }

            } else {
                buffer.append(" ").append(a);
                if (a.contains(")")) {
                    rebuilt.add(buffer.toString());
                    inParens = false;
                }
            }
        }

        if (inParens) {
            player.sendMessage(plugin.pName() + "§cUnclosed parenthesis \"()\" in the command.");
            return true;
        }

        boolean hasPrefix = false;
        String[] lines = new String[4];

        for (int i = 0; i < 4; i++) lines[i] = "";

        for (String a : rebuilt) {
            String low = a.toLowerCase();

            if (low.matches("l[1-4]:.*")) {
                hasPrefix = true;
                int lineNum = a.charAt(1) - '1';

                String content;

                if (low.matches("l[1-4]:\\(.+\\)")) {
                    content = a.substring(a.indexOf('(') + 1, a.lastIndexOf(')'));

                } else {
                    content = a.substring(3);
                }

                if (content.length() > 15) {
                    content = content.substring(0, 15);
                }

                lines[lineNum] = content;
            }
        }

        if (!hasPrefix) {
            player.sendMessage(plugin.pName() + "§cYou must specify at least one line using l1:, l2:, l3:, or l4:.");
            return true;
        }

        player.sendMessage(plugin.pName() + "§eYou’re about to modify a sign with this content:");
        for (int i = 0; i < 4; i++) {
            String lineContent = lines[i];
            if (lineContent == null || lineContent.isEmpty()) {
                lineContent = "";
            }
            player.sendMessage("§eLine " + (i + 1) + ": §f[" + colorCodeSpecial(lineContent) + "§f]");
        }

        if (pendingTasks.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(pendingTasks.get(player.getUniqueId()));
        }

        pendingSignEdits.put(player.getUniqueId(), rebuilt.toArray(new String[0]));
        player.sendMessage(plugin.pName() + "§eClick on the sign you want to modify.");

        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (pendingSignEdits.containsKey(player.getUniqueId())) {
                player.sendMessage(plugin.pName() + "§cSign edit request has expired.");
                pendingSignEdits.remove(player.getUniqueId());
                pendingTasks.remove(player.getUniqueId());
            }
        }, timeoutSeconds * 20L);

        pendingTasks.put(player.getUniqueId(), taskId);
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!pendingSignEdits.containsKey(uuid))
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();

        if (block == null)
            return;

        Material type = block.getType();

        if (!(type == Material.SIGN_POST || type == Material.WALL_SIGN))
            return;

        event.setCancelled(true);

        if (pendingTasks.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(pendingTasks.get(uuid));
            pendingTasks.remove(uuid);
        }

        String[] args = pendingSignEdits.remove(uuid);
        applySignEdit(player, block, args);
    }

    private void applySignEdit(Player player, Block targetBlock, String[] args) {
        Sign sign = (Sign) targetBlock.getState();

        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = sign.getLine(i);
        }

        for (String arg : args) {
            String lower = arg.toLowerCase();

            if (lower.matches("l[1-4]:\\(.+\\)")) {

                int lineNum = lower.charAt(1) - '1';
                String content = arg.substring(arg.indexOf('(') + 1, arg.lastIndexOf(')'));

                if (content.length() > 15) {
                    content = content.substring(0, 15);
                }
                lines[lineNum] = content;

            } else if (lower.matches("l[1-4]:.*")) {

                int lineNum = lower.charAt(1) - '1';
                String content = arg.substring(3);

                if (content.length() > 15) {
                    content = content.substring(0, 15);
                }
                lines[lineNum] = content;
            }
        }

        BlockBreakEvent breakEvent = new BlockBreakEvent(targetBlock, player);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInHand();
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(targetBlock, targetBlock.getState(), targetBlock, itemInHand, player, true);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            return;
        }

        SignChangeEvent signEvent = new SignChangeEvent(targetBlock, player, lines.clone());
        Bukkit.getPluginManager().callEvent(signEvent);

        if (signEvent.isCancelled()) {
            return;
        }

        String[] finalLines = signEvent.getLines();

        for (int i = 0; i < 4; i++) {
            sign.setLine(i, finalLines[i]);
        }

        sign.update(true);

        player.sendMessage(plugin.pName() + "§aSign successfully updated.");
    }

    private String colorCodeSpecial(String line) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '&' && i + 1 < line.length()) {
                char next = line.charAt(i + 1);
                if ("0123456789abcdef".indexOf(Character.toLowerCase(next)) != -1) {
                    result.append("§").append(next).append("&").append(next).append("§f");
                    i++;
                    continue;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
}
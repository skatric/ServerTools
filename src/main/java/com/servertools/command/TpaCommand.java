package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles /tpa, /tpaccept and /tpdeny. Each player can have one pending request at a time;
 * a newer request replaces the older one.
 */
public final class TpaCommand implements TabExecutor, Listener {

    private final JavaPlugin plugin;
    private final Messages messages;

    // target -> request waiting for them
    private final Map<UUID, Request> requests = new HashMap<>();

    public TpaCommand(JavaPlugin plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "players-only");
            return true;
        }

        switch (command.getName()) {
            case "tpa" -> sendRequest(player, label, args);
            case "tpaccept" -> answer(player, true);
            case "tpdeny" -> answer(player, false);
        }
        return true;
    }

    private void sendRequest(Player player, String label, String[] args) {
        if (!player.hasPermission("servertools.tpa")) {
            messages.send(player, "no-permission");
            return;
        }
        if (args.length == 0) {
            messages.send(player, "usage", "usage", "/" + label + " <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !player.canSee(target)) {
            messages.send(player, "player-not-found", "player", args[0]);
            return;
        }
        if (target == player) {
            messages.send(player, "tpa-self");
            return;
        }

        UUID targetId = target.getUniqueId();
        long timeoutTicks = Math.max(1, plugin.getConfig().getLong("tpa-timeout", 60)) * 20L;

        Request request = new Request(player.getUniqueId());
        request.expiry = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (requests.remove(targetId, request)) {
                Player requester = Bukkit.getPlayer(request.requester);
                if (requester != null) {
                    messages.send(requester, "tpa-expired", "player", target.getName());
                }
            }
        }, timeoutTicks);

        Request previous = requests.put(targetId, request);
        if (previous != null) {
            previous.expiry.cancel();
        }

        messages.send(player, "tpa-sent", "player", target.getName());
        messages.send(target, "tpa-received", "player", player.getName());
    }

    private void answer(Player player, boolean accept) {
        if (!player.hasPermission(accept ? "servertools.tpaccept" : "servertools.tpdeny")) {
            messages.send(player, "no-permission");
            return;
        }

        Request request = requests.remove(player.getUniqueId());
        Player requester = request == null ? null : Bukkit.getPlayer(request.requester);
        if (request != null) {
            request.expiry.cancel();
        }
        if (requester == null) {
            messages.send(player, "tpa-no-request");
            return;
        }

        if (accept) {
            requester.teleportAsync(player.getLocation());
            messages.send(player, "tpa-accepted", "player", requester.getName());
            messages.send(requester, "tpa-accepted-requester", "player", player.getName());
        } else {
            messages.send(player, "tpa-denied", "player", requester.getName());
            messages.send(requester, "tpa-denied-requester", "player", player.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        requests.entrySet().removeIf(entry -> {
            Request request = entry.getValue();
            if (entry.getKey().equals(id) || request.requester.equals(id)) {
                request.expiry.cancel();
                return true;
            }
            return false;
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("tpa") && args.length == 1 && sender.hasPermission("servertools.tpa")) {
            return Commands.playerNames(sender, args[0]);
        }
        return List.of();
    }

    private static final class Request {
        private final UUID requester;
        private BukkitTask expiry;

        private Request(UUID requester) {
            this.requester = requester;
        }
    }
}

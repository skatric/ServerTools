package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class Commands {

    private Commands() {
    }

    /**
     * Works out who an optional [player] argument points at. Falls back to the sender
     * when no name is given. Returns null (after telling the sender why) if it can't.
     */
    static Player target(CommandSender sender, String[] args, int index, String othersPermission, Messages messages) {
        if (args.length <= index) {
            if (sender instanceof Player player) {
                return player;
            }
            messages.send(sender, "console-needs-player");
            return null;
        }

        Player target = Bukkit.getPlayer(args[index]);
        if (target == null) {
            messages.send(sender, "player-not-found", "player", args[index]);
            return null;
        }
        if (target != sender && !sender.hasPermission(othersPermission)) {
            messages.send(sender, "no-permission");
            return null;
        }
        return target;
    }

    static List<String> playerNames(CommandSender sender, String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player viewer && !viewer.canSee(player)) {
                continue;
            }
            if (player.getName().toLowerCase(Locale.ROOT).startsWith(lower)) {
                names.add(player.getName());
            }
        }
        return names;
    }
}

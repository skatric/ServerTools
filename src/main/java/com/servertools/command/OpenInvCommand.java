package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public final class OpenInvCommand implements TabExecutor {

    private final Messages messages;

    public OpenInvCommand(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "players-only");
            return true;
        }
        if (!player.hasPermission("servertools.openinv")) {
            messages.send(player, "no-permission");
            return true;
        }
        if (args.length == 0) {
            messages.send(player, "usage", "usage", "/" + label + " <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messages.send(player, "player-not-found", "player", args[0]);
            return true;
        }

        // This is the target's live inventory, so anything moved in here changes on their end too.
        player.openInventory(target.getInventory());
        messages.send(player, "openinv-opened", "player", target.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("servertools.openinv")) {
            return Commands.playerNames(sender, args[0]);
        }
        return List.of();
    }
}

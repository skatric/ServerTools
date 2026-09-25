package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class GodCommand implements TabExecutor {

    private final Messages messages;
    private final Set<UUID> godPlayers;

    public GodCommand(Messages messages, Set<UUID> godPlayers) {
        this.messages = messages;
        this.godPlayers = godPlayers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("servertools.god")) {
            messages.send(sender, "no-permission");
            return true;
        }

        Player target = Commands.target(sender, args, 0, "servertools.god.others", messages);
        if (target == null) {
            return true;
        }

        boolean enabled = godPlayers.add(target.getUniqueId());
        if (!enabled) {
            godPlayers.remove(target.getUniqueId());
        }

        messages.send(target, enabled ? "god-enabled" : "god-disabled");
        if (target != sender) {
            messages.send(sender, enabled ? "god-enabled-other" : "god-disabled-other", "player", target.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("servertools.god.others")) {
            return Commands.playerNames(sender, args[0]);
        }
        return List.of();
    }
}

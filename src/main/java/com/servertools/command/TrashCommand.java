package com.servertools.command;

import com.servertools.Messages;
import com.servertools.listener.TrashListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public final class TrashCommand implements TabExecutor {

    private final Messages messages;

    public TrashCommand(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "players-only");
            return true;
        }
        if (!player.hasPermission("servertools.trash")) {
            messages.send(player, "no-permission");
            return true;
        }

        player.openInventory(new TrashListener.TrashHolder(messages.component("trash-title")).getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

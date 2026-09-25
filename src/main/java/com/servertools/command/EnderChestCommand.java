package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public final class EnderChestCommand implements TabExecutor {

    private final Messages messages;

    public EnderChestCommand(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("servertools.enderchest")) {
            messages.send(sender, "no-permission");
            return true;
        }

        Player target = Commands.target(sender, args, 0, "servertools.enderchest.others", messages);
        if (target == null) {
            return true;
        }

        // Console can't look at an inventory, so it opens the chest for the player instead.
        Player viewer = sender instanceof Player player ? player : target;
        viewer.openInventory(target.getEnderChest());

        if (viewer != target) {
            messages.send(viewer, "enderchest-opened-other", "player", target.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("servertools.enderchest.others")) {
            return Commands.playerNames(sender, args[0]);
        }
        return List.of();
    }
}

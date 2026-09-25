package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

public final class FixCommand implements TabExecutor {

    private final Messages messages;

    public FixCommand(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "players-only");
            return true;
        }
        if (!player.hasPermission("servertools.fix")) {
            messages.send(player, "no-permission");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            messages.send(player, "fix-no-item");
            return true;
        }

        if (!(item.getItemMeta() instanceof Damageable meta)
                || (!meta.hasMaxDamage() && item.getType().getMaxDurability() <= 0)) {
            messages.send(player, "fix-not-repairable");
            return true;
        }
        if (!meta.hasDamage()) {
            messages.send(player, "fix-already-full");
            return true;
        }

        meta.setDamage(0);
        item.setItemMeta(meta);
        messages.send(player, "fix-success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}

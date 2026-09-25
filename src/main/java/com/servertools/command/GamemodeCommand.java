package com.servertools.command;

import com.servertools.Messages;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public final class GamemodeCommand implements TabExecutor {

    private static final List<String> MODES = List.of("survival", "creative", "adventure", "spectator");

    private final Messages messages;

    public GamemodeCommand(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("servertools.gamemode")) {
            messages.send(sender, "no-permission");
            return true;
        }
        if (args.length == 0) {
            messages.send(sender, "usage", "usage", "/" + label + " <type> [player]");
            return true;
        }

        GameMode mode = parse(args[0]);
        if (mode == null) {
            messages.send(sender, "gamemode-invalid", "mode", args[0]);
            return true;
        }

        Player target = Commands.target(sender, args, 1, "servertools.gamemode.others", messages);
        if (target == null) {
            return true;
        }

        target.setGameMode(mode);
        String modeName = mode.name().toLowerCase(Locale.ROOT);
        if (target == sender) {
            messages.send(sender, "gamemode-self", "mode", modeName);
        } else {
            messages.send(sender, "gamemode-other", "player", target.getName(), "mode", modeName);
            messages.send(target, "gamemode-target", "mode", modeName, "sender", sender.getName());
        }
        return true;
    }

    private static GameMode parse(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "survival", "s", "0" -> GameMode.SURVIVAL;
            case "creative", "c", "1" -> GameMode.CREATIVE;
            case "adventure", "a", "2" -> GameMode.ADVENTURE;
            case "spectator", "sp", "3" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("servertools.gamemode")) {
            return List.of();
        }
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            return MODES.stream().filter(mode -> mode.startsWith(input)).toList();
        }
        if (args.length == 2 && sender.hasPermission("servertools.gamemode.others")) {
            return Commands.playerNames(sender, args[1]);
        }
        return List.of();
    }
}

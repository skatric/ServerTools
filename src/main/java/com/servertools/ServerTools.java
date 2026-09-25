package com.servertools;

import com.servertools.command.EnderChestCommand;
import com.servertools.command.FixCommand;
import com.servertools.command.GamemodeCommand;
import com.servertools.command.GodCommand;
import com.servertools.command.OpenInvCommand;
import com.servertools.command.TpaCommand;
import com.servertools.command.TrashCommand;
import com.servertools.listener.GodListener;
import com.servertools.listener.TrashListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ServerTools extends JavaPlugin {

    private final Set<UUID> godPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Messages messages = new Messages(getConfig());

        register("gamemode", new GamemodeCommand(messages));
        register("god", new GodCommand(messages, godPlayers));
        register("openinv", new OpenInvCommand(messages));
        register("enderchest", new EnderChestCommand(messages));
        register("fix", new FixCommand(messages));
        register("trash", new TrashCommand(messages));

        TpaCommand tpa = new TpaCommand(this, messages);
        register("tpa", tpa);
        register("tpaccept", tpa);
        register("tpdeny", tpa);

        getServer().getPluginManager().registerEvents(new GodListener(godPlayers), this);
        getServer().getPluginManager().registerEvents(new TrashListener(), this);
        getServer().getPluginManager().registerEvents(tpa, this);
    }

    private void register(String name, TabExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().warning("Command /" + name + " is missing from plugin.yml");
            return;
        }
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }
}

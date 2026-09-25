package com.servertools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public final class Messages {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private final FileConfiguration config;

    public Messages(FileConfiguration config) {
        this.config = config;
    }

    /**
     * Sends a message from the config with the prefix in front.
     * Placeholders are passed as pairs, e.g. send(sender, "tpa-sent", "player", name).
     */
    public void send(CommandSender to, String key, String... placeholders) {
        String text = raw(key, placeholders);
        if (text.isEmpty()) {
            return;
        }
        to.sendMessage(SERIALIZER.deserialize(config.getString("prefix", "") + text));
    }

    public Component component(String key, String... placeholders) {
        return SERIALIZER.deserialize(raw(key, placeholders));
    }

    private String raw(String key, String... placeholders) {
        String text = config.getString("messages." + key, "");
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            text = text.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
        }
        return text;
    }
}

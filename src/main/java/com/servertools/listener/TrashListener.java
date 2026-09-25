package com.servertools.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class TrashListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder(false) instanceof TrashHolder) {
            event.getInventory().clear();
        }
    }

    public static final class TrashHolder implements InventoryHolder {

        private final Inventory inventory;

        public TrashHolder(Component title) {
            this.inventory = Bukkit.createInventory(this, 54, title);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}

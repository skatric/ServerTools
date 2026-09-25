package com.servertools.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Set;
import java.util.UUID;

public final class GodListener implements Listener {

    private final Set<UUID> godPlayers;

    public GodListener(Set<UUID> godPlayers) {
        this.godPlayers = godPlayers;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && godPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.setFireTicks(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player
                && godPlayers.contains(player.getUniqueId())
                && event.getFoodLevel() < player.getFoodLevel()) {
            event.setCancelled(true);
        }
    }
}

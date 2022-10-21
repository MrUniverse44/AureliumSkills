package com.archyx.aureliumskills.api.event;

import com.archyx.aureliumskills.data.PluginPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManaRegenerateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PluginPlayer pluginPlayer;
    private double amount;
    private boolean isCancelled;

    public ManaRegenerateEvent(PluginPlayer pluginPlayer, double amount) {
        this.pluginPlayer = pluginPlayer;
        this.amount = amount;
        this.isCancelled = false;
    }

    public PluginPlayer getPlayerData() {
        return pluginPlayer;
    }

    public Player getPlayer() {
        return pluginPlayer.getBukkitPlayer();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

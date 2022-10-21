package com.archyx.aureliumskills.api.event;

import com.archyx.aureliumskills.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManaRegenerateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerData playerData;
    private double amount;
    private boolean isCancelled;

    public ManaRegenerateEvent(PlayerData playerData, double amount) {
        this.playerData = playerData;
        this.amount = amount;
        this.isCancelled = false;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Player getPlayer() {
        return playerData.getBukkitPlayer();
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

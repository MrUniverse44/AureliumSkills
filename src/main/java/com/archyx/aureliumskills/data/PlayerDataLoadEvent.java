package com.archyx.aureliumskills.data;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDataLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PluginPlayer pluginPlayer;

    public PlayerDataLoadEvent(PluginPlayer pluginPlayer) {
        this.pluginPlayer = pluginPlayer;
    }

    public PluginPlayer getPlayerData() {
        return pluginPlayer;
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

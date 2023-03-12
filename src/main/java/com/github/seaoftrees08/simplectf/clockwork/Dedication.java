package com.github.seaoftrees08.simplectf.clockwork;

import com.github.seaoftrees08.simplectf.PlayerListeners;
import org.bukkit.scheduler.BukkitRunnable;

public class Dedication extends BukkitRunnable {
    private final String playerName;
    public Dedication(String playerName){
        this.playerName = playerName;
    }
    @Override
    public void run() {
        PlayerListeners.dedication.remove(playerName);
    }
}

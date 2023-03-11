package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;
import org.bukkit.entity.Player;

public class ArenaPlayer extends StoredPlayerData {

    public final String playerName;

    public ArenaPlayer(Player player) {
        super(player.getInventory(), player.getLocation());
        playerName = player.getName();
    }
}

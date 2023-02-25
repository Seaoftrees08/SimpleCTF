package com.github.seaoftrees08.simplectf.team;

import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import org.bukkit.entity.Player;

public class ArenaPlayer {
    public final Player player;
    public final String arenaName;
    private final PlayerInventoryItems backup;

    private PlayerInventoryItems gameInventory = new PlayerInventoryItems();

    public ArenaPlayer(Player player, String arenaName){
        this.player = player;
        this.arenaName = arenaName;
        backup = new PlayerInventoryItems(player.getInventory());
        backup.clearInventory(player);
    }

    public void restoreInventory(){
        backup.setInventory(player);
    }

    public void setGameInventory(PlayerInventoryItems pii){
        gameInventory = pii;
    }

    public void applyToPlayerGameInventory(){
        gameInventory.setInventory(player);
    }


}

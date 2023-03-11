package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.ArenaPhase;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerListeners implements Listener {
    public PlayerListeners(SimpleCTF simpleCTF) {
        simpleCTF.getServer().getPluginManager().registerEvents(this, simpleCTF);
    }

    //Playerがブロックを破壊したときに発生
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        String playerName = e.getPlayer().getName();

        //Arena作成時
        if(ArenaManager.isCreating(playerName) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)){
            String arenaName = ArenaManager.getBelongingCreateArenaName(playerName);
            ArenaPhase ap = ArenaManager.getCreateArenaPhase(arenaName);
            if(ap.equals(ArenaPhase.FIRST_POINT_SETTING) || ap.equals(ArenaPhase.SECOND_POINT_SETTING)
                || ap.equals(ArenaPhase.RED_FLAG_SETTING) || ap.equals(ArenaPhase.BLUE_FLAG_SETTING)){
                ArenaManager.doCreateFlow(arenaName, e.getBlock().getLocation(), null);
                e.setCancelled(true);
            }
        }

    }
}

package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.ArenaCreationCause;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.team.PlayerManager;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerListeners implements Listener {
    public PlayerListeners(SimpleCTF simpleCTF) {
        simpleCTF.getServer().getPluginManager().registerEvents(this, simpleCTF);
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        String name = e.getPlayer().getName();

        //Arena作成時
        if(ArenaManager.isCreating(name) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)){
            ArenaManager.doCreationFlow(name, ArenaCreationCause.EVENT, e.getBlock().getLocation(), null);
            e.setCancelled(true);
        }

        //プレイ中の破壊禁止
        if(PlayerManager.isJoined(name)){
            e.setCancelled(true);
        }
    }
}

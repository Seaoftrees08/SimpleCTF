package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.ArenaCreationCause;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerListeners implements Listener {
    public PlayerListeners(SimpleCTF simpleCTF) {
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        String name = e.getPlayer().getName();

        //Arena作成時
        if(ArenaManager.isCreating(name) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)){
            ArenaManager.doCreationFlow(name, ArenaCreationCause.EVENT, new Vec3i(e.getBlock().getLocation()), null);
            e.setCancelled(true);
        }

        //プレイ中の破壊禁止
        if(ArenaManager.isJoined(name)){
            e.setCancelled(true);
        }
    }
}

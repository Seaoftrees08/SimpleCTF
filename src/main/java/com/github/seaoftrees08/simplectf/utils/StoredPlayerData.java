package com.github.seaoftrees08.simplectf.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class StoredPlayerData {

    private final List<ArenaItemStack> invContents = new ArrayList<>();  //インベントリ内のアイテム
    private ArenaItemStack helmet;
    private ArenaItemStack chest;
    private ArenaItemStack leggings;
    private ArenaItemStack boots;
    private ArenaItemStack offHand;
    private Location location;

    public StoredPlayerData(PlayerInventory pi, Location loc){
        for(ItemStack is : pi.getContents()) invContents.add(new ArenaItemStack(is));
        location = loc;
    }

    /**
     * プレイヤーにインベントリの内容を設定する.
     * すでに持っているアイテムはすべて削除される.
     * @param player 設定するプレイヤー
     */
    public void setInventory(Player player){
        player.getInventory();
        //TODO
    }

    /**
     * 設定されているLocationを返す
     * @return 設定されているLocation
     */
    public Location getLocation() {
        return location;
    }
}

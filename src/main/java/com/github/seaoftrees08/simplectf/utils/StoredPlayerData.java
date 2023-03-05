package com.github.seaoftrees08.simplectf.utils;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class StoredPlayerData {

    private final List<ArenaItem> invContents = new ArrayList<>();  //インベントリ内のアイテム
    private ArenaItem helmet;
    private ArenaItem chest;
    private ArenaItem leggings;
    private ArenaItem boots;
    private ArenaItem offHand;
    private Location location;

    public StoredPlayerData(PlayerInventory pi){
        for(ItemStack is : pi.getContents()) invContents.add(new ArenaItem(is));
        //TODO
    }


}

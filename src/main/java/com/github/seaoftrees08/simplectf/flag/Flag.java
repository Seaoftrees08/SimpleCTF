package com.github.seaoftrees08.simplectf.flag;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class Flag {

    public FlagStatus status = FlagStatus.CAMP;
    private final FlagItem flagItem;
    private final Location campLocation;
    private Location location;
    public int onGroundedTime = 0;//-1 -> PlayerListener等でonGroundを感知、0->camp、n->地面に落ちた時のremTime
    private Item onGroundItem;

    private Player havingPlayer;

    public Flag(FlagItem flagItem, Location flagFenceLocation){
        this.flagItem = flagItem;
        campLocation = flagFenceLocation;
        location = flagFenceLocation;
    }

    /**
     * flagの場所を返す
     * @return
     */
    public Location getLocation(){
        switch (status){
            case CAMP -> { return location.clone().add(0,1,0); }
            case GROUND -> { return onGroundItem.getLocation(); }
            case BRING -> { return havingPlayer.getLocation(); }
        }
        return location;
    }

    public Location getCampLocation(){ return campLocation; }

    /**
     * flagを落とした時
     * @param item 落としたItem(Wool)
     */
    public void drop(Item item){
        this.onGroundItem = item;
        this.havingPlayer = null;
        this.location = item.getLocation();
        status = FlagStatus.GROUND;
    }

    /**
     * flagをcampに召喚、帰還させるとき
     */
    public void spawnCamp(){
        this.onGroundItem = null;
        this.havingPlayer = null;
        this.location = campLocation.clone();
        flagItem.spawn();
        status = FlagStatus.CAMP;
    }

    public void pickUp(Player p){
        this.onGroundItem = null;
        this.havingPlayer = p;
        this.location = null;
        status = FlagStatus.BRING;
    }

    public boolean hasFlag(String playerName){ return havingPlayer != null && havingPlayer.getName().equals(playerName); }

    public Player getHavingPlayer(){
        return havingPlayer;
    }

    public static ItemStack getRedFlagItemStack(){
        ItemStack is = new ItemStack(Material.RED_WOOL);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.RED + "Red Flag");
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getBlueFlagItemStack(){
        ItemStack is = new ItemStack(Material.BLUE_WOOL);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.BLUE + "Blue Flag");
        is.setItemMeta(im);
        return is;
    }
}

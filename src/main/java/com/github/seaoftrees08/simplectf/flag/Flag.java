package com.github.seaoftrees08.simplectf.flag;

import com.github.seaoftrees08.simplectf.arena.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Flag {
    public static final String RED_FLAG_NAME = "Red Flag";
    public static final String BLUE_FLAG_NAME = "Blue Flag";
    public FlagStatus status = FlagStatus.CAMP;
    private final FlagItem flagItem;
    private final Location campLocation;
//    private Location location;
    public int onGroundedTime = 0;//-1 -> PlayerListener等でonGroundを感知、0->camp、n->地面に落ちた時のremTime
    private Item onGroundItem;

    private Player havingPlayer;

    public Flag(TeamColor color, Location campLocation){
        this.flagItem = getFlagItem(color, campLocation);
        this.campLocation = campLocation;
    }

    /**
     * flagの場所を返す
     * @return
     */
    public Location getLocation(){
        switch (status){
            case CAMP -> { return flagItem.getFenceLocation(); }
            case GROUND -> { return onGroundItem.getLocation(); }
            case BRING -> { return havingPlayer.getLocation(); }
        }
        return flagItem.getFenceLocation();
    }

    public Location getCampLocation(){ return campLocation; }

    /**
     * flagを落とした時
     * @param item 落としたItem(Wool)
     */
    public void drop(Item item){
        this.onGroundItem = item;
        this.havingPlayer = null;
        status = FlagStatus.GROUND;
    }

    /**
     * flagをcampに召喚、帰還させるとき
     */
    public void spawnCamp(){
        this.onGroundItem = null;
        this.havingPlayer = null;
        flagItem.spawn();
        status = FlagStatus.CAMP;
    }

    public void pickUp(Player p){
        this.onGroundItem = null;
        this.havingPlayer = p;
        status = FlagStatus.BRING;
    }

    public boolean hasFlag(String playerName){ return havingPlayer != null && havingPlayer.getName().equals(playerName); }

    public void kill(){
        flagItem.kill();
    }

    public static ItemStack getRedFlagItemStack(){
        ItemStack is = new ItemStack(Material.RED_WOOL);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.RED + RED_FLAG_NAME);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getBlueFlagItemStack(){
        ItemStack is = new ItemStack(Material.BLUE_WOOL);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(ChatColor.BLUE + BLUE_FLAG_NAME);
        is.setItemMeta(im);
        return is;
    }

    private static FlagItem getFlagItem(TeamColor color, Location flagFenceLocation){
        switch (color){
            case RED -> {
                return new FlagItem(
                        flagFenceLocation,
                        Flag.RED_FLAG_NAME,
                        new ItemStack(Material.RED_WOOL)
                );
            }
            case BLUE -> {
                return new FlagItem(
                        flagFenceLocation,
                        Flag.BLUE_FLAG_NAME,
                        new ItemStack(Material.BLUE_WOOL)
                );
            }
            default -> {}
        }
        return new FlagItem(null, "", new ItemStack(Material.AIR));
    }
}

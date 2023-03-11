package com.github.seaoftrees08.simplectf.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoredPlayerData {

    private List<ArenaItemStack> invContents = new ArrayList<>();  //インベントリ内のアイテム
    private ArenaItemStack helmet = new ArenaItemStack(new ItemStack(Material.AIR));
    private ArenaItemStack chest = new ArenaItemStack(new ItemStack(Material.AIR));
    private ArenaItemStack leggings = new ArenaItemStack(new ItemStack(Material.AIR));
    private ArenaItemStack boots = new ArenaItemStack(new ItemStack(Material.AIR));
    private ArenaItemStack offHand = new ArenaItemStack(new ItemStack(Material.AIR));
    private LocationStringList location; //spawn location or return location

    public StoredPlayerData(){

    }
    public StoredPlayerData(PlayerInventory pi, Location loc){
        for(ItemStack is : pi.getContents()) invContents.add(new ArenaItemStack(is));
        helmet = new ArenaItemStack(pi.getHelmet());
        chest = new ArenaItemStack(pi.getChestplate());
        leggings = new ArenaItemStack(pi.getLeggings());
        boots = new ArenaItemStack(pi.getBoots());
        offHand = new ArenaItemStack(pi.getItemInOffHand());
        location = new LocationStringList(loc);

        int count = helmet.isAir()
                + chest.isAir()
                + leggings.isAir()
                + boots.isAir()
                + offHand.isAir();
        for(int i=0; i<count; i++) invContents.remove(invContents.size()-1);
    }

    /**
     * チームのデータをセットするときに使う
     * @param teamInventory チームのインベントリ
     * @param spawnPoint チームのスポーンポイント
     */
    public StoredPlayerData(List<ArenaItemStack> teamInventory, ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots,
                            ItemStack offHand, LocationStringList spawnPoint){
        invContents = teamInventory;
        this.helmet = new ArenaItemStack(helmet);
        this.chest = new ArenaItemStack(chest);
        this.leggings = new ArenaItemStack(leggings);
        this.boots = new ArenaItemStack(boots);
        this.offHand = new ArenaItemStack(offHand);
        location = spawnPoint;
    }

    /**
     * プレイヤーにインベントリの内容を設定する.
     * すでに持っているアイテムはすべて削除される.
     * @param player 設定するプレイヤー
     */
    public void setInventory(Player player){
        PlayerInventory pi = player.getInventory();
        clearInventory(pi);

        //inventory set
        invContents.forEach(pi::addItem);
        pi.setHelmet(helmet);
        pi.setChestplate(chest);
        pi.setLeggings(leggings);
        pi.setBoots(boots);
        pi.setItemInOffHand(offHand);
    }

    /**
     * メインアイテムのStringListのListを返す.
     * yaml保存用
     * @return メインアイテムのStringListのList
     */
    public List<List<String>> getMainItemStringList(){
        return invContents.stream()
                .map(ArenaItemStack::getStringList)
                .toList();
    }

    public List<String> getHelmetStringList(){
        return helmet.getStringList();
    }

    public List<String> getChestStringList(){
        return chest.getStringList();
    }

    public List<String> getLeggingsStringList(){
        return leggings.getStringList();
    }

    public List<String> getBootsStringList(){
        return boots.getStringList();
    }

    /**
     * offHandに持っているアイテムのStringListを返す
     * yaml保存用
     * @return offHandに持っているアイテムのStringList
     */
    public List<String> getOffHandItemStringList(){
        return offHand.getStringList();
    }

    /**
     * 設定されているLocationStringListを返す
     * @return 設定されているLocationStringList
     */
    public LocationStringList getLocationStringList() {
        return location;
    }

    public static void clearInventory(PlayerInventory pi){
        pi.clear();
        pi.setHelmet(new ItemStack(Material.AIR));
        pi.setChestplate(new ItemStack(Material.AIR));
        pi.setLeggings(new ItemStack(Material.AIR));
        pi.setBoots(new ItemStack(Material.AIR));
        pi.setItemInOffHand(new ItemStack(Material.AIR));
    }
}

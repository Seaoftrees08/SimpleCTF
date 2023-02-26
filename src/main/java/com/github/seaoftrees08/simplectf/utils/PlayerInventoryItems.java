package com.github.seaoftrees08.simplectf.utils;


import com.github.seaoftrees08.simplectf.arena.ArenaItem;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerInventoryItems {

    private List<ItemStack> mainItems = new ArrayList<>();
    private ItemStack headItem = new ItemStack(Material.AIR);
    private ItemStack chestItem = new ItemStack(Material.AIR);
    private ItemStack leggingsItem = new ItemStack(Material.AIR);
    private ItemStack bootsItem = new ItemStack(Material.AIR);
    private ItemStack leftHandItem = new ItemStack(Material.AIR);

    public PlayerInventoryItems(){

    }
    public PlayerInventoryItems(PlayerInventory pi){
        List<ItemStack> contents = new ArrayList<>();
        for(ItemStack is : pi.getContents()){
            contents.add(is==null ? new ItemStack(Material.AIR) : is);
        }
        int count=0;
        if(pi.getHelmet() != null) count++;
        if(pi.getChestplate() != null) count++;
        if(pi.getLeggings() != null) count++;
        if(pi.getBoots() != null) count++;
        if(pi.getItemInOffHand().getType().equals(Material.AIR)) count++;
        for(int i=0; i<count; i++) contents.remove(contents.size()-1);
        mainItems.addAll(contents);
        headItem = pi.getHelmet()==null ? new ItemStack(Material.AIR) : pi.getHelmet();
        chestItem = pi.getChestplate()==null ? new ItemStack(Material.AIR) : pi.getChestplate();
        leggingsItem = pi.getLeggings()==null ? new ItemStack(Material.AIR) : pi.getLeggings();
        bootsItem = pi.getBoots()==null ? new ItemStack(Material.AIR) : pi.getBoots();
        leftHandItem = pi.getItemInOffHand();
    }

    public PlayerInventoryItems(List<ItemStack> main, ItemStack head, ItemStack chest, ItemStack leggings, ItemStack boots, ItemStack leftHand){
        mainItems = main;
        headItem = head;
        chestItem = chest;
        leggingsItem = leggings;
        bootsItem = boots;
        leftHandItem = leftHand;
    }

    private List<String> getHeadItemStringList(){ return new ArenaItem(headItem).getStringList(); }

    private List<String> getChestItemStringList(){
        return new ArenaItem(chestItem).getStringList();
    }

    private List<String> getLeggingsItemStringList(){
        return new ArenaItem(leggingsItem).getStringList();
    }

    private List<String> getBootsItemStringList(){
        return new ArenaItem(bootsItem).getStringList();
    }

    /**
     * mainItemをStringのListにして、それをListにしたmainItem群を返す
     * @return Yaml保存で使うListのList
     */
    public List<List<String>> getMainItemStringList(){
        return mainItems.stream()
                .map(is -> is==null ? new ItemStack(Material.AIR) : is)
                .map(ArenaItem::new)
                .map(ArenaItem::getStringList)
                .toList();
    }

    public List<List<String>> getArmorList(){
        return Arrays.asList(getBootsItemStringList(), getLeggingsItemStringList(), getChestItemStringList(), getHeadItemStringList());
    }

    public List<String> getLeftHandItemStringList(){
        return new ArenaItem(leftHandItem).getStringList();
    }

    /**
     * Playerのインベントリを上書き設定する。中身はthis
     * @param p 設定するplayer
     */
    public void setInventory(Player p){
        PlayerInventory pi = p.getInventory();

        clearInventory(p);

        for(ItemStack is : mainItems) pi.addItem(is==null ? new ItemStack(Material.AIR) : is);//ほんまか?
        pi.setHelmet(headItem);
        pi.setChestplate(chestItem);
        pi.setLeggings(leggingsItem);
        pi.setBoots(bootsItem);
        pi.setItemInOffHand(leftHandItem);

    }

    public void clearInventory(Player p){
        PlayerInventory pi = p.getInventory();
        pi.clear();
        pi.setHelmet(new ItemStack(Material.AIR));
        pi.setChestplate(new ItemStack(Material.AIR));
        pi.setLeggings(new ItemStack(Material.AIR));
        pi.setBoots(new ItemStack(Material.AIR));
        pi.setItemInOffHand(new ItemStack(Material.AIR));
    }
}

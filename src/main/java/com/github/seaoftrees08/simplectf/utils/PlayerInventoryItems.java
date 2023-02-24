package com.github.seaoftrees08.simplectf.utils;


import org.bukkit.Material;
import org.bukkit.entity.Item;
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

    public PlayerInventoryItems(PlayerInventory pi){
        mainItems.addAll(Arrays.asList(pi.getContents()));
        headItem = pi.getHelmet();
        chestItem = pi.getChestplate();
        leggingsItem = pi.getLeggings();
        bootsItem = pi.getBoots();
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

}

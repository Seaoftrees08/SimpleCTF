package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.team.PlayerManager;
import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    public final String name;
    private final FileConfiguration yml;
    private final File file;
    private Vec3i firstPoint;
    private Vec3i secondPoint;
    private Vec3i redFlag;
    private Vec3i blueFlag;
    private ArenaLocation redSpawn;
    private ArenaLocation blueSpawn;
    private ArenaLocation returnPoint;
    private PlayerInventoryItems redInv;
    private PlayerInventoryItems blueInv;
    private boolean enable;

    public Arena(String name){
        this.name = name;
        file = new File(SimpleCTF.getSimpleCTF().getDataFolder(), name+ ".yml");
        yml = YamlConfiguration.loadConfiguration(file);

        firstPoint = new Vec3i(yml.getIntegerList(ArenaYamlPath.FIRST_POINT));
        secondPoint = new Vec3i(yml.getIntegerList(ArenaYamlPath.SECOND_POINT));
        redFlag = new Vec3i(yml.getIntegerList(ArenaYamlPath.RED_FLAG));
        blueFlag = new Vec3i(yml.getIntegerList(ArenaYamlPath.BLUE_FLAG));
        redSpawn = new ArenaLocation(yml.getStringList(ArenaYamlPath.RED_SPAWN));
        blueSpawn = new ArenaLocation(yml.getStringList(ArenaYamlPath.BLUE_SPAWN));
        returnPoint = new ArenaLocation(yml.getStringList(ArenaYamlPath.RETURN_POINT));
        enable = yml.getBoolean(ArenaYamlPath.ENABLE);

        //redInventory
        ArrayList<ItemStack> redMainItems = new ArrayList<>();
        if(yml.getList(ArenaYamlPath.RED_INV_ITEMS) != null){
            //main items
            for(List<String> lst : (List<ArrayList<String>>) yml.getList(ArenaYamlPath.RED_INV_ITEMS)){
                redMainItems.add(new ArenaItem(lst).getItemStack());
            }
        }
        //Red armor
        ItemStack redHead = new ItemStack(Material.AIR);
        ItemStack redChest = new ItemStack(Material.AIR);
        ItemStack redLeggings = new ItemStack(Material.AIR);
        ItemStack redBoots = new ItemStack(Material.AIR);
        if(yml.getList(ArenaYamlPath.RED_INV_ARMOR) != null){

            int i = 0;
            for(List<String> lst : (List<ArrayList<String>>) yml.getList(ArenaYamlPath.RED_INV_ARMOR)){
                switch (i){
                    case 0: redBoots = new ArenaItem(lst).getItemStack();
                    case 1: redLeggings = new ArenaItem(lst).getItemStack();
                    case 2: redChest = new ArenaItem(lst).getItemStack();
                    case 3: redHead = new ArenaItem(lst).getItemStack();
                }
                i++;
            }
        }
        //Red left Hand
        ItemStack redLeftHand = new ArenaItem(yml.getStringList(ArenaYamlPath.RED_INV_LEFTHAND));
        redInv = new PlayerInventoryItems(redMainItems, redHead, redChest, redLeggings, redBoots, redLeftHand);

        //blueInventory
        ArrayList<ItemStack> blueMainItems = new ArrayList<>();
        if(yml.getList(ArenaYamlPath.BLUE_INV_ITEMS) != null){
            //main items
            for(List<String> lst : (List<ArrayList<String>>) yml.getList(ArenaYamlPath.BLUE_INV_ITEMS)){
                blueMainItems.add(new ArenaItem(lst).getItemStack());
            }
        }
        //blue armor
        ItemStack blueHead = new ItemStack(Material.AIR);
        ItemStack blueChest = new ItemStack(Material.AIR);
        ItemStack blueLeggings = new ItemStack(Material.AIR);
        ItemStack blueBoots = new ItemStack(Material.AIR);
        if(yml.getList(ArenaYamlPath.BLUE_INV_ARMOR) != null){

            int i = 0;
            for(List<String> lst : (List<ArrayList<String>>) yml.getList(ArenaYamlPath.BLUE_INV_ARMOR)){
                switch (i){
                    case 0: blueBoots = new ArenaItem(lst).getItemStack();
                    case 1: blueLeggings = new ArenaItem(lst).getItemStack();
                    case 2: blueChest = new ArenaItem(lst).getItemStack();
                    case 3: blueHead = new ArenaItem(lst).getItemStack();
                }
                i++;
            }
        }
        //blue left Hand
        ItemStack blueLeftHand = new ArenaItem(yml.getStringList(ArenaYamlPath.BLUE_INV_LEFTHAND));
        blueInv = new PlayerInventoryItems(blueMainItems, blueHead, blueChest, blueLeggings, blueBoots, blueLeftHand);

        //canPlay

    }

    public void setFirstPoint(Vec3i v){
        this.firstPoint = v;
    }

    public void setSecondPoint(Vec3i v){
        this.secondPoint = v;
    }

    public void setRedFlag(Vec3i v){
        this.redFlag = v;
    }

    public void setBlueFlag(Vec3i v){
        this.blueFlag = v;
    }

    public void setRedSpawn(Location loc){
        this.redSpawn = new ArenaLocation(loc);
    }

    public void setBlueSpawn(Location loc){
        this.blueSpawn = new ArenaLocation(loc);
    }

    public void setReturnPoint(Location loc){
        this.returnPoint = new ArenaLocation(loc);
    }

    public void setRedInv(PlayerInventoryItems pii){
        this.redInv = pii;
    }

    public void setBlueInv(PlayerInventoryItems pii){
        this.blueInv = pii;
    }

    public void setEnable(boolean b){
        this.enable = b;
    }

    public boolean isEnable(){
        return enable;
    }

    public void save(){
        //ArenaList
        List<String> arenaList = ArenaManager.loadArenaNameList();
        if(!arenaList.contains(name)) arenaList.add(name);
        SimpleCTF.getSimpleCTF().getConfig().set(ArenaManager.ARENA_LIST_PATH, arenaList);
        SimpleCTF.getSimpleCTF().saveConfig();

        yml.set(ArenaYamlPath.FIRST_POINT, firstPoint.getList());     //firstPoint
        yml.set(ArenaYamlPath.SECOND_POINT, secondPoint.getList());   //secondPoint
        yml.set(ArenaYamlPath.RED_FLAG, redFlag.getList());           //redFlag
        yml.set(ArenaYamlPath.BLUE_FLAG, blueFlag.getList());         //blueFlag
        yml.set(ArenaYamlPath.RED_SPAWN, redSpawn.getStringList());         //redSpawn
        yml.set(ArenaYamlPath.BLUE_SPAWN, blueSpawn.getStringList());       //blueSpawn
        yml.set(ArenaYamlPath.RETURN_POINT, returnPoint.getStringList());   //returnPoint
        yml.set(ArenaYamlPath.RED_INV_ITEMS, redInv.getMainItemStringList());   //redMainItem
        yml.set(ArenaYamlPath.RED_INV_ARMOR, redInv.getArmorList());            //redArmor
        yml.set(ArenaYamlPath.RED_INV_LEFTHAND, redInv.getLeftHandItemStringList()); //redLeftHand
        yml.set(ArenaYamlPath.BLUE_INV_ITEMS, blueInv.getMainItemStringList());   //blueMainItem
        yml.set(ArenaYamlPath.BLUE_INV_ARMOR, blueInv.getArmorList());            //blueArmor
        yml.set(ArenaYamlPath.BLUE_INV_LEFTHAND, blueInv.getLeftHandItemStringList()); //blueLeftHand
        yml.set(ArenaYamlPath.ENABLE, enable);
        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(){
        file.delete();
    }

}

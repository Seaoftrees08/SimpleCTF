package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Utils;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final String name;
    private final FileConfiguration yml;
    private Vec3i firstPoint;
    private Vec3i secondPoint;
    private Vec3i redFlag;
    private Vec3i blueFlag;
    private Location redSpawn;
    private Location blueSpawn;
    private Location returnPoint;
    private PlayerInventoryItems redInv;
    private PlayerInventoryItems blueInv;
    private boolean enable;

    public Arena(String name){
        this.name = name;
        yml = YamlConfiguration.loadConfiguration(new File(SimpleCTF.getSimpleCTF().getDataFolder(), name+ ".yml"));

        firstPoint = new Vec3i(yml.getIntegerList(ArenaYamlPath.FIRST_POINT));
        secondPoint = new Vec3i(yml.getIntegerList(ArenaYamlPath.SECOND_POINT));
        redFlag = new Vec3i(yml.getIntegerList(ArenaYamlPath.RED_FLAG));
        blueFlag = new Vec3i(yml.getIntegerList(ArenaYamlPath.BLUE_FLAG));
        redSpawn = Utils.listToLocation(yml.getStringList(ArenaYamlPath.RED_SPAWN));
        blueSpawn = Utils.listToLocation(yml.getStringList(ArenaYamlPath.BLUE_SPAWN));
        returnPoint = Utils.listToLocation(yml.getStringList(ArenaYamlPath.RETURN_POINT));
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
        this.redSpawn = loc;
    }

    public void setBlueSpawn(Location loc){
        this.blueSpawn = loc;
    }

    public void setReturnPoint(Location loc){
        this.returnPoint = loc;
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

    /**
     * Arenaの名前一覧をconfig.ymlから読み取って返す
     * @return ArenaNameの一覧
     */
    public static List<String> loadArenaNameList(){
        FileConfiguration fc = SimpleCTF.getSimpleCTF().getConfig();
        return fc.getStringList("ArenaList");
    }

    /**
     * 新しいArenaを追加してconfigにセーブする
     * @param newArenaName 新しいArenaName
     */
    public static void saveArenaNameList(String newArenaName){
        FileConfiguration fc = SimpleCTF.getSimpleCTF().getConfig();
        List<String> lst = loadArenaNameList();
        lst.add(newArenaName);
        fc.set("ArenaList", lst);
        SimpleCTF.getSimpleCTF().saveConfig();
    }

    /**
     * ArenaのListを取得する
     * @return ArenaのList
     */
    public static List<Arena> getArenaList(){
        ArrayList<Arena> lst = new ArrayList<>();
        for(String name : loadArenaNameList()){
            lst.add(new Arena(name));
        }
        return lst;
    }




}

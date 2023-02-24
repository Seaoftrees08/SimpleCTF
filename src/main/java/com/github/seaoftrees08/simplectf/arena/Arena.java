package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final String name;
    private final FileConfiguration yml;

    public Arena(String name){
        this.name = name;
        yml = YamlConfiguration.loadConfiguration(new File(SimpleCTF.getSimpleCTF().getDataFolder(), name+ ".yml"));
    }



    /**
     * Arenaの一覧をconfig.ymlから読み取って返す
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
        for(String name : loadArenaNameList()){
            //TODO
        }
        return null;
    }




}

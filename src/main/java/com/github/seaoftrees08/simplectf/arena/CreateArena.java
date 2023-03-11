package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.utils.ArenaYamlPath;
import com.github.seaoftrees08.simplectf.utils.Cuboid;
import com.github.seaoftrees08.simplectf.utils.LocationStringList;
import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class CreateArena extends Arena{

    public final Player author;

    /**
     * 最初のアリーナ作成時に呼ばれる場所.
     *
     * @param uniqueName アリーナ名, 使用されていないことを要請する
     * @param player 作成者. メッセージを送るのに使う
     * @param isCreation 初期作成かどうか。そうじゃないほうのコンストラクタとの差別用に追加しているだけで特に意味はない
     */
    public CreateArena(String uniqueName, Player player, boolean isCreation) {
        super(uniqueName, isCreation);
        author = player;
        phase = ArenaPhase.FIRST_POINT_SETTING;
        player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
        sendMessage("Arena-Creation has 9 phase.");
        sendMessage("1st: left punch first arena edge with BLAZE_ROD plz.");
    }

    /**
     * enableの更新で使用する、書き換え用CreateArena
     *
     * @param arenaName アリーナ名. 既に使用されていることを要請する.
     * @param player CreateArenaの作成者. メッセージを送るのに使う
     */
    public CreateArena(String arenaName, Player player){
        super(arenaName);
        author = player;
    }


    /**
     * 外部からはこれを呼び出せばフェーズは進むヤツ
     * ただし、他のフェーズ等の除外設定は住んでいるものとする.
     * @param loc 登録するLocation
     * @param player インベントリを登録する際に使用
     */
    public void flow(Location loc, Player player){
        switch (phase){
            case FIRST_POINT_SETTING -> setFirstPoint(loc);
            case SECOND_POINT_SETTING -> setSecondPoint(loc);
            case RED_FLAG_SETTING -> setRedFlagFence(loc);
            case BLUE_FLAG_SETTING -> setBlueFlagFence(loc);
            case RED_TEAM_DATA_SETTING -> setRedSpawnAndInventory(player);
            case BLUE_TEAM_DATA_SETTING -> setBlueSPawnAndInventory(player);
            default -> {}
        }
    }

    public ArenaPhase getArenaPhase(){
        return phase;
    }

    public void save(){
        //アリーナ一覧を更新
        List<String> arenaList = ArenaManager.loadArenaNameList();
        if(!arenaList.contains(arenaName)){
            arenaList.add(arenaName);
            SimpleCTF.getSimpleCTF().getConfig().set(ArenaManager.ARENA_LIST_PATH, arenaList);
            SimpleCTF.getSimpleCTF().saveConfig();
        }

        //アリーナの範囲を保存
        yml.set(ArenaYamlPath.FIRST_POINT, arenaField.getMinPointIntegerList());
        yml.set(ArenaYamlPath.SECOND_POINT, arenaField.getMaxPointIntegerList());
        //旗の場所を保存
        yml.set(ArenaYamlPath.RED_FLAG, new LocationStringList(redFlag.getCampLocation()));
        yml.set(ArenaYamlPath.BLUE_FLAG, new LocationStringList(blueFlag.getCampLocation()));
        //スポーンポイント
        yml.set(ArenaYamlPath.RED_SPAWN, redTeam.getStoredPlayerData().getLocationStringList());
        yml.set(ArenaYamlPath.BLUE_SPAWN, blueTeam.getStoredPlayerData().getLocationStringList());
        //Red Inventory
        yml.set(ArenaYamlPath.RED_INV_ITEMS, redTeam.getStoredPlayerData().getMainItemStringList());
        yml.set(ArenaYamlPath.RED_INV_HELMET, redTeam.getStoredPlayerData().getHelmetStringList());
        yml.set(ArenaYamlPath.RED_INV_CHEST_PLATE, redTeam.getStoredPlayerData().getChestStringList());
        yml.set(ArenaYamlPath.RED_INV_LEGGINGS, redTeam.getStoredPlayerData().getLeggingsStringList());
        yml.set(ArenaYamlPath.RED_INV_BOOTS, redTeam.getStoredPlayerData().getBootsStringList());
        yml.set(ArenaYamlPath.RED_INV_OFFHAND, redTeam.getStoredPlayerData().getOffHandItemStringList());
        //Blue Inventory
        yml.set(ArenaYamlPath.BLUE_INV_ITEMS, blueTeam.getStoredPlayerData().getMainItemStringList());
        yml.set(ArenaYamlPath.BLUE_INV_HELMET, blueTeam.getStoredPlayerData().getHelmetStringList());
        yml.set(ArenaYamlPath.BLUE_INV_CHEST_PLATE, blueTeam.getStoredPlayerData().getChestStringList());
        yml.set(ArenaYamlPath.BLUE_INV_LEGGINGS, blueTeam.getStoredPlayerData().getLeggingsStringList());
        yml.set(ArenaYamlPath.BLUE_INV_BOOTS, blueTeam.getStoredPlayerData().getBootsStringList());
        yml.set(ArenaYamlPath.BLUE_INV_OFFHAND, blueTeam.getStoredPlayerData().getOffHandItemStringList());
        //enable
        yml.set(ArenaYamlPath.ENABLE, enable);

        //save file
        try{
            yml.save(file);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteArena(){
        //アリーナ一覧を更新
        List<String> arenaList = ArenaManager.loadArenaNameList();
        if(arenaList.contains(arenaName)){
            arenaList.remove(arenaName);
            SimpleCTF.getSimpleCTF().getConfig().set(ArenaManager.ARENA_LIST_PATH, arenaList);
            SimpleCTF.getSimpleCTF().saveConfig();
        }

        //<arenaName>.ymlを削除
        if(file.exists()){
            file.delete();
        }
    }

    public void setEnable(boolean value){
        enable = value;
    }

    private void setFirstPoint(Location loc){
        arenaField = new Cuboid(loc);
        phase = ArenaPhase.SECOND_POINT_SETTING;
        sendMessage("2nd: left punch second arena edge with BLAZE_ROD plz.");
    }

    private void setSecondPoint(Location loc){
        arenaField.setSecond(loc);
        phase = ArenaPhase.RED_FLAG_SETTING;
        sendMessage("3rd: left punch " + ChatColor.RED + "Red Flag fence" + ChatColor.GREEN + " with BLAZE_ROD plz.");
    }

    private void setRedFlagFence(Location loc){
        redFlag = new Flag(TeamColor.RED, loc);
        phase = ArenaPhase.BLUE_FLAG_SETTING;
        sendMessage("4rd: left punch " + ChatColor.BLUE + "Blue Flag fence" + ChatColor.GREEN + " with BLAZE_ROD plz.");
    }

    private void setBlueFlagFence(Location loc){
        blueFlag = new Flag(TeamColor.BLUE, loc);
        phase = ArenaPhase.RED_TEAM_DATA_SETTING;
        sendMessage("5th: set " + ChatColor.RED + "RedInventory and SpawnPoint" + ChatColor.GREEN + " with command.");
        sendMessage("Stand at the " + ChatColor.RED + "RedTeam's SpawnPoint" + ChatColor.GREEN + " and execute the following command.");
        sendMessage("Please type " + ChatColor.GOLD + " /sctf admin setTeam");
    }

    private void setRedSpawnAndInventory(Player player){
        StoredPlayerData spd = new StoredPlayerData(player.getInventory(), player.getLocation());
        redTeam = new ArenaTeam(TeamColor.RED, spd);
        phase = ArenaPhase.BLUE_TEAM_DATA_SETTING;
        sendMessage("6th: set " + ChatColor.BLUE + "BlueInventory and SpawnPoint" + ChatColor.GREEN + " with command.");
        sendMessage("Stand at the " + ChatColor.BLUE + "BlueTeam's SpawnPoint" + ChatColor.GREEN + " and execute the following command.");
        sendMessage("Please type " + ChatColor.GOLD + " /sctf admin setTeam");
    }

    private void setBlueSPawnAndInventory(Player player){
        StoredPlayerData spd = new StoredPlayerData(player.getInventory(), player.getLocation());
        blueTeam = new ArenaTeam(TeamColor.BLUE, spd);
        save();
        phase = ArenaPhase.FINISHED;
        ArenaManager.finishCreation(arenaName);
        sendMessage("Finish arena creation! Arena " + ChatColor.AQUA + arenaName + ChatColor.GREEN + " was created!");
    }

    private void sendMessage(String msg){
        author.sendMessage(ChatColor.AQUA + "[S-CTF Creation] " + ChatColor.GREEN + msg);
    }

}

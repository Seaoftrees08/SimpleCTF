package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArenaManager {

    public static final String ARENA_LIST_PATH = "ArenaList";
    private static HashMap<String, String> creating = new HashMap<>();//PlayerName ArenaName
    private static HashMap<String, ArenaCreation> creatingArena = new HashMap<>();//arenaName, Arena
    private static HashMap<String, String> joined = new HashMap<>();//PlayerName ArenaName

    public ArenaManager(){}

    /**
     * アリーナの作成を行うステータスを付与する
     * これにより作成中に重複したりすることを防ぐ
     *
     * @param player 作成者
     * @param arenaName アリーナ名
     * @return 成功したかどうか(false->重複している)
     */
    public static boolean startCreation(Player player, String arenaName){
        if(creating.containsKey(player.getName()) || creating.containsValue(arenaName)){
            player.sendMessage(ChatColor.AQUA + "[S-CTF Creation] " + ChatColor.GREEN + "You began ArenaCreation so cannot start new creation.");
            return false;
        }else{
            creatingArena.put(arenaName, new ArenaCreation(player, arenaName));
            creating.put(player.getName(), arenaName);
            return true;
        }
    }

    /**
     * playerNameがアリーナを作成中か
     * @param playerName 検査するPlayerName
     * @return 作成中かどうか(作成中ならばtrue)
     */
    public static boolean isCreating(String playerName){
        return creating.containsKey(playerName);
    }

    public static void doCreationFlow(String playerName, ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!isCreating(playerName)) return;
        ArenaCreation ac = creatingArena.get(creating.get(playerName));
        ac.flow(acc, loc, inv);
    }

    /**
     * 作成終了時に実行
     * playerを作成ステータスから除外する
     * @param playerName 除外するplayerName
     */
    public static void finishCreation(String playerName){
        creating.remove(playerName);
    }

    /**
     * Arenaに参加した際に実行
     * 参加ステータスを付与する
     * @param player 付与するplayer
     * @param arenaName 参加するArena名
     * @return 参加に成功したかどうか(成功したらtrue)
     */
    public static boolean join(Player player, String arenaName){
        if(joined.containsKey(player.getName())) return false;
        joined.put(player.getName(), arenaName);
        return true;
    }

    /**
     * プレイヤーがアリーナに参加しているかどうか
     * @param playerName 検査するplayerName
     * @return 参加しているか（していればtrue）
     */
    public static boolean isJoined(String playerName){
        return joined.containsKey(playerName);
    }

    /**
     * Arenaの名前一覧をconfig.ymlから読み取って返す
     * @return ArenaNameの一覧
     */
    public static List<String> loadArenaNameList(){
        FileConfiguration fc = SimpleCTF.getSimpleCTF().getConfig();
        return fc.getStringList(ARENA_LIST_PATH);
    }

    /**
     * 新しいArenaを追加してconfigにセーブする
     * @param newArenaName 新しいArenaName
     */
    public static void saveArenaNameList(String newArenaName){
        FileConfiguration fc = SimpleCTF.getSimpleCTF().getConfig();
        List<String> lst = loadArenaNameList();
        lst.add(newArenaName);
        fc.set(ARENA_LIST_PATH, lst);
        SimpleCTF.getSimpleCTF().saveConfig();
    }

    /**
     * ArenaのListを取得する
     * @return ArenaのList
     */
    public static List<Arena> loadArenaList(){
        ArrayList<Arena> lst = new ArrayList<>();
        for(String name : loadArenaNameList()){
            lst.add(new Arena(name));
        }
        return lst;
    }

}

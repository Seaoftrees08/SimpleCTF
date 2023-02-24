package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArenaManager {

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
            return false;
        }else{
            creatingArena.put(arenaName, new ArenaCreation(player, arenaName));
            creating.put(player.getName(), arenaName);
            new ArenaCreation(player, arenaName);
            return true;
        }
    }

    public static boolean isCreating(String playerName){
        return creating.containsKey(playerName);
    }

    public static void doCreationFlow(String playerName, ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){
        if(isCreating(playerName)) return;
        ArenaCreation ac = creatingArena.get(creating.get(playerName));
        ac.flow(acc, loc, inv);
    }

    /**
     * 作成終了時に実行
     * @param playerName
     */
    public static void finishCreation(String playerName){
        creating.remove(playerName);
    }

    /**
     * Arenaに参加した際に実行
     * @param player
     * @param arenaName
     * @return
     */
    public static boolean join(Player player, String arenaName){
        if(joined.containsKey(player.getName())) return false;
        joined.put(player.getName(), arenaName);
        return true;
    }

    public static boolean isJoined(String playerName){
        return joined.containsKey(playerName);
    }

}

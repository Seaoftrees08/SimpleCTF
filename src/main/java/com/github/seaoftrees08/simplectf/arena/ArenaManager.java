package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ArenaManager {
    public static final String ARENA_LIST_PATH = "ArenaList";
    public static HashMap<String, Arena> playArena = new HashMap<>();
    public static HashMap<String, CreateArena> createArena = new HashMap<>();

    public static List<String> loadArenaNameList(){
        return SimpleCTF.getSimpleCTF().getConfig().getStringList(ARENA_LIST_PATH);
    }

    /**
     * アリーナが存在するかどうかを返す
     * これは作成中のアリーナも含めて判別する
     *
     * @param arenaName 検査するアリーナ名
     * @return 存在すればtrue
     */
    public static boolean existArena(String arenaName){
        return loadArenaNameList().contains(arenaName) || createArena.containsKey(arenaName);
    }

    // -------------------- creation ------------------- //

    /**
     * アリーナを作成する
     * この時、既に存在するアリーナ、もしくは作成中のアリーナ名が使われた場合falseを返し、アリーナ作成は開始されない
     * ただし、失敗した際のreplyはない
     *
     * @param arenaName 作成するアリーナ名
     * @param player 作成者(チャットを送るのに使用)
     */
    public static boolean create(String arenaName, Player player){
        if(existArena(arenaName)) return false;
        createArena.put(arenaName, new CreateArena(arenaName, player, true));
        return true;
    }

    /**
     * アリーナ作成後にcreateArenaから削除する
     * @param arenaName 作成を終了したアリーナ名
     */
    public static void finishCreation(String arenaName){
        createArena.remove(arenaName);
    }

}

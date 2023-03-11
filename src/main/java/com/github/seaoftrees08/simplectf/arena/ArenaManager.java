package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ArenaManager {
    public static final String ARENA_LIST_PATH = "ArenaList";
    public static final String INVALID_ARENA_NAME = "Invalid Arena";
    public static HashMap<String, Arena> playArena = new HashMap<>();//arenaName, CreateArena
    public static HashMap<String, CreateArena> createArena = new HashMap<>();//arenaName CreateArena

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
    public static boolean doCreation(String arenaName, Player player){
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

    /**
     * プレイヤーがアリーナを作成中かをかえす
     * @param playerName 検査するプレイヤー名
     * @return 作成中であればtrue
     */
    public static boolean isCreating(String playerName){
        return createArena.values().stream()
                .map(it -> it.author.getName())
                .toList()
                .contains(playerName);
    }

    /**
     * playerがアリーナ作成中の場合、そのアリーナ名を返す.
     * @param playerName 検査するプレイヤー名
     * @return 作成中のarena名. 存在しない場合`ArenaManager.INVALID_ARENA_NAME`が返る
     */
    public static String getBelongingCreateArenaName(String playerName){
        return createArena.values().stream()
                .filter(it -> it.author.getName().equals(playerName))
                .map(it -> it.arenaName)
                .findFirst()
                .orElse(INVALID_ARENA_NAME);
    }

    /**
     * 作成中アリーナの現在のPhaseを取得する
     *
     * @param arenaName 取得したい作成中のアリーナ名
     * @return 現在のPhase
     */
    public static ArenaPhase getCreateArenaPhase(String arenaName){
        if(!createArena.containsKey(arenaName)) return ArenaPhase.NONE;
        return createArena.get(arenaName).phase;
    }

    /**
     * アリーナ作成時に値を入力する際に使用する
     * ArenaPhaseが正確かどうかについては検査済みのものとする.
     *
     * @param arenaName 入力するアリーナ名
     * @param location 入力値となるLocation(Flagの位置, ArenaFieldの端)
     * @param player 入力値となるPlayer(SpawnPoint, Inventory)
     */
    public static void doCreateFlow(String arenaName, Location location, Player player){
        if(!createArena.containsKey(arenaName)) return;
        createArena.get(arenaName).flow(location, player);
    }

}

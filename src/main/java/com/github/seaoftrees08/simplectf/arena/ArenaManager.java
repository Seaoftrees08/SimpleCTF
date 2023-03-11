package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ArenaManager {
    public static final String ARENA_LIST_PATH = "ArenaList";
    public static final String INVALID_ARENA_NAME = "Invalid Arena";
    public static HashMap<String, PlayArena> playArena = new HashMap<>();//arenaName, PlayArena
    public static HashMap<String, CreateArena> createArena = new HashMap<>();//arenaName CreateArena

    public static List<String> loadArenaNameList(){
        return SimpleCTF.getSimpleCTF().getConfig().getStringList(ARENA_LIST_PATH);
    }

    public static boolean existPlayArena(String arenaName){
        return loadArenaNameList().contains(arenaName);
    }

    public static boolean enable(String arenaName){
        if(!existPlayArena(arenaName)) return false;
        return new Arena(arenaName).enable;
    }

    /**
     * アリーナが存在するかどうかを返す
     * これは作成中のアリーナも含めて判別する
     *
     * @param arenaName 検査するアリーナ名
     * @return 存在すればtrue
     */
    public static boolean existArenaIncludeCreating(String arenaName){
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
        if(existArenaIncludeCreating(arenaName)) return false;
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


    // -------------------- playing ------------------- //

    /**
     * プレイヤーがすでにほかのところに参加しているかどうかを検査する
     *
     * @param playerName 検査するプレイヤー名
     * @return すでに参加している場合はtrue
     */
    public static boolean alreadyJoin(String playerName){
        return !whereBelong(playerName).equals(ArenaManager.INVALID_ARENA_NAME);
    }

    /**
     * プレイヤーが参加しているアリーナ名を返す
     * どこにも参加していなければ`ArenaManager.INVALID_ARENA_NAME`を返す
     *
     * @param playerName 検査するプレイヤー名
     * @return 参加しているアリーナ名
     */
    public static String whereBelong(String playerName){
        return playArena.values().stream()
                .filter(arena -> arena.isJoined(playerName))
                .map(arena -> arena.arenaName)
                .findFirst()
                .orElse(ArenaManager.INVALID_ARENA_NAME);
    }


    /**
     * アリーナに参加する.
     * アリーナが存在するかどうか、プレイヤーがすでに他のアリーナに参加しているかどうかを判別し、メッセージを送るのもここで行う
     *
     * @param arenaName 参加するarena名
     * @param player 参加するPlayer
     */
    public static void join(String arenaName, Player player){
        if(!existPlayArena(arenaName)){
            sendMessage(player, "This arena is not exist.", ChatColor.RED);
            return;
        }

        if(alreadyJoin(player.getName())){
            sendMessage(player, "You already join " + whereBelong(player.getName()), ChatColor.RED);
            return;
        }

        //PlayArenaが作成されてなかった場合、作成
        if(!playArena.containsKey(arenaName)){
            playArena.put(arenaName, new PlayArena(arenaName));
        }

        playArena.get(arenaName).join(player);
        sendMessage(player, "You join Arena!.", ChatColor.GOLD);

    }

    /**
     * アリーナから退出する
     * プレイヤーがすでに他のアリーナに参加しているかどうかを判別し、メッセージを送るのもここで行う
     *
     * @param player 退出するプレイヤー
     */
    public static void leave(Player player){
        if(!alreadyJoin(player.getName())){
            sendMessage(player, "You are not join anywhere.", ChatColor.GRAY);
            return;
        }

        String arenaName = whereBelong(player.getName());
        playArena.get(arenaName).leave(player);
        sendMessage(player, "You leaved Arena.", ChatColor.GOLD);
    }

    // -------------------- other --------------------- //
    private static void sendMessage(Player player, String message, ChatColor cc) {
        player.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
    }

}

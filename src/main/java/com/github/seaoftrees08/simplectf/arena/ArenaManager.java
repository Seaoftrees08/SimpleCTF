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
        return !whereJoined(playerName).equals(ArenaManager.INVALID_ARENA_NAME);
    }

    /**
     * プレイヤーが参加しているアリーナ名を返す
     * これは観戦者も含める
     * どこにも参加していなければ`ArenaManager.INVALID_ARENA_NAME`を返す
     *
     * @param playerName 検査するプレイヤー名
     * @return 参加しているアリーナ名
     */
    public static String whereJoined(String playerName){
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
            sendMessage(player, "This arena does not exist.", ChatColor.RED);
            return;
        }

        if(alreadyJoin(player.getName())){
            sendMessage(player, "You already join " + whereJoined(player.getName()), ChatColor.RED);
            return;
        }

        PlayArena pa = new PlayArena(arenaName);
        if(!pa.enable){
            sendMessage(player, "This arena does disabled.", ChatColor.GRAY);
            return;
        }

        //PlayArenaが作成されてなかった場合、作成
        if(!playArena.containsKey(arenaName)){
            playArena.put(arenaName, pa);
        }

        playArena.get(arenaName).join(player);
        sendMessage(player, "You join Arena!", ChatColor.GOLD);

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

        String arenaName = whereJoined(player.getName());
        playArena.get(arenaName).leave(player);
        sendMessage(player, "You leaved Arena.", ChatColor.GOLD);
    }

    /**
     * アリーナに観戦者として参加する.
     * アリーナが存在するかどうか、プレイヤーがすでに他のアリーナに参加しているかどうかを判別し、メッセージを送るのもここで行う
     *
     * @param arenaName 参加するarena名
     * @param player 参加するPlayer
     */
    public static void joinSpectator(String arenaName, Player player){
        if(!existPlayArena(arenaName)){
            sendMessage(player, "This arena does not exist.", ChatColor.RED);
            return;
        }

        if(alreadyJoin(player.getName())){
            sendMessage(player, "You already join " + whereJoined(player.getName()), ChatColor.RED);
            return;
        }

        PlayArena pa = new PlayArena(arenaName);
        if(!pa.enable){
            sendMessage(player, "This arena does disabled.", ChatColor.GRAY);
            return;
        }

        //PlayArenaが作成されてなかった場合、作成
        if(!playArena.containsKey(arenaName)){
            playArena.put(arenaName, pa);
        }

        playArena.get(arenaName).joinSpectator(player);
        sendMessage(player, "You join Arena as Spectators!", ChatColor.BLUE);

    }

    /**
     * アリーナの観戦者から退出する
     * プレイヤーがすでに他のアリーナに参加しているかどうかを判別し、メッセージを送るのもここで行う
     *
     * @param player 退出するプレイヤー
     */
    public static void leaveSpectator(Player player){
        if(!alreadyJoin(player.getName())){
            sendMessage(player, "You are not join anywhere.", ChatColor.GRAY);
            return;
        }

        String arenaName = whereJoined(player.getName());
        playArena.get(arenaName).leaveSpectator(player);
        sendMessage(player, "You leaved Arena.", ChatColor.BLUE);
    }

    public static void removePlayArena(String arenaName){
        playArena.remove(arenaName);
    }

    /**
     * PlayArenaを取得する. ClockWork用
     *
     * @param arenaName 取得するアリーナ名
     * @return 該当するPlayArena. 無ければnullが返る
     */
    public static PlayArena getPlayArena(String arenaName){
        return playArena.getOrDefault(arenaName, null);
    }

    /**
     * アリーナのカウントダウンを強制10秒にする.
     *
     * @param arenaName 強制したいアリーナ名
     * @return 成功したかどうか(アリーナが存在しないなどで失敗するとfalseが返る)
     */
    public static boolean forceStart(String arenaName){
        if(!existPlayArena(arenaName)){
            return false;
        }
        PlayArena pa = playArena.get(arenaName);
        if(pa.phase.equals(ArenaPhase.WAITING) || pa.phase.equals(ArenaPhase.PLAYING)){
            pa.setTime(10);
            return true;
        }
        return false;
    }

    // -------------------- other --------------------- //
    private static void sendMessage(Player player, String message, ChatColor cc) {
        player.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
    }

}

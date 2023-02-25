package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.team.ArenaPlayer;
import com.github.seaoftrees08.simplectf.team.PlayerManager;
import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ArenaManager {

    public static final String ARENA_LIST_PATH = "ArenaList";
    private static HashMap<String, String> creating = new HashMap<>();//PlayerName ArenaName
    private static HashMap<String, ArenaCreation> creatingArena = new HashMap<>();//arenaName, Arena
    private static HashMap<String, PlayArena> playing = new HashMap<>();//arenaName, PlayArena

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
     * Arenaの名前一覧をconfig.ymlから読み取って返す
     * @return ArenaNameの一覧
     */
    public static List<String> loadArenaNameList(){
        return SimpleCTF.getSimpleCTF().getConfig().getStringList(ARENA_LIST_PATH);
    }

    /**
     * arenaNameに参加しているプレイヤーリストを返す
     * @param arenaName 検査するArena名
     * @return
     */
    public static List<String> joinedPlayerNameList(String arenaName){
        if(!loadArenaNameList().contains(arenaName)) return new ArrayList<>();
        return playing.get(arenaName).joinedPlayerNameList();
    }

    /**
     * playArenaのListを返す
     * @return playArenaのList
     */
    public static List<PlayArena> getPlayArenaList(){
        return playing.values().stream().toList();
    }

    /**
     * 指定されたarenaNameにplayerを参加させる
     * @param ap 参加させるPlayer
     * @param arenaName 対象のarenaName
     */
    public static void join(ArenaPlayer ap, String arenaName){
        if(!playing.containsKey(arenaName)) playing.put(arenaName, new PlayArena(arenaName));
        PlayArena playArena = playing.get(arenaName);
        playArena.join(ap);
        PlayerManager.sendNormalMessage(ap.player, "You joined Arena! (" + playArena.name + ")");

        if(playArena.canPlay()){
            //TODO:Start!
            System.out.println("START! START! START!");
        }
    }

    /**
     * playerNameをarenaNameから退場させる
     * この退場によってアリーナが誰もいなくなるようであればplayingから削除する
     * @param playerName 退場させるplayerName
     * @param arenaName 退場させるarenaName
     */
    public static void leave(String playerName, String arenaName){
        PlayArena pa = playing.get(arenaName);
        pa.leave(playerName);
        if(pa.joinedPlayerList().isEmpty()){
            playing.remove(arenaName);
            PlayerManager.sendNormalMessage(
                    Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(playerName)),
                    "You leaved Arena! (" + pa.name + ")");
        }
    }
}

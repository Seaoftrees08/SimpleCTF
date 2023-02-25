package com.github.seaoftrees08.simplectf.team;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlayerManager {

    public static final String NONE = "not join";
    public PlayerManager(){
    }

    /**
     * Arenaに参加した際に実行
     * 参加ステータスを付与する
     * @param player 付与するplayer
     * @param arenaName 参加するArena名
     * @return 参加に成功したかどうか(成功したらtrue)
     */
    public static boolean join(Player player, String arenaName){
        if(isJoined(player.getName())) return false;

        if(PlayerManager.isJoined(player.getName())){
            PlayerManager.sendNormalMessage(player, "You have already joined other arena.");
            return false;
        }else{
            ArenaManager.join(player, arenaName);
        }
        return true;
    }

    /**
     * プレイヤーがどこかのアリーナに参加しているかどうか
     * @param playerName 検査するplayerName
     * @return 参加しているか（していればtrue）
     */
    public static boolean isJoined(String playerName){
        return !whereJoined(playerName).equals(NONE);
    }

    /**
     * プレイヤーがどこのPlayArenaに参加しているかどうか
     * @param playerName 検査するplayerName
     * @return 参加しているarenaの名前
     */
    public static String whereJoined(String playerName){
        Optional<String> joinArenaName = ArenaManager.getPlayArenaList()
                .stream()
                .filter(pa -> pa.joinedPlayerNameList().contains(playerName))
                .map(pa -> pa.name)
                .findFirst();
        return joinArenaName.orElse(NONE);
    }

    /**
     * 通常のメッセージを送信する
     * @param player 送信する対象
     * @param message 内容
     */
    public static void sendNormalMessage(Player player, String message) {
        player.sendMessage(ChatColor.AQUA + "[S-CTF] " + ChatColor.GRAY + message);
    }

    /**
     * プレイヤーを退場させる
     * @param playerName 退場させるプレイヤー
     */
    public static void leave(String playerName){
        ArenaManager.leave(playerName, whereJoined(playerName));
    }
}

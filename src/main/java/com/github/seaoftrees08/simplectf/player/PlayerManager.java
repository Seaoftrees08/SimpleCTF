package com.github.seaoftrees08.simplectf.player;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Optional;

public class PlayerManager {

    public static ArrayList<Player> spectator = new ArrayList<>();
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
            initStatus(player);//PlayerのPotionEffectをリセット
            ArenaManager.join(new ArenaPlayer(player, arenaName), arenaName);//ArenaPlayer作成時にインベントリのバックアップを作成
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
     * 参加してなければ「not join」を返す
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

    /**
     * spectatorに入れる
     * @param p いれるPlayer
     * @param arenaName 入れるarena名
     */
    public static void joinSpectator(Player p, String arenaName){
        //Scoreboard忘れないこと
    }

    /**
     * spectatorから退出させる
     * @param p 退出させるPlayer
     * @param arenaName 退出させるarena名
     */
    public static void leaveSpectator(Player p, String arenaName){
        //Scoreboard忘れないこと
    }

    /**
     * ステータスを初期化します
     * @param p 初期化を行うPlayer
     */
    private static void initStatus(Player p){
        for(PotionEffect pe : p.getActivePotionEffects()) p.removePotionEffect(pe.getType());
        p.setHealth(20);
        p.setFoodLevel(20);
    }
}

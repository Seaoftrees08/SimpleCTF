package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.team.ArenaPlayer;
import com.github.seaoftrees08.simplectf.team.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PlayArena extends Arena{

    private final static String RED_TEAM = "Red Team";
    private final static String BLUE_TEAM = "Blue Team";
    private ArrayList<ArenaPlayer> redTeamMember = new ArrayList<>();
    private ArrayList<ArenaPlayer> blueTeamMember = new ArrayList<>();
    private Scoreboard scoreboard;
    private int redPoint = 0;
    private int bluePoint = 0;
    private int time= 0;

    public PlayArena(String name) {
        super(name);
    }

    /**
     * PlayArenaにプレイヤーを参加させる
     * 少ないほうのチームに参加をさせる.
     *
     * @param player 参加させるArenaPlayer
     */
    public void join(ArenaPlayer player){
        if(redTeamMember.size() < blueTeamMember.size()){
            redTeamMember.add(player);
            player.setGameInventory(redInv);
        }else{
            blueTeamMember.add(player);
            player.setGameInventory(blueInv);
        }
    }

    /**
     * PlayArenaに参加しているすべてのプレイヤー名を返す
     * @return プレイヤー名のリスト
     */
    public List<String> joinedPlayerNameList(){
        return joinedPlayerList().stream()
                .map(Player::getName)
                .toList();

    }

    /**
     * PlayArenaに参加しているすべてのプレイヤーを返す
     * @return プレイヤーのリスト
     */
    public List<Player> joinedPlayerList(){
        List<Player> list = new ArrayList<>(redTeamMember.stream().map(ap -> ap.player).toList());
        list.addAll(blueTeamMember.stream().map(ap -> ap.player).toList());
        return list;
    }

    /**
     * PlayArenaに参加しているすべてのArenaPlayerを返す
     * @return ArenaPlayerのList
     */
    public List<ArenaPlayer> joinedArenaPlayerList(){
        List<ArenaPlayer> list = new ArrayList<>(redTeamMember);
        list.addAll(blueTeamMember);
        return list;
    }

    /**
     * PlayerにgameInventoryを設定する
     * @param playerName インベントリを設定するPlayer名
     */
    public void applyGameInventory(String playerName){
        Optional<ArenaPlayer> oap = joinedArenaPlayerList().stream().filter(ap -> ap.player.getName().equals(playerName)).findFirst();
        if(oap.isPresent()){
            ArenaPlayer ap = oap.get();
            ap.applyToPlayerGameInventory();
        }
    }

    /**
     * プレイヤーがスポーンするとき
     * リスキル防止のため最初2秒は最強
     * @param playerName スポーンするプレイヤー名
     */
    public void whenSpawn(String playerName){
        applyGameInventory(playerName);
        Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(playerName))
                .addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 5));
        Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(playerName))
                .addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
        Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(playerName))
                .addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 40, 5));
        Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(playerName))
                .addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 5));
    }

    public void whenStartGame(){
        //announce
        broadcastInArena(ChatColor.GOLD + "Game Start!");
        broadcastInArena("300 seconds remaining time.");

        //Apply Items
        joinedPlayerNameList().forEach(this::whenSpawn);

        //setSpawn
        redTeamMember.forEach(ap -> ap.player.setBedSpawnLocation(redSpawn.getLocation()));
        blueTeamMember.forEach(ap -> ap.player.setBedSpawnLocation(blueSpawn.getLocation()));
    }

    /**
     * plauyerNameが参加しているかどうかを返す
     * @param playerName 検査するplayerName
     * @return 参加しているかどうか(していればtrue)
     */
    public boolean isJoined(String playerName){
        return joinedPlayerNameList().contains(playerName);
    }

    /**
     * red, blueの各チームにそれぞれ1人以上メンバーがいるかどうかを確認する
     * @return 各チームに1人以上プレイヤーがいればtrue
     */
    public boolean canPlay(){
        return !redTeamMember.isEmpty() && !blueTeamMember.isEmpty();
    }

    /**
     * プレイヤーを退場させる
     * @param playerName 退場させるプレイヤー名
     */
    public void leave(String playerName){
        ArrayList<ArenaPlayer> list = new ArrayList<>(redTeamMember);
        list.addAll(blueTeamMember);
        Optional<ArenaPlayer> oap = list.stream().filter(ap -> ap.player.getName().equals(playerName)).findFirst();
        if(oap.isPresent()){
            ArenaPlayer ap = oap.get();
            ap.restoreInventory(); //インベントリを戻す

            //スコアボード
            ap.player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            Objects.requireNonNull(scoreboard.getTeam(belongTeam(ap.player.getName()))).removeEntry(ap.player.getName());

        }

        redTeamMember.removeIf(p -> p.player.getName().equals(playerName));
        blueTeamMember.removeIf(p -> p.player.getName().equals(playerName));
    }

    public void waitScoreboard(){
        Scoreboard sb = initScoreboard();
        Objective obj = sb.getObjective(name);

        obj.getScore(ChatColor.GREEN + "  == CTF in " + name + " == ").setScore(200);
        obj.getScore(" ").setScore(199);
        obj.getScore(ChatColor.RED + " -- RED Team Member -- ").setScore(198);
        obj.getScore(" ").setScore(99);
        obj.getScore(ChatColor.BLUE + " -- BLUE Team Member -- ").setScore(98);//*/

        // RED_101~197,  BLUE_1~97
        int i = 101;
        for(ArenaPlayer ap : redTeamMember){
            obj.getScore(" " + ChatColor.RED + ap.player.getName()).setScore(i);
            i++;
        }
        i = 0;
        for(ArenaPlayer ap : blueTeamMember){
            obj.getScore(" " + ChatColor.BLUE + ap.player.getName()).setScore(i);
            i++;
        }

        applyScoreboard(sb);
    }

    public void playScoreBoard(){
        Scoreboard sb = initScoreboard();
        Objective obj = sb.getObjective(name);

        // RED_101~197,  BLUE_1~97
        obj.getScore(ChatColor.GREEN + "  == CTF in " + name + " == ").setScore(300);
        obj.getScore(" ").setScore(299);
        obj.getScore(ChatColor.AQUA + "remaining time: "+(time-100)*10).setScore(300);
        obj.getScore(" ").setScore(199);
        obj.getScore(ChatColor.RED + " -- RED Team Status -- ").setScore(198);
        obj.getScore(ChatColor.RED + " Score: " + ChatColor.WHITE + redPoint).setScore(197);
        obj.getScore(ChatColor.RED + " Join Players: " + ChatColor.WHITE + redTeamMember.size()).setScore(196);
        obj.getScore(" ").setScore(99);
        obj.getScore(ChatColor.BLUE + " -- BLUE Team Status -- ").setScore(98);
        obj.getScore(ChatColor.BLUE + " Score: " + ChatColor.WHITE + bluePoint).setScore(97);
        obj.getScore(ChatColor.BLUE + " Join Players: " + ChatColor.WHITE + blueTeamMember.size()).setScore(96);

        applyScoreboard(sb);
    }

    public void broadcastInArena(String msg){
        joinedPlayerList().forEach(p -> p.sendMessage(ChatColor.GREEN + "[S-CTF " + name + "] "
                + ChatColor.GREEN + msg));
    }

    private String belongTeam(String playerName){
        if(redTeamMember.stream().anyMatch(ap -> ap.player.getName().equals(playerName))){
            return RED_TEAM;
        }else if(blueTeamMember.stream().anyMatch(ap -> ap.player.getName().equals(playerName))){
            return BLUE_TEAM;
        }else{
            return "";
        }
    }

    private void applyScoreboard(Scoreboard sb){
        joinedPlayerList().forEach(p -> p.setScoreboard(sb));
        PlayerManager.spectator.forEach(p -> p.setScoreboard(sb));
        scoreboard = sb;
    }

    private Scoreboard initScoreboard(){
        Scoreboard sb = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        //red team
        Team redTeam = sb.registerNewTeam(RED_TEAM);
        redTeam.setAllowFriendlyFire(false);
        redTeam.setPrefix(ChatColor.RED+"");

        //blue team
        Team blueTeam = sb.registerNewTeam(BLUE_TEAM);
        blueTeam.setAllowFriendlyFire(false);
        blueTeam.setPrefix(ChatColor.BLUE+"");

        //Objective
        Objective obj = sb.registerNewObjective(name, "dummy", ChatColor.AQUA + name);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        //addPlayerList
        for(ArenaPlayer ap : redTeamMember){
            ap.player.getScoreboard().resetScores(ChatColor.RED + ap.player.getName());
            ap.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            Objects.requireNonNull(sb.getTeam(RED_TEAM)).addEntry(ap.player.getName());
        }
        for(ArenaPlayer ap : blueTeamMember){
            ap.player.getScoreboard().resetScores(ChatColor.BLUE + ap.player.getName());
            ap.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            Objects.requireNonNull(sb.getTeam(BLUE_TEAM)).addEntry(ap.player.getName());
        }

        return sb;
    }
}

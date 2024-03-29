package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.clockwork.Waiting;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayArena extends Arena{

    private final static double NEAR_DISTANCE = 1.8; //距離の2乗した値
    private int remTime = 0;
    protected ArenaTeam spectators;
    public PlayArena(String arenaName) {
        super(arenaName);
        spectators = new ArenaTeam(
                TeamColor.SPECTATOR,
                new StoredPlayerData(arenaField.getCentral().getLocation(redFlag.getLocation().getWorld()), GameMode.SPECTATOR)
        );
    }

    /**
     * プレイヤーが本アリーナに参加していない場合は、参加させる
     * このプレイヤーは他のアリーナには所属していないものとする.
     * ただし、本アリーナに所属しているかどうかはわからない.
     *
     * @param player 所属させるプレイヤー
     * @return すでに所属している -> false、どこにも所属せず正式に参加できた -> true
     */
    public boolean join(Player player){
        if(redTeam.isBelonging(player.getName()) || blueTeam.isBelonging(player.getName())
            || spectators.isBelonging(player.getName())) return false;

        //Player Init and join team
        if(redTeam.getArenaPlayerList().size() <= blueTeam.getArenaPlayerList().size()){
            redTeam.addMember(new ArenaPlayer(player));
        }else{
            blueTeam.addMember(new ArenaPlayer(player));
        }
        player.getInventory().clear();

        //scoreboard set
        if(phase.equals(ArenaPhase.PLAYING)) playScoreBoard();
        if(phase.equals(ArenaPhase.WAITING) || phase.equals(ArenaPhase.NONE) || phase.equals(ArenaPhase.FINISHED)) waitScoreboard();

        //gamemode set
        player.setGameMode(GameMode.ADVENTURE);

        //開始されてなければ開始
        if(redTeam.getArenaPlayerList().size()>=1 && blueTeam.getArenaPlayerList().size()>=1
                && (phase.equals(ArenaPhase.NONE) || phase.equals(ArenaPhase.FINISHED))){
            phase = ArenaPhase.WAITING;
            //0tick後から、60秒間、20tickごとに実行するタイマー
            new Waiting(60, arenaName).runTaskTimer(SimpleCTF.getSimpleCTF(), 20, 20);
        }

        //既に開始されている場合
        if(phase.equals(ArenaPhase.PLAYING)){
            whenSpawn(player);
        }

        return true;
    }

    /**
     * このアリーナからプレイヤーを退場させる
     * このプレイヤーが本アリーナに所属していない場合`null`が返される.
     * インベントリの修復等もここで行われる
     * @param player 退場させるプレイヤー
     * @return 退場させたArenaPlayer (本アリーナに所属していない場合`null`)
     */
    public ArenaPlayer leave(Player player){
        if(!redTeam.isBelonging(player.getName()) && !blueTeam.isBelonging(player.getName())
                && !spectators.isBelonging(player.getName())) return null;

        ArenaPlayer arenaPlayer = redTeam.removeMember(player.getName());
        if(arenaPlayer == null) arenaPlayer = blueTeam.removeMember(player.getName());
        if(arenaPlayer == null) return null;

        //Inventory 修復
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.teleport(arenaPlayer.getLocationStringList().getLocation());
        arenaPlayer.setInventory(player);

        //scorebord set
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

        //gamemode
        arenaPlayer.applyGameMode(player);

        //Arenaの事後処理
        if(redTeam.getArenaPlayerList().isEmpty() && blueTeam.getArenaPlayerList().isEmpty() && spectators.getArenaPlayerList().isEmpty()){
            //flag kill
            redFlag.kill();
            blueFlag.kill();

            //DropItemRemove
            Objects.requireNonNull(redFlag.getCampLocation().getWorld())
                    .getEntities()
                    .stream()
                    .filter(en -> en.getType().equals(EntityType.DROPPED_ITEM) || en.getType().equals(EntityType.ARMOR_STAND))
                    .filter(en -> arenaField.contain(en.getLocation()))
                    .toList().forEach(Entity::remove);

            //Arena remove from memory
            ArenaManager.removePlayArena(arenaName);
        }

        return arenaPlayer;
    }

    /**
     * プレイヤーが本アリーナに観戦者として参加していない場合は、参加させる
     * このプレイヤーは他のアリーナには所属していないものとする.
     * ただし、本アリーナに所属しているかどうかはわからない.
     *
     * @param player 観戦者として参加させるプレイヤー
     * @return 成功したらtrue
     */
    public boolean joinSpectator(Player player){
        if(redTeam.isBelonging(player.getName()) || blueTeam.isBelonging(player.getName())
                || spectators.isBelonging(player.getName())) return false;

        //Player Init and join team
        spectators.addMember(new ArenaPlayer(player));
        player.getInventory().clear();

        //gamemode set
        spectators.getStoredPlayerData().applyGameMode(player);

        //teleport
        player.teleport(spectators.getStoredPlayerData().getLocationStringList().getLocation());

        return true;
    }

    /**
     * このアリーナからプレイヤーを退場させる
     * このプレイヤーが本アリーナに所属していない場合`null`が返される.
     * インベントリの修復等もここで行われる
     * @param player 退場させるプレイヤー
     * @return 退場させたArenaPlayer (本アリーナに所属していない場合`null`)
     */
    public ArenaPlayer leaveSpectator(Player player){
        if(!redTeam.isBelonging(player.getName()) && !blueTeam.isBelonging(player.getName())
                && !spectators.isBelonging(player.getName())) return null;

        ArenaPlayer arenaPlayer = spectators.removeMember(player.getName());
        if(arenaPlayer == null) return null;

        //Inventory 修復
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.teleport(arenaPlayer.getLocationStringList().getLocation());
        arenaPlayer.setInventory(player);

        //scorebord set
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

        //gamemode
        arenaPlayer.applyGameMode(player);

        //Arenaの事後処理
        if(redTeam.getArenaPlayerList().isEmpty() && blueTeam.getArenaPlayerList().isEmpty() && spectators.getArenaPlayerList().isEmpty()){
            //flag kill
            redFlag.kill();
            blueFlag.kill();

            //DropItemRemove
            Objects.requireNonNull(redFlag.getCampLocation().getWorld())
                    .getEntities()
                    .stream()
                    .filter(en -> en.getType().equals(EntityType.DROPPED_ITEM) || en.getType().equals(EntityType.ARMOR_STAND))
                    .filter(en -> arenaField.contain(en.getLocation()))
                    .toList().forEach(Entity::remove);

            //Arena remove from memory
            ArenaManager.removePlayArena(arenaName);
        }

        return arenaPlayer;
    }

    public void teleportSpectator(Player player){
        player.teleport(spectators.getStoredPlayerData().getLocationStringList().getLocation());
    }

    public void setTime(int time){ this.remTime = time; }

    public int getTime(){ return remTime; }

    public void degreesTime(){
        remTime--;
    }

    public void setPhase(ArenaPhase phase){ this.phase = phase; }

    public Flag getRedFlag(){
        return redFlag;
    }

    public Flag getBlueFlag(){
        return blueFlag;
    }

    public boolean inArena(Location loc){ return arenaField.contain(loc); }

    public boolean canPlay(){
        return enable && redTeam.getArenaPlayerList().size()>0 && blueTeam.getArenaPlayerList().size()>0;
    }
    public ArenaPhase getPhase(){ return phase; }

    public TeamColor getPlayerTeamColor(String playerName){
        if(redTeam.isBelonging(playerName)) return TeamColor.RED;
        if(blueTeam.isBelonging(playerName)) return TeamColor.BLUE;
        if(spectators.isBelonging(playerName)) return TeamColor.SPECTATOR;
        return TeamColor.NONE;
    }

    /**
     * 青旗を納品して、赤チームが点数を取得
     */
    public void takePointRed(){
        if(phase.equals(ArenaPhase.PLAYING)){
            redTeam.increaseScore();
            broadcastInArena(ChatColor.RED + "Red Team" + ChatColor.GREEN + " get one Point!");
            spawnBlueFlagAtBase(true);
        }
        if(redTeam.getScore()>=3){ setTime(1); }
    }

    /**
     * 赤旗を納品して、青チームが点数を取得
     */
    public void takePointBlue(){
        if(phase.equals(ArenaPhase.PLAYING)){
            blueTeam.increaseScore();
            broadcastInArena(ChatColor.BLUE + "Blue Team" + ChatColor.GREEN + " get one Point!");
            spawnRedFlagAtBase(false);
        }
        if(blueTeam.getScore()>=3){ setTime(1); }
    }

    /**
     * 青旗を落とした時に呼びだされる
     * @param p 落としたプレイヤー
     * @param item 落としたアイテム
     */
    public void dropBlueFlag(Player p, Item item){
        if(item==null){
            item = p.getWorld().dropItemNaturally(p.getLocation(), Flag.getBlueFlagItemStack());
        }
        broadcastInArena(ChatColor.BLUE + "BLUE Flag" + ChatColor.GREEN + " is Dropped!");
        blueFlag.drop(item);
        blueFlag.onGroundedTime = -1;
    }

    /**
     * 赤旗を落とした時に呼びだされる
     * @param p 落としたプレイヤー
     * @param item 落としたアイテム
     */
    public void dropRedFlag(Player p, Item item){
        if(item==null){
            item = p.getWorld().dropItemNaturally(p.getLocation(), Flag.getRedFlagItemStack());
        }
        broadcastInArena(ChatColor.RED + "Red Flag" + ChatColor.GREEN + " is Dropped!");
        redFlag.drop(item);
        redFlag.onGroundedTime = -1;
    }
    /**
     * 旗を拾った時に呼びだされる
     * @param p 拾ったプレイヤー
     */
    public void pickupRedFlag(Player p){
        redFlag.pickUp(p);
        redFlag.onGroundedTime = 0;
        broadcastInArena(ChatColor.BLUE + p.getName() + ChatColor.GREEN + " pick up RED FLAG!");
    }

    /**
     * 旗を拾った時に呼びだされる
     * @param p 拾ったプレイヤー
     */
    public void pickupBlueFlag(Player p){
        blueFlag.pickUp(p);
        blueFlag.onGroundedTime = 0;
        broadcastInArena(ChatColor.RED + p.getName() + ChatColor.GREEN + " pick up BLUE FLAG!");
    }

    /**
     * 赤旗をキャンプに設置する
     *
     * @param first 最初の呼び出しか(最初の呼び出しでなければbroadcastする)
     */
    public void spawnRedFlagAtBase(boolean first){
        redFlag.spawnCamp();
        redFlag.onGroundedTime = 0;
        if(!first) broadcastInArena(ChatColor.RED + "RED FLAG" + ChatColor.GREEN + " is returned base.");
    }

    /**
     * 青旗をキャンプに設置する
     *
     * @param first 最初の呼び出しか(最初の呼び出しでなければbroadcastする)
     */
    public void spawnBlueFlagAtBase(boolean first){
        blueFlag.spawnCamp();
        blueFlag.onGroundedTime = 0;
        if(!first) broadcastInArena(ChatColor.BLUE + "BLUE FLAG" + ChatColor.GREEN + " is returned base.");
    }

    /**
     * Flagのパーティクルを出す
     */
    public void spawnFlagParticle(){
        //赤旗
        Location l = redFlag.getLocation();
        Objects.requireNonNull(l.getWorld()).playEffect(l, Effect.MOBSPAWNER_FLAMES, 1, 100);

        //青旗
        l = blueFlag.getLocation();
        Objects.requireNonNull(l.getWorld()).playEffect(l, Effect.MOBSPAWNER_FLAMES, 1, 100);
    }

    /**
     * red flagのフェンスとの距離を計測し、近ければTrueを返す
     * @param location 検査するLocation
     * @return 近いかどうか(近ければtrue)
     */
    public boolean nearRedFlagFence(Location location){
        Location camp = redFlag.getCampLocation();
        return Objects.requireNonNull(location.getWorld()).getName().equals(Objects.requireNonNull(camp.getWorld()).getName())
                && location.distanceSquared(camp) < NEAR_DISTANCE;
    }

    /**
     * blue flagのフェンスとの距離を計測し、近ければTrueを返す
     * @param location 検査するLocation
     * @return 近いかどうか(近ければtrue)
     */
    public boolean nearBlueFlagFence(Location location){
        Location camp = blueFlag.getCampLocation();
        return Objects.requireNonNull(location.getWorld()).getName().equals(Objects.requireNonNull(camp.getWorld()).getName())
                && location.distanceSquared(camp) < NEAR_DISTANCE;
    }

    /**
     * 最初のスポーン、死んだときに呼び出される.
     * インベントリの設定、ゲームモードの設定、テレポ、最初のリスキル対策エフェクトをやってくれる
     * @param player 適応するプレイヤー
     */
    public void whenSpawn(Player player){
        //スポーン, 除外設定はこの中でやってくれている
        redTeam.spawnPlayer(player);
        blueTeam.spawnPlayer(player);

        //リスキル対策
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 40, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 5));
    }

    /**
     * アリーナが始まるときに呼ばれるところ.
     * アリーナの戦場を初期化する
     */
    public void whenStartGame(){
        //announce
        broadcastInArena(ChatColor.GOLD + "Game Start!");
        broadcastInArena("300 seconds remaining time.");

        //ApplyItems and Gamemode and teleport
        joinedAllPlayerList().forEach(this::whenSpawn);

        //setSpawnは効かないのでそのまま.(PlayerListenerで対応)

        //setArenaPhase
        phase = ArenaPhase.PLAYING;

        //setFlag
        spawnRedFlagAtBase(true);
        spawnBlueFlagAtBase(true);
    }

    /**
     * アリーナが終わるときに呼ばれるところ
     */
    public void whenFinish(){
        //announce
        broadcastInArena(ChatColor.GOLD + "Game Finished!");
        if(redTeam.getScore() == blueTeam.getScore()){
            broadcastInArena("Game result: " + ChatColor.LIGHT_PURPLE + "DRAW");
        }else if(redTeam.getScore() > blueTeam.getScore()){
            broadcastInArena("Game result: " + ChatColor.RED + "Red Team" + ChatColor.GREEN + " Win!");
        }else{
            broadcastInArena("Game result: " + ChatColor.BLUE + "Blue Team" + ChatColor.GREEN + " Win!");
        }

        //status
        phase = ArenaPhase.FINISHED;

        //player leave
        joinedAllPlayerList().forEach(ArenaManager::leave);
        spectators.getArenaPlayerList().forEach(ap -> ArenaManager.leaveSpectator(ap.player));

        //フィールドの初期化
        //flag kill
        redFlag.kill();
        blueFlag.kill();

        //DropItemRemove
        Objects.requireNonNull(redFlag.getCampLocation().getWorld())
                .getEntities()
                .stream()
                .filter(en -> en.getType().equals(EntityType.DROPPED_ITEM) || en.getType().equals(EntityType.ARMOR_STAND))
                .filter(en -> arenaField.contain(en.getLocation()))
                .toList().forEach(Entity::remove);

        //Arena remove from memory
        if(redTeam.getArenaPlayerList().isEmpty() && blueTeam.getArenaPlayerList().isEmpty() && spectators.getArenaPlayerList().isEmpty()){
            ArenaManager.removePlayArena(arenaName);
        }

    }

    /**
     * アリーナに所属しているプレイヤー一覧を返す
     * @return アリーナに所属しているプレイヤー一覧
     */
    public List<Player> joinedAllPlayerList(){
        ArrayList<Player> playerList = new ArrayList<>(redTeam.getArenaPlayerList().stream().map(it -> it.player).toList());
        playerList.addAll( blueTeam.getArenaPlayerList().stream().map(it -> it.player).toList() );
        return playerList;
    }

    /**
     * プレイヤーがこのアリーナに所属しているかどうかを返す
     *
     * @param playerName 検査するプレイヤー名
     * @return 所属していればtrue
     */
    public boolean isJoined(String playerName){
        return joinedAllPlayerList().stream()
                .map(Player::getName)
                .toList()
                .contains(playerName)
                || spectators.getArenaPlayerList().stream()
                .map(ap -> ap.player.getName())
                .toList()
                .contains(playerName);
    }

    public Location getRedRespawnLocation(){
        return redTeam.getStoredPlayerData().getLocationStringList().getLocation();
    }

    public Location getBlueRespawnLocation(){
        return blueTeam.getStoredPlayerData().getLocationStringList().getLocation();
    }


    public void broadcastInArena(String msg){
        joinedAllPlayerList().forEach(p -> p.sendMessage(ChatColor.GREEN + "[S-CTF " + arenaName + "] "
                + ChatColor.GREEN + msg));
    }

    public void broadcastRedTeam(String msg){
        joinedAllPlayerList().forEach(p -> p.sendMessage(ChatColor.RED + "[S-CTF " + arenaName + " RED] "
                + ChatColor.GREEN + msg));
    }

    public void broadcastBlueTeam(String msg){
        joinedAllPlayerList().forEach(p -> p.sendMessage(ChatColor.BLUE + "[S-CTF " + arenaName + " BLUE] "
                + ChatColor.GREEN + msg));
    }

    public void waitScoreboard(){
        Scoreboard sb = initScoreboard();
        Objective obj = sb.getObjective(arenaName);

        obj.getScore(ChatColor.GREEN + "  == CTF in " + arenaName + " == ").setScore(201);
        obj.getScore(" ").setScore(200);
        obj.getScore(ChatColor.AQUA + "remaining time: "+remTime).setScore(199);
        obj.getScore(" ").setScore(198);
        obj.getScore(ChatColor.RED + " -- RED Team Member -- ").setScore(197);
        obj.getScore(" ").setScore(99);
        obj.getScore(ChatColor.BLUE + " -- BLUE Team Member -- ").setScore(98);//*/

        // RED_101~197,  BLUE_1~97
        int i = 101;
        for(ArenaPlayer ap : redTeam.getArenaPlayerList()){
            obj.getScore(" " + ChatColor.RED + ap.player.getName()).setScore(i);
            i++;
        }
        i = 0;
        for(ArenaPlayer ap : blueTeam.getArenaPlayerList()){
            obj.getScore(" " + ChatColor.BLUE + ap.player.getName()).setScore(i);
            i++;
        }

        applyScoreboard(sb);
    }

    public void playScoreBoard(){
        Scoreboard sb = initScoreboard();
        Objective obj = sb.getObjective(arenaName);

        // RED_101~197,  BLUE_1~97
        obj.getScore(ChatColor.GREEN + "  == CTF in " + arenaName + " == ").setScore(301);
        obj.getScore(" ").setScore(300);
        obj.getScore(ChatColor.AQUA + "remaining time: "+remTime).setScore(299);
        obj.getScore(" ").setScore(199);
        obj.getScore(ChatColor.RED + " -- RED Team Status -- ").setScore(198);
        obj.getScore(ChatColor.RED + " Score: " + ChatColor.WHITE + redTeam.getScore()).setScore(197);
        obj.getScore(ChatColor.RED + " Join Players: " + ChatColor.WHITE + redTeam.getArenaPlayerList().size()).setScore(196);
        obj.getScore(" ").setScore(99);
        obj.getScore(ChatColor.BLUE + " -- BLUE Team Status -- ").setScore(98);
        obj.getScore(ChatColor.BLUE + " Score: " + ChatColor.WHITE + blueTeam.getScore()).setScore(97);
        obj.getScore(ChatColor.BLUE + " Join Players: " + ChatColor.WHITE + blueTeam.getArenaPlayerList().size()).setScore(96);

        applyScoreboard(sb);
    }

    private void applyScoreboard(Scoreboard sb){
        joinedAllPlayerList().forEach(p -> p.setScoreboard(sb));
        spectators.getArenaPlayerList().forEach(ap -> ap.player.setScoreboard(sb));
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
        Objective obj = sb.registerNewObjective(arenaName, "dummy", ChatColor.AQUA + arenaName);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        //addPlayerList
        for(ArenaPlayer ap : this.redTeam.getArenaPlayerList()){
            ap.player.getScoreboard().resetScores(ChatColor.RED + ap.player.getName());
            ap.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            Objects.requireNonNull(sb.getTeam(RED_TEAM)).addEntry(ap.player.getName());
        }
        for(ArenaPlayer ap : this.blueTeam.getArenaPlayerList()){
            ap.player.getScoreboard().resetScores(ChatColor.BLUE + ap.player.getName());
            ap.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            Objects.requireNonNull(sb.getTeam(BLUE_TEAM)).addEntry(ap.player.getName());
        }

        return sb;
    }
}

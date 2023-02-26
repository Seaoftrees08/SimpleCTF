package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.clockwork.Waiting;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.flag.FlagItem;
import com.github.seaoftrees08.simplectf.flag.FlagStatus;
import com.github.seaoftrees08.simplectf.player.ArenaPlayer;
import com.github.seaoftrees08.simplectf.player.PlayerManager;
import com.github.seaoftrees08.simplectf.player.TeamColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
    private final ArrayList<ArenaPlayer> redTeamMember = new ArrayList<>();
    private final ArrayList<ArenaPlayer> blueTeamMember = new ArrayList<>();
    private Scoreboard scoreboard;
    private int redPoint = 0;
    private int bluePoint = 0;
    private int time= 0;
    private ArenaStatus status = ArenaStatus.NONE;
    private final Flag redFlag;
    private final Flag blueFlag;

//    private FlagStatus redFlagStatus = FlagStatus.CAMP;
//    private FlagStatus blueFlagStatus = FlagStatus.CAMP;
//    private int redFlagOnGround = 0; //-1 -> PlayerListener等でonGroundを感知、0->camp、n->地面に落ちた時のremTime
//    private int blueFlagOnGround = 0;

    public PlayArena(String name) {
        super(name);

        //redFlag
        redFlag = new Flag(
                new FlagItem(
                        redFlagFence.getLocation(redSpawn.getLocation().getWorld()),
                        "Red Flag",
                        Flag.getRedFlagItemStack()
                ),
                redFlagFence.getLocation(redSpawn.getLocation().getWorld())
        );

        //blueFlag
        blueFlag = new Flag(
                new FlagItem(
                        blueFlagFence.getLocation(blueSpawn.getLocation().getWorld()),
                        "Blue Flag",
                        Flag.getBlueFlagItemStack()
                ),
                blueFlagFence.getLocation(blueSpawn.getLocation().getWorld())
        );
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

    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
    }

    public void degreesTime(){ time--; }

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

    public void startWaiting(){
        //0tick後から、60秒間、20tickごとに実行するタイマー
        new Waiting(60, name).runTaskTimer(SimpleCTF.getSimpleCTF(), 0, 20);
    }

    public Location getRedRespawnLocation(){
        return redSpawn.getLocation();
    }

    public Location getBlueRespawnLocation(){
        return blueSpawn.getLocation();
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

        //setArenaStatus
        setArenaStatus(ArenaStatus.PLAYING);

        //setFlag
        spawnRedFlagAtBase(true);
        spawnBlueFlagAtBase(true);

        //gamemode
        joinedPlayerList().forEach(p -> p.setGameMode(GameMode.ADVENTURE));

        //teleport
        redTeamMember.forEach(ap -> ap.player.teleport(redSpawn.getLocation()));
        blueTeamMember.forEach(ap -> ap.player.teleport(blueSpawn.getLocation()));

    }

    public void whenFinish(){
        setArenaStatus(ArenaStatus.FINISHED);
        //announce
        broadcastInArena(ChatColor.GOLD + "Game Finished!");
        if(redPoint == bluePoint){
            broadcastInArena("Game result: " + ChatColor.LIGHT_PURPLE + "DRAW");
        }else if(redPoint > bluePoint){
            broadcastInArena("Game result: " + ChatColor.RED + "Red Team" + ChatColor.GREEN + " Win!");
        }else{
            broadcastInArena("Game result: " + ChatColor.BLUE + "Blue Team" + ChatColor.GREEN + " Win!");
        }


        joinedPlayerNameList().forEach(PlayerManager::leave);

        //kill flag
        redFlag.kill();
        blueFlag.kill();

        //DropItemRemove
        Objects.requireNonNull(redSpawn.getLocation()
                .getWorld())
                .getEntities()
                .stream()
                .filter(en -> en.getType().equals(EntityType.DROPPED_ITEM) || en.getType().equals(EntityType.ARMOR_STAND))
                .filter(en -> isInArena(en.getLocation()))
                .toList().forEach(Entity::remove);

    }

    public boolean isInArena(Location loc){
        double x = Math.min(firstPoint.x, secondPoint.x);
        double dx = Math.max(firstPoint.x, secondPoint.x);
        double y = Math.min(firstPoint.y, secondPoint.y);
        double dy = Math.max(firstPoint.y, secondPoint.y);
        double z = Math.min(firstPoint.z, secondPoint.z);
        double dz = Math.max(firstPoint.z, secondPoint.z);

        return x <= loc.getX() && loc.getX() <= dx
                && y <= loc.getY() && loc.getY() <= dy
                && z <= loc.getZ() && loc.getZ() <= dz;
    }

    public ArenaStatus getArenaStatus(){
        return status;
    }

    public void setArenaStatus(ArenaStatus status){
        this.status = status;
    }

    public FlagStatus getRedFlagStatus(){
        return redFlag.status;
    }

    public FlagStatus getBlueFlagStatus(){
        return blueFlag.status;
    }

    /**
     * Playingからの呼び出し。旗の落ちてる時間制御に使う
     * @param remTime 地面に落ちた時の時間
     */
    public void setRedFlagOnGroundTime(int remTime){
        blueFlag.onGroundedTime = remTime;
    }

    /**
     * 地面に落ちた時の時間を取得する Playingで使う
     * @return 地面に落ちた時の時間
     */
    public int getRedFlagOnGroundTime(){
        return redFlag.onGroundedTime;
    }

    /**
     * Playingからの呼び出し。旗の落ちてる時間制御に使う
     * @param remTime 地面に落ちた時の時間
     */
    public void setBlueFlagOnGroundTime(int remTime){
        redFlag.onGroundedTime = remTime;
    }

    /**
     * 地面に落ちた時の時間を取得する Playingで使う
     * @return 地面に落ちた時の時間
     */
    public int getBlueFlagOnGroundTime(){
        return blueFlag.onGroundedTime;
    }

    /**
     * 旗を落とした時に呼びだされる
     * @param p 落としたプレイヤー
     */
    public void dropRedFlag(Player p, Item item){
        if(item==null){
            item = p.getWorld().dropItemNaturally(p.getLocation(), Flag.getRedFlagItemStack());
        }
        broadcastInArena(ChatColor.RED + "Red Flag" + ChatColor.GREEN + " is Dropped!");
        redFlag.drop(item);
        setRedFlagOnGroundTime(-1);
    }

    /**
     * 旗を落とした時に呼びだされる
     * @param p 落としたプレイヤー
     */
    public void dropBlueFlag(Player p, Item item){
        if(item==null){
            item = p.getWorld().dropItemNaturally(p.getLocation(), Flag.getBlueFlagItemStack());
        }
        broadcastInArena(ChatColor.BLUE + "BLUE Flag" + ChatColor.GREEN + " is Dropped!");
        blueFlag.drop(item);
        setBlueFlagOnGroundTime(-1);
    }

    /**
     * 旗を拾った時に呼びだされる
     * @param p 拾ったプレイヤー
     */
    public void pickupRedFlag(Player p){
        redFlag.pickUp(p);
        setRedFlagOnGroundTime(0);
        broadcastInArena(ChatColor.BLUE + p.getName() + ChatColor.GREEN + " pick up RED FLAG!");
    }

    /**
     * 旗を拾った時に呼びだされる
     * @param p 拾ったプレイヤー
     */
    public void pickupBlueFlag(Player p){
        blueFlag.pickUp(p);
        setBlueFlagOnGroundTime(0);
        broadcastInArena(ChatColor.RED + p.getName() + ChatColor.GREEN + " pick up BLUE FLAG!");
    }

    public void spawnFlagParticle(){
        //赤旗
        Location l = redFlag.getLocation();
        Objects.requireNonNull(l.getWorld()).playEffect(l, Effect.MOBSPAWNER_FLAMES, 1, 100);

        //青旗
        l = blueFlag.getLocation();
        Objects.requireNonNull(l.getWorld()).playEffect(l, Effect.MOBSPAWNER_FLAMES, 1, 100);
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
            if(scoreboard!=null) Objects.requireNonNull(scoreboard.getTeam(getBelongTeam(ap.player.getName()))).removeEntry(ap.player.getName());

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
        obj.getScore(ChatColor.AQUA + "remaining time: "+time).setScore(300);
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

    /**
     * 旗をキャンプに設置する時に呼びだされる
     */
    public void spawnRedFlagAtBase(boolean first){
        redFlag.spawnCamp();
        setRedFlagOnGroundTime(0);
        if(!first) broadcastInArena(ChatColor.RED + "RED FLAG" + ChatColor.GREEN + " is returned base.");
    }

    /**
     * 旗をキャンプに設置する時に呼びだされる
     */
    public void spawnBlueFlagAtBase(boolean first){
        blueFlag.spawnCamp();
        setBlueFlagOnGroundTime(0);
        if(!first) broadcastInArena(ChatColor.BLUE + "BLUE FLAG" + ChatColor.GREEN + " is returned base.");
    }

    /**
     * red flagのフェンスとの距離を計測し、近ければTrueを返す
     * @param location 検査するLocation
     * @return 近いかどうか(近ければtrue)
     */
    public boolean nearRedFlagFence(Location location){
        Location camp = redFlag.getCampLocation();
        return Objects.requireNonNull(location.getWorld()).getName().equals(Objects.requireNonNull(camp.getWorld()).getName())
                && location.distanceSquared(camp) < 1.5;
    }

    /**
     * blue flagのフェンスとの距離を計測し、近ければTrueを返す
     * @param location 検査するLocation
     * @return 近いかどうか(近ければtrue)
     */
    public boolean nearBlueFlagFence(Location location){
        Location camp = blueFlag.getCampLocation();
        return Objects.requireNonNull(location.getWorld()).getName().equals(Objects.requireNonNull(camp.getWorld()).getName())
                && location.distanceSquared(camp) < 1.5;
    }

    /**
     * 青旗を納品して、赤チームが点数を取得
     */
    public void takePointRed(){
        if(getArenaStatus().equals(ArenaStatus.PLAYING)){
            redPoint++;
            broadcastInArena(ChatColor.RED + "Red Team" + ChatColor.GREEN + " get one Point!");
            spawnBlueFlagAtBase(true);
        }
        if(redPoint>=3){ setTime(1); }
    }

    /**
     * 赤旗を納品して、青チームが点数を取得
     */
    public void takePointBlue(){
        if(getArenaStatus().equals(ArenaStatus.PLAYING)){
            bluePoint++;
            broadcastInArena(ChatColor.BLUE + "Blue Team" + ChatColor.GREEN + " get one Point!");
            spawnRedFlagAtBase(false);
        }
        if(bluePoint>=3){ setTime(1); }
    }

    public void broadcastInArena(String msg){
        joinedPlayerList().forEach(p -> p.sendMessage(ChatColor.GREEN + "[S-CTF " + name + "] "
                + ChatColor.GREEN + msg));
    }

    public void broadcastRedTeam(String msg){
        joinedPlayerList().forEach(p -> p.sendMessage(ChatColor.RED + "[S-CTF " + name + " RED] "
                + ChatColor.GREEN + msg));
    }

    public void broadcastBlueTeam(String msg){
        joinedPlayerList().forEach(p -> p.sendMessage(ChatColor.BLUE + "[S-CTF " + name + " BLUE] "
                + ChatColor.GREEN + msg));
    }

    /**
     * プレイヤーが所属しているチームカラーを返す
     * どこにも所属していない場合はTeamColor.NONEが返る
     * @param playerName 検査するPlayer名
     * @return 所属しているTeamColor
     */
    public TeamColor getPlayerTeamColor(String playerName){
        if(redTeamMember.stream().anyMatch(ap -> ap.player.getName().equals(playerName))) return TeamColor.RED;
        if(blueTeamMember.stream().anyMatch(ap -> ap.player.getName().equals(playerName))) return TeamColor.BLUE;
        return TeamColor.NONE;
    }

    public boolean hasRedFlag(String playerName){
        return redFlag.hasFlag(playerName);
    }

    public boolean hasBlueFlag(String playerName){
        return blueFlag.hasFlag(playerName);
    }

    private String getBelongTeam(String playerName){
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

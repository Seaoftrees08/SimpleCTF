package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class PlayArena extends Arena{

    private Scoreboard scoreboard;
    private int remTime = 0;
    protected ArenaTeam spectators = new ArenaTeam(TeamColor.SPECTATOR, new StoredPlayerData());
    public PlayArena(String arenaName) {
        super(arenaName);
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
        if(redTeam.isBelonging(player.getName()) || blueTeam.isBelonging(player.getName())) return false;

        if(redTeam.getArenaPlayerList().size() < blueTeam.getArenaPlayerList().size()){
            redTeam.addMember(new ArenaPlayer(player));
        }else{
            blueTeam.addMember(new ArenaPlayer(player));
        }
        player.getInventory().clear();

        if(redTeam.getArenaPlayerList().size()>=1 && blueTeam.getArenaPlayerList().size()>=1
                && (phase.equals(ArenaPhase.NONE) || phase.equals(ArenaPhase.FINISHED))){
            //TODO: clock work
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
        if(redTeam.isBelonging(player.getName()) || blueTeam.isBelonging(player.getName())) return null;

        ArenaPlayer arenaPlayer = redTeam.removeMember(player.getName());
        if(arenaPlayer == null) arenaPlayer = blueTeam.removeMember(player.getName());
        if(arenaPlayer == null) return null;

        arenaPlayer.setInventory(player);
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.teleport(arenaPlayer.getLocationStringList().getLocation());

        //TODO:arenaに誰もいない場合削除


        return arenaPlayer;
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
     * @param playerName 検査するプレイヤー名
     * @return 所属していればtrue
     */
    public boolean isJoined(String playerName){
        return joinedAllPlayerList().stream()
                .map(Player::getName)
                .toList()
                .contains(playerName);
    }

    /**
     * アリーナのプレイ後のアイテム削除等をここで行う
     */
    public void cleanArena(){

    }
}

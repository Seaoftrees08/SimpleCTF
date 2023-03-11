package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * ArenaのTeamに関するData Class
 * get setがメインで、それに伴う操作はArenaにて行う
 */
public class ArenaTeam {

    final private HashMap<String, ArenaPlayer> teamMember = new HashMap<>();// playerName, ArenaPlayer
    final private StoredPlayerData teamData;
    private int score;
    final public TeamColor teamColor;

    public ArenaTeam(TeamColor tc, StoredPlayerData teamData){
        teamColor = tc;
        this.teamData = teamData;
    }

    /**
     * メンバーを追加する.
     * このメンバーは本チームおよびほかのチーム、ほかのアリーナのチームに属していないことを前提とする
     * @param ap 追加するメンバー
     */
    public void addMember(ArenaPlayer ap){
        teamMember.put(ap.player.getName(), ap);
    }

    public void setScore(int value){
        score = value;
    }

    public int getScore(){ return score; }

    /**
     * メンバーを削除する.
     * これによって発生するインベントリの修復等はここでは行わない
     * @param playerName 削除するプレイヤー名
     * @return arenaPlayer 削除したArenaPlayer
     */
    public ArenaPlayer removeMember(String playerName){
        return teamMember.remove(playerName);
    }

    /**
     * メンバー一覧を出力する
     * @return メンバーのリスト
     */
    public List<ArenaPlayer> getArenaPlayerList(){
        return teamMember.values().stream().toList();
    }

    public boolean isBelonging(String playerName){
        return teamMember.containsKey(playerName);
    }

    public StoredPlayerData getStoredPlayerData(){
        return teamData;
    }

    /**
     * 最初や、死んだときにスポーンする際に呼び出される.
     * このチームに所属していない場合何もしない
     *
     * @param player 初期化するプレイヤー
     */
    public void spawnPlayer(Player player){
        if(!isBelonging(player.getName())) return;

        teamData.applyGameMode(player);
        teamData.setInventory(player);
        player.teleport(teamData.getLocationStringList().getLocation());

    }
}

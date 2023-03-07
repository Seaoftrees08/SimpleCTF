package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;

import java.util.HashMap;
import java.util.List;

/**
 * ArenaのTeamに関するData Class
 * get setがメインで、それに伴う操作はArenaにて行う
 */
public class ArenaTeam {

    final private HashMap<String, ArenaPlayer> teamMember = new HashMap<>();// playerName, ArenaPlayer
    final private StoredPlayerData teamItems;
    final public TeamColor teamColor;

    public ArenaTeam(TeamColor tc, StoredPlayerData teamItems){
        teamColor = tc;
        this.teamItems = teamItems;
    }

    /**
     * メンバーを追加する.
     * このメンバーは本チームおよびほかのチーム、ほかのアリーナのチームに属していないことを前提とする
     * @param ap 追加するメンバー
     */
    public void addMember(ArenaPlayer ap){
        //TODO
    }

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
        return (List<ArenaPlayer>) teamMember.values();
    }

    public boolean isBelonging(String playerName){
        return teamMember.containsKey(playerName);
    }
}

package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.Cuboid;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class Arena {
    
    public final String arenaName;
    protected final Flag redFlag;
    protected final Flag blueFlag;
    protected Cuboid arenaField;
    protected ArenaTeam redTeam;
    protected ArenaTeam blueTeam;
    protected Scoreboard scoreboard;
    protected int remTime;
    protected ArenaPhase phase;
    protected boolean enable;
    protected ArenaTeam spectators;

    /**
     * CreateArenaにて使われるコンストラクタ
     * 区別のためにbooleanの値をとっている
     * falseの場合は`Arena(String arenaName)`と同じ挙動をする
     * @param uniqueName
     * @param isCreation
     */
    protected Arena(String uniqueName, boolean isCreation){
        arenaName = uniqueName;
        //TODO?
    }

    /**
     * アリーナを作成、読み込む
     * これはArenaManagerから呼ばれるもので、config上で存在するアリーナ名のみが使われることを前提とする
     * @param arenaName
     */
    public Arena(String arenaName){
        this.arenaName = arenaName;
        loadArena();
    }

    /**
     * アリーナを読み込む
     * フィールドの`arenaName`はすでにsetされており、これを用いてロードを行う
     */
    private void loadArena(){
        //TODO
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
        //TODO
        return false;
    }

    /**
     * このアリーナからプレイヤーを退場させる
     * このプレイヤーが本アリーナに所属していない場合`null`が返される.
     * @param playerName 退場させるプレイヤー名
     * @return 退場させたArenaPlayer (本アリーナに所属していない場合`null`)
     */
    public ArenaPlayer leave(String playerName){
        ArenaPlayer player = redTeam.removeMember(playerName);
        if(player == null) player = blueTeam.removeMember(playerName);
        if(player == null) return null;
        //TODO
        return player;
    }


}

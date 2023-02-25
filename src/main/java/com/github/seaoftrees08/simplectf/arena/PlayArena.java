package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.team.ArenaPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayArena extends Arena{

    private ArrayList<ArenaPlayer> redTeamMember = new ArrayList<>();
    private ArrayList<ArenaPlayer> blueTeamMember = new ArrayList<>();

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
        redTeamMember.removeIf(p -> p.player.getName().equals(playerName));
        blueTeamMember.removeIf(p -> p.player.getName().equals(playerName));
    }
}

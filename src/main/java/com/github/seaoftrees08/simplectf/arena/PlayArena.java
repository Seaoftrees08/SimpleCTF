package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.team.PlayerManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayArena extends Arena{

    private ArrayList<Player> redTeamMember = new ArrayList<>();
    private ArrayList<Player> blueTeamMemeber = new ArrayList<>();

    public PlayArena(String name) {
        super(name);
    }

    /**
     * PlayArenaにプレイヤーを参加させる
     * 少ないほうのチームに参加をさせる.
     *
     * @param player 参加させるPlayer
     */
    public void join(Player player){
        if(redTeamMember.size() < blueTeamMemeber.size()){
            redTeamMember.add(player);
        }else{
            blueTeamMemeber.add(player);
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
        List<Player> list = new ArrayList<>(List.copyOf(redTeamMember));
        list.addAll(List.copyOf(blueTeamMemeber));
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
        return !redTeamMember.isEmpty() && !blueTeamMemeber.isEmpty();
    }

    /**
     * プレイヤーを退場させる
     * @param playerName 退場させるプレイヤー名
     */
    public void leave(String playerName){
        redTeamMember.removeIf(p -> p.getName().equals(playerName));
        blueTeamMemeber.removeIf(p -> p.getName().equals(playerName));
    }
}

package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArenaManager {

    private static HashMap<String, String> creating = new HashMap<>();//PlayerName ArenaName
    private static HashMap<String, ArenaCreation> creatingArena = new HashMap<>();//arenaName, Arena
    private static HashMap<String, String> joined = new HashMap<>();//PlayerName ArenaName

    public ArenaManager(){}

    /**
     * �A���[�i�̍쐬���s���X�e�[�^�X��t�^����
     * ����ɂ��쐬���ɏd�������肷�邱�Ƃ�h��
     *
     * @param player �쐬��
     * @param arenaName �A���[�i��
     * @return �����������ǂ���(false->�d�����Ă���)
     */
    public static boolean startCreation(Player player, String arenaName){
        if(creating.containsKey(player.getName()) || creating.containsValue(arenaName)){
            return false;
        }else{
            creatingArena.put(arenaName, new ArenaCreation(player, arenaName));
            creating.put(player.getName(), arenaName);
            new ArenaCreation(player, arenaName);
            return true;
        }
    }

    public static boolean isCreating(String playerName){
        return creating.containsKey(playerName);
    }

    public static void doCreationFlow(String playerName, ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){
        if(isCreating(playerName)) return;
        ArenaCreation ac = creatingArena.get(creating.get(playerName));
        ac.flow(acc, loc, inv);
    }

    /**
     * �쐬�I�����Ɏ��s
     * @param playerName
     */
    public static void finishCreation(String playerName){
        creating.remove(playerName);
    }

    /**
     * Arena�ɎQ�������ۂɎ��s
     * @param player
     * @param arenaName
     * @return
     */
    public static boolean join(Player player, String arenaName){
        if(joined.containsKey(player.getName())) return false;
        joined.put(player.getName(), arenaName);
        return true;
    }

    public static boolean isJoined(String playerName){
        return joined.containsKey(playerName);
    }

}

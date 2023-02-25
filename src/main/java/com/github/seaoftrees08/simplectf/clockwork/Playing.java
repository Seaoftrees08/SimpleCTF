package com.github.seaoftrees08.simplectf.clockwork;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.github.seaoftrees08.simplectf.team.PlayerManager;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Playing extends BukkitRunnable {

    int remTime;
    String arenaName;

    public Playing(int gamePlaySecond, String arenaName){
        Preconditions.checkArgument(gamePlaySecond <= 0, "[Simple-CTF Error] invalid waitSecond in Playing.java");
        remTime = gamePlaySecond;
        this.arenaName = arenaName;
    }
    @Override
    public void run() {
        //check
        PlayArena arena = ArenaManager.getPlayArena(arenaName);
        if(!arena.canPlay()){
            arena.broadcastInArena("Cancelled due to lack of capacity.");
            arena.joinedPlayerNameList().forEach(PlayerManager::leave);
            this.cancel();
        }
        if(!arena.isEnable()){
            arena.broadcastInArena("Administrator made this arena disabled.");
            arena.joinedPlayerNameList().forEach(PlayerManager::leave);
            this.cancel();
        }
        if(remTime < 0){
            arena.broadcastInArena(ChatColor.RED + "Waiting Error!");
            arena.joinedPlayerNameList().forEach(PlayerManager::leave);
            this.cancel();
        }

        //finish
        if(remTime==0){
            //TODO:FINISH
        }

        //Countdown
        if(remTime>60 && remTime%30 == 0){
            arena.broadcastInArena(arenaName + "will start in" + ChatColor.GOLD + remTime + ChatColor.GREEN + "Seconds!");
        }else if(remTime<=60 && remTime%10 == 0){

        }
        remTime--;

        //ScoreBoard
        arena.waitScoreboard();
    }
}

package com.github.seaoftrees08.simplectf.clockwork;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.flag.FlagStatus;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Playing extends BukkitRunnable {

    String arenaName;

    public Playing(int gamePlaySecond, String arenaName){
        Preconditions.checkArgument(gamePlaySecond > 0, "[Simple-CTF Error] invalid waitSecond in Playing.java");
        this.arenaName = arenaName;
        PlayArena arena = ArenaManager.getPlayArena(arenaName);
        arena.setTime(gamePlaySecond);
    }
    @Override
    public void run() {
        //check
        PlayArena arena = ArenaManager.getPlayArena(arenaName);
        int remTime = arena.getTime();
        if(!arena.canPlay()){
            arena.broadcastInArena(ChatColor.LIGHT_PURPLE + "Cancelled due to lack of capacity or Administrator made this arena disabled.");
            arena.joinedAllPlayerList().forEach(ArenaManager::leave);
            this.cancel();
        }
        if(remTime < 0){
            arena.broadcastInArena(ChatColor.RED + "Playing Error!");
            arena.joinedAllPlayerList().forEach(ArenaManager::leave);
            this.cancel();
        }

        //finish
        if(remTime==0){
            arena.whenFinish();
            this.cancel();
        }

        //red flagの時間制御(OnGroundの感知)
        Flag redFlag = arena.getRedFlag();
        if(redFlag.onGroundedTime < 0){
            if(redFlag.status.equals(FlagStatus.GROUND)){
                redFlag.onGroundedTime = remTime;
            }else{
                redFlag.onGroundedTime = 0;
            }

        }

        //blue flagの時間制御(OnGroundの感知)
        Flag blueFlag = arena.getBlueFlag();
        if(blueFlag.onGroundedTime < 0){
            if(blueFlag.status.equals(FlagStatus.GROUND)){
                blueFlag.onGroundedTime = remTime;
            }else{
                blueFlag.onGroundedTime = 0;
            }
        }

        //flagの時間制御(OnGroundの時間超過でのCamp)
        if(redFlag.onGroundedTime > 0 && redFlag.onGroundedTime-remTime > 30){
            arena.spawnRedFlagAtBase(false);
        }
        if(blueFlag.onGroundedTime > 0 && blueFlag.onGroundedTime-remTime > 30){
            arena.spawnBlueFlagAtBase(false);
        }

        //flagの時間制御(Particle)
        if(remTime%3 == 0){
            arena.spawnFlagParticle();
        }


        //Countdown
        if(remTime>60 && remTime%30 == 0){
            arena.broadcastInArena(ChatColor.GOLD + "" + remTime + ChatColor.GREEN + " Seconds to game end!");
        }else if(remTime<=60 && remTime%10 == 0 && remTime!=0){
            arena.broadcastInArena(ChatColor.GOLD + "" + remTime + ChatColor.GREEN + " Seconds to game end!");
        }
        arena.degreesTime();//remTime--

        //ScoreBoard
        arena.playScoreBoard();
    }
}

package com.github.seaoftrees08.simplectf.clockwork;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.github.seaoftrees08.simplectf.flag.FlagStatus;
import com.github.seaoftrees08.simplectf.player.PlayerManager;
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
            arena.broadcastInArena(ChatColor.LIGHT_PURPLE + "Cancelled due to lack of capacity.");
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
            System.out.println("----------- FINISH! FINISH! FINISH! ----------");
            this.cancel();
        }

        //flagの時間制御(OnGroundの感知)
        if(arena.getRedFlagOnGroundTime()<0){
            if(arena.getRedFlagStatus().equals(FlagStatus.GROUND)){
                arena.setRedFlagOnGroundTime(remTime);
            }else{
                arena.setRedFlagOnGroundTime(0);
            }

        }
        if(arena.getBlueFlagOnGroundTime()<0){
            if(arena.getBlueFlagStatus().equals(FlagStatus.GROUND)){
                arena.setBlueFlagOnGroundTime(remTime);
            }else{
                arena.setBlueFlagOnGroundTime(0);
            }

        }

        //flagの時間制御(OnGroundの時間超過でのCamp)
        if(arena.getRedFlagOnGroundTime()>0 && arena.getRedFlagOnGroundTime()-remTime > 30){
            arena.spawnRedFlagAtBase(false);
        }
        if(arena.getBlueFlagOnGroundTime()>0 && arena.getBlueFlagOnGroundTime()-remTime > 30){
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
        arena.waitScoreboard();
    }
}

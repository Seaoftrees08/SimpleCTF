package com.github.seaoftrees08.simplectf.clockwork;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.ArenaPhase;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

public class Waiting extends BukkitRunnable {
    String arenaName;

    public Waiting(int waitSeconds, String arenaName){
        Preconditions.checkArgument(waitSeconds > 0, "[Simple-CTF Error] invalid waitSecond in Waiting.java, giveSeconds:" + waitSeconds);
        this.arenaName = arenaName;
        PlayArena arena = ArenaManager.getPlayArena(arenaName);
        arena.setTime(waitSeconds);
    }

    @Override
    public void run() {
        //check
        PlayArena arena = ArenaManager.getPlayArena(arenaName);
        int remTime = arena.getTime();
        if(!arena.canPlay()){
            arena.broadcastInArena(ChatColor.LIGHT_PURPLE + "Cancelled due to lack of capacity or Administrator made this arena disabled.");
            this.cancel();
            arena.setPhase(ArenaPhase.FINISHED);
        }
        if(remTime < 0){
            arena.broadcastInArena(ChatColor.RED + "Waiting Error!");
            this.cancel();
        }

        //start
        if(remTime==0){
            arena.whenStartGame();

            System.out.println("Start! Start! Start!");
            arena.broadcastInArena(ChatColor.RED + "STAAAAAAAAAAT!");

            new Playing(300, arenaName).runTaskTimer(SimpleCTF.getSimpleCTF(), 0, 20);
            this.cancel();
        }

        //Countdown
        if(remTime%10 == 0 && remTime!=0){
            arena.broadcastInArena(ChatColor.AQUA + arenaName
                    + ChatColor.GREEN + " will start in " + ChatColor.GOLD + remTime + ChatColor.GREEN + " Seconds!");
        }
        arena.degreesTime();//remTime--

        //ScoreBoard
        arena.waitScoreboard();

    }
}

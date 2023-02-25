package com.github.seaoftrees08.simplectf.clockwork;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 最初の待ち時間カウントダウン
 */
public class Waiting extends BukkitRunnable {

    int remTime;
    String arenaName;

    public Waiting(int waitSeconds, String arenaName){
        Preconditions.checkArgument(waitSeconds <= 0, "[Simple-CTF Error] invalid waitSecond in Waiting.java");
        remTime = waitSeconds;
        this.arenaName = arenaName;
    }

    @Override
    public void run() {
        //check
        PlayArena arena = ArenaManager.getPlayArena(arenaName);
        if(!arena.canPlay()){
            arena.broadcastInArena("Cancelled due to lack of capacity.");
            this.cancel();
        }
        if(!arena.isEnable()){
            arena.broadcastInArena("Administrator made this arena disabled.");
            this.cancel();
        }
        if(remTime < 0){
            arena.broadcastInArena(ChatColor.RED + "Waiting Error!");
            this.cancel();
        }

        //start
        if(remTime==0){
            arena.whenStartGame();
            new Playing(300, arenaName).runTaskTimer(SimpleCTF.getSimpleCTF(), 0, 20);
        }

        //Countdown
        if(remTime%10 == 0){
            arena.broadcastInArena(arenaName + "will start in" + ChatColor.GOLD + remTime + ChatColor.GREEN + "Seconds!");
        }
        remTime--;

        //ScoreBoard
        arena.waitScoreboard();

    }
}

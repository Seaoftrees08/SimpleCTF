package com.github.seaoftrees08.simplectf.arena;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaCreation {

    private final Player author;
    private CreationPhase cp;

    public ArenaCreation(Player player){
        author = player;
        cp = CreationPhase.SET_FIRST_POINT;
        flow();
    }

    /**
     * “ü—Í‚ª•¡”‚ ‚é‚ªA‚Æ‚è‚ ‚¦‚¸”]Ž€‚Å‚±‚ê‚ð‘–‚ç‚¹‚Æ‚¯‚Î‚¢‚¢flow
     *
     */
    private void flow(){
        switch (cp){
            case SET_FIRST_POINT -> setFirstPoint();
            case SET_SECOND_POINT -> setSecondPoint();
            case SET_RED_FLAG -> setRedFlag();
            case SET_BLUE_FLAG -> setBlueFlag();
            case SET_RED_SPAWN -> setRedSpawn();
            case SET_BLUE_SPAWN -> setBlueSpawn();
            case SET_RETURN_POINT -> setReturnPoint();
        }
    }

    private void setFirstPoint(){
        author.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
        sendMessage("Arena-Creation has 7 phase.");
        sendMessage("1st: left punch first arena edge with BLAZE_ROD plz.");
        cp = CreationPhase.SET_SECOND_POINT;
    }

    private void setSecondPoint(){

    }

    private void setRedFlag(){

    }

    private void setBlueFlag(){

    }

    private void setRedSpawn(){

    }

    private void setBlueSpawn(){

    }

    private void setReturnPoint(){

    }

    private void sendMessage(String msg){
        author.sendMessage(ChatColor.AQUA + "[S-CTF Creation] " + ChatColor.GREEN + msg);
    }

}

enum CreationPhase{
    SET_FIRST_POINT,
    SET_SECOND_POINT,
    SET_RED_FLAG,
    SET_BLUE_FLAG,
    SET_RED_SPAWN,
    SET_BLUE_SPAWN,
    SET_RETURN_POINT
}
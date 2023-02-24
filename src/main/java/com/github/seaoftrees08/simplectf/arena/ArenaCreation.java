package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaCreation {

    private final Player author;
    private CreationPhase cp;
    private Arena arena;

    public ArenaCreation(Player player, String arenaName){
        author = player;
        cp = CreationPhase.STARTED;
        arena = new Arena(arenaName);
        flow(ArenaCreationCause.FIRST, new Vec3i(), null);
    }

    /**
     * “ü—Í‚ª•¡”‚ ‚é‚ªA‚Æ‚è‚ ‚¦‚¸”]Ž€‚Å‚±‚ê‚ð‘–‚ç‚¹‚Æ‚¯‚Î‚¢‚¢flow
     * @param acc ”­¶Œ¹
     */
    public void flow(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){
        switch (cp){
            case STARTED -> started(acc, loc, inv);
            case SET_FIRST_POINT -> setFirstPoint(acc, loc, inv);
            case SET_SECOND_POINT -> setSecondPoint(acc, loc, inv);
            case SET_RED_FLAG -> setRedFlag(acc, loc, inv);
            case SET_BLUE_FLAG -> setBlueFlag(acc, loc, inv);
            case SET_RED_SPAWN -> setRedSpawn(acc, loc, inv);
            case SET_BLUE_SPAWN -> setBlueSpawn(acc, loc, inv);
            case SET_RETURN_POINT -> setReturnPoint(acc, loc, inv);
        }
    }

    private void started(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.FIRST)) return;
        author.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
        sendMessage("Arena-Creation has 7 phase.");
        sendMessage("1st: left punch first arena edge with BLAZE_ROD plz.");
        cp = CreationPhase.SET_FIRST_POINT;
    }

    private void setFirstPoint(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.EVENT)) return;
        
    }

    private void setSecondPoint(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){

    }

    private void setRedFlag(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){

    }

    private void setBlueFlag(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){

    }

    private void setRedSpawn(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){

    }

    private void setBlueSpawn(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){

    }

    private void setReturnPoint(ArenaCreationCause acc, Vec3i loc, PlayerInventoryItems inv){

    }

    private void sendMessage(String msg){
        author.sendMessage(ChatColor.AQUA + "[S-CTF Creation] " + ChatColor.GREEN + msg);
    }

}

enum CreationPhase{
    STARTED,
    SET_FIRST_POINT,
    SET_SECOND_POINT,
    SET_RED_FLAG,
    SET_BLUE_FLAG,
    SET_RED_SPAWN,
    SET_BLUE_SPAWN,
    SET_RETURN_POINT
}
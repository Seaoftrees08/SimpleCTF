package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.Vec3i;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
        flow(ArenaCreationCause.FIRST, null, null);
    }

    /**
     * 入力が複数あるが、とりあえず脳死でこれを走らせとけばいいflow
     * @param acc 発生源
     * @param loc 場所の設定に使う
     * @param inv Inventoryの設定に使う
     */
    public void flow(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        switch (cp){
            case STARTED -> started(acc, loc, inv);
            case SET_FIRST_POINT -> setFirstPoint(acc, loc, inv);
            case SET_SECOND_POINT -> setSecondPoint(acc, loc, inv);
            case SET_RED_FLAG -> setRedFlag(acc, loc, inv);
            case SET_BLUE_FLAG -> setBlueFlag(acc, loc, inv);
            case SET_RED_SPAWN -> setRedSpawn(acc, loc, inv);
            case SET_BLUE_SPAWN -> setBlueSpawn(acc, loc, inv);
            case SET_RETURN_POINT -> setReturnPoint(acc, loc, inv);
            case SET_RED_INVENTORY -> setRedInventory(acc, loc, inv);
            case SET_BLUE_INVENTORY -> setBlueInventory(acc, loc, inv);
        }
    }

    private void started(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.FIRST)) return;
        author.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
        sendMessage("Arena-Creation has 9 phase.");
        sendMessage("1st: left punch first arena edge with BLAZE_ROD plz.");
        cp = CreationPhase.SET_FIRST_POINT;
    }

    private void setFirstPoint(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.EVENT)) return;
        arena.setFirstPoint(new Vec3i(loc));
        sendMessage("2nd: left punch second arena edge with BLAZE_ROD plz.");
        cp = CreationPhase.SET_SECOND_POINT;
    }

    private void setSecondPoint(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.EVENT)) return;
        arena.setSecondPoint(new Vec3i(loc));
        sendMessage("3rd: left punch " + ChatColor.RED + "Red Flag fence" + ChatColor.GREEN + "with BLAZE_ROD plz.");
        cp = CreationPhase.SET_RED_FLAG;
    }

    private void setRedFlag(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.EVENT)) return;
        arena.setRedFlag(new Vec3i(loc));
        sendMessage("4rd: left punch " + ChatColor.BLUE + "Blue Flag fence" + ChatColor.GREEN + " with BLAZE_ROD plz.");
        cp = CreationPhase.SET_BLUE_FLAG;
    }

    private void setBlueFlag(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.EVENT)) return;
        arena.setBlueFlag(new Vec3i(loc));
        sendMessage("5th: set " + ChatColor.RED + "Red Spawn Block" + ChatColor.GREEN + " with command.");
        sendMessage("Please type /sctf admin setLoc");
        cp = CreationPhase.SET_RED_SPAWN;
    }

    private void setRedSpawn(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.COMMAND)) return;
        arena.setRedSpawn(loc);
        sendMessage("6th: set " + ChatColor.BLUE + "Blue Spawn Block" + ChatColor.GREEN + " with command.");
        sendMessage("Please type /sctf admin setLoc");
        cp = CreationPhase.SET_BLUE_SPAWN;
    }

    private void setBlueSpawn(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.COMMAND)) return;
        arena.setBlueSpawn(loc);
        sendMessage("7th: set " + ChatColor.LIGHT_PURPLE + "Return Point" + ChatColor.GREEN + " with command.");
        sendMessage("Please type /sctf admin setLoc");
        cp = CreationPhase.SET_RETURN_POINT;
    }

    private void setReturnPoint(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.COMMAND)) return;
        arena.setReturnPoint(loc);
        sendMessage("8th: set " + ChatColor.RED + "Red Team Inventory" + ChatColor.GREEN + " with command.");
        sendMessage("Please type /sctf admin setInv");
        cp = CreationPhase.SET_RED_INVENTORY;
    }

    private void setRedInventory(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.COMMAND)) return;
        arena.setRedInv(inv);
        sendMessage("9th: set " + ChatColor.BLUE + "Blue Team Inventory" + ChatColor.GREEN + " with command.");
        sendMessage("Please type /sctf admin setInv");
        cp = CreationPhase.SET_BLUE_INVENTORY;
    }

    private void setBlueInventory(ArenaCreationCause acc, Location loc, PlayerInventoryItems inv){
        if(!acc.equals(ArenaCreationCause.COMMAND)) return;
        arena.setBlueInv(inv);
        sendMessage("Finish arena creation! Arena " + ChatColor.AQUA + arena.name + ChatColor.GREEN + " was created!");
        arena.save();
        ArenaManager.finishCreation(author.getName());
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
    SET_RETURN_POINT,
    SET_RED_INVENTORY,
    SET_BLUE_INVENTORY
}
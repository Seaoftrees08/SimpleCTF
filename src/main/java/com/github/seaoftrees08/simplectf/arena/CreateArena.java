package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.utils.Cuboid;
import com.github.seaoftrees08.simplectf.utils.StoredPlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateArena extends Arena{
    public CreateArena(String uniqueName, Player player) {
        super(uniqueName, true);
        phase = ArenaPhase.FIRST_POINT_SETTING;
        player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
        //TODO: reply
    }

    public void flow(Location loc, Player player){
        switch (phase){
            case FIRST_POINT_SETTING -> setFirstPoint(loc);
            case SECOND_POINT_SETTING -> setSecondPoint(loc);
            case RED_FLAG_SETTING -> setRedFlagFence(loc);
            case BLUE_FLAG_SETTING -> setBlueFlagFence(loc);
            case RED_TEAM_DATA_SETTING -> setRedSpawnAndInventory(player);
            case BLUE_TEAM_DATA_SETTING -> setBlueSPawnAndInventory(player);
            default -> {}
        }
    }

    public void setFirstPoint(Location loc){
        arenaField = new Cuboid(loc);
        phase = ArenaPhase.SECOND_POINT_SETTING;
        //TODO: reply
    }

    public void setSecondPoint(Location loc){
        arenaField.setSecond(loc);
        phase = ArenaPhase.RED_FLAG_SETTING;
        //TODO: reply
    }

    public void setRedFlagFence(Location loc){
        redFlag = new Flag(TeamColor.RED, loc);
        phase = ArenaPhase.BLUE_FLAG_SETTING;
        //TODO: reply
    }

    public void setBlueFlagFence(Location loc){
        blueFlag = new Flag(TeamColor.BLUE, loc);
        phase = ArenaPhase.RED_TEAM_DATA_SETTING;
        //TODO: reply
    }

    public void setRedSpawnAndInventory(Player player){
        StoredPlayerData spd = new StoredPlayerData(player.getInventory(), player.getLocation());
        redTeam = new ArenaTeam(TeamColor.RED, spd);
        phase = ArenaPhase.BLUE_TEAM_DATA_SETTING;
        //TODO: reply
    }

    public void setBlueSPawnAndInventory(Player player){
        StoredPlayerData spd = new StoredPlayerData(player.getInventory(), player.getLocation());
        blueTeam = new ArenaTeam(TeamColor.BLUE, spd);
        phase = ArenaPhase.FINISHED;
        //TODO: reply
    }

    public void save(){

    }

}

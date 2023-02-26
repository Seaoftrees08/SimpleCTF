package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ArenaLocation extends ArrayList<String> {
    private Location loc;
    public ArenaLocation(Location loc){
        this.loc = loc;
        add(String.valueOf(loc.getX()));        //0
        add(String.valueOf(loc.getY()));        //1
        add(String.valueOf(loc.getZ()));        //2
        add(String.valueOf(loc.getYaw()));      //3
        add(String.valueOf(loc.getPitch()));    //4
        add(loc.getWorld().getName());          //5
    }

    public ArenaLocation(List<String> locationStringList){
        if(locationStringList == null || locationStringList.size()!=6){
            loc = null;
        }else{
            addAll(locationStringList);
            loc = new Location(
                    SimpleCTF.getSimpleCTF().getServer().getWorld(locationStringList.get(5)),
                    Double.parseDouble(locationStringList.get(0)),
                    Double.parseDouble(locationStringList.get(1)),
                    Double.parseDouble(locationStringList.get(2)),
                    Float.parseFloat(locationStringList.get(3)),
                    Float.parseFloat(locationStringList.get(4))
                );
        }
    }

    public Location getLocation(){
        return loc;
    }
    public List<String> getStringList(){ return this; }
}

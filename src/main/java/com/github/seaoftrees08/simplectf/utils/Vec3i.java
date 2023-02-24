package com.github.seaoftrees08.simplectf.utils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vec3i {

    public int x = 0;
    public int y = 0;
    public int z = 0;

    public Vec3i(){
    }
    public Vec3i(Location loc){
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }

    public Vec3i(List<Integer> lst){
        if(lst != null || lst.size()==3){
            x = lst.get(0);
            y = lst.get(1);
            z = lst.get(2);
        }
    }

    public List<Integer> getList(){
        return Arrays.asList(x, y, z);
    }

}

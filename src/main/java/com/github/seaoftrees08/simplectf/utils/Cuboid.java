package com.github.seaoftrees08.simplectf.utils;

import org.bukkit.Location;

import java.util.List;

public class Cuboid {
    private final Vec3i sv = new Vec3i(); //small
    private final Vec3i bv = new Vec3i(); //big

    private boolean isCollect = false;

    public Cuboid(Location loc){
        sv.x = loc.getBlockX();
        sv.y = loc.getBlockY();
        sv.z = loc.getBlockZ();
    }

    public Cuboid(Location locA, Location locB){
        sv.x = Math.min(locA.getBlockX(), locB.getBlockX());
        sv.y = Math.min(locA.getBlockY(), locB.getBlockY());
        sv.z = Math.min(locA.getBlockZ(), locB.getBlockZ());
        bv.x = Math.max(locA.getBlockX(), locB.getBlockX());
        bv.y = Math.max(locA.getBlockY(), locB.getBlockY());
        bv.z = Math.max(locA.getBlockZ(), locB.getBlockZ());
        isCollect = true;
    }

    public Cuboid(Vec3i v1, Vec3i v2){
        sv.x = Math.min(v1.x, v2.x);
        sv.y = Math.min(v1.y, v2.y);
        sv.z = Math.min(v1.z, v2.z);
        bv.x = Math.max(v1.x, v2.x);
        bv.y = Math.max(v1.y, v2.y);
        bv.z = Math.max(v1.z, v2.z);
        isCollect = true;
    }

    public void setSecond(Location loc){
        if(isCollect) return;
        sv.x = Math.min(loc.getBlockX(), sv.x);
        sv.y = Math.min(loc.getBlockY(), sv.y);
        sv.z = Math.min(loc.getBlockZ(), sv.z);
        bv.x = Math.max(loc.getBlockX(), sv.x);
        bv.y = Math.max(loc.getBlockY(), sv.y);
        bv.z = Math.max(loc.getBlockZ(), sv.z);
        isCollect = true;
    }

    public Vec3i getMinPointVec3i(){
        return sv;
    }

    public Vec3i getMaxPointVec3i(){
        return bv;
    }


    public boolean contain(Location loc){
        return sv.x < loc.getX() && loc.getX() < bv.x
                && sv.y < loc.getY() && loc.getY() < bv.y
                && sv.z < loc.getZ() && loc.getZ() < bv.z;
    }
}

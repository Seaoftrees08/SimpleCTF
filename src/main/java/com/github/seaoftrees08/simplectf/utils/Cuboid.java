package com.github.seaoftrees08.simplectf.utils;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class Cuboid {
    private final Vec3i sv = new Vec3i(); //small
    private final Vec3i bv = new Vec3i(); //big

    private boolean isCollect = false;

    public Cuboid(Location loc){
        sv.x = loc.getBlockX();
        sv.y = loc.getBlockY();
        sv.z = loc.getBlockZ();
        bv.x = loc.getBlockX();
        bv.y = loc.getBlockY();
        bv.z = loc.getBlockZ();
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
        bv.x = Math.max(loc.getBlockX(), bv.x);
        bv.y = Math.max(loc.getBlockY(), bv.y);
        bv.z = Math.max(loc.getBlockZ(), bv.z);
        isCollect = true;
    }

    public Vec3i getMinPointVec3i(){
        return sv;
    }

    public Vec3i getMaxPointVec3i(){
        return bv;
    }

    public Vec3i getCentral(){
        return new Vec3i(
                List.of(
                        (sv.x + bv.x) / 2,
                        (sv.y + bv.y) / 2,
                        (sv.z + bv.z) / 2
                )
        );
    }


    public boolean contain(Location loc){
        return sv.x < loc.getX() && loc.getX() < bv.x
                && sv.y < loc.getY() && loc.getY() < bv.y
                && sv.z < loc.getZ() && loc.getZ() < bv.z;
    }
}

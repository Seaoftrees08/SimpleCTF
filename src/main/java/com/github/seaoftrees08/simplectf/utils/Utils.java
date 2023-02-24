package com.github.seaoftrees08.simplectf.utils;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import org.bukkit.Location;

import java.util.List;

public class Utils {

    public static Location listToLocation(List<String> lst){
        return new Location(
            SimpleCTF.getSimpleCTF().getServer().getWorld(lst.get(5)),
            Double.parseDouble(lst.get(0)),
            Double.parseDouble(lst.get(1)),
            Double.parseDouble(lst.get(2)),
            Float.parseFloat(lst.get(3)),
            Float.parseFloat(lst.get(4))
        );
    }

}

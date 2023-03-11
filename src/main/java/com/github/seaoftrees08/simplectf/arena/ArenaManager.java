package com.github.seaoftrees08.simplectf.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArenaManager {
    public static final String ARENA_LIST_PATH = "ArenaList";
    public static HashMap<String, Arena> playArena = new HashMap<>();
    public static HashMap<String, CreateArena> createArena = new HashMap<>();

    public static List<String> loadArenaNameList(){
        //TODO
        return new ArrayList<>();
    }

}

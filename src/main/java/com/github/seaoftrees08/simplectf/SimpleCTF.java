package com.github.seaoftrees08.simplectf;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class SimpleCTF extends JavaPlugin {

    private static SimpleCTF plugin;
    private static HashMap<String, String> creating = new HashMap<>();//PlayerName ArenaName

    public static SimpleCTF getSimpleCTF(){
        return plugin;
    }

    @Override
    public void onEnable() {

        //config
        this.saveDefaultConfig();

        //Commands
        getCommand("sctf").setExecutor(new Commands());
        getCommand("sctf").setTabCompleter(new CmdTabCompletion());

        //Listener
        new PlayerListeners(this);

        super.onEnable();
        plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * アリーナの作成を行うステータスを付与する
     * これにより作成中に重複したりすることを防ぐ
     *
     * @param playerName 作成者
     * @param arenaName アリーナ名
     * @return 成功したかどうか(false->重複している)
     */
    public static boolean startCreation(String playerName, String arenaName){
        if(creating.containsKey(playerName) || creating.containsValue(arenaName)){
            return false;
        }else{
            creating.put(playerName, arenaName);
            return true;
        }
    }
}

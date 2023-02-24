package com.github.seaoftrees08.simplectf;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class SimpleCTF extends JavaPlugin {

    private static SimpleCTF plugin;

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


}

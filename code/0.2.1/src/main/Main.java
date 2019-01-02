package main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{

	@Override
	public void onEnable() {
		
		//config
		this.saveDefaultConfig();
		
		//Commands
		getCommand("sctf").setExecutor(new Commands(this));
		
		//Listener
		new PlayerListeners(this);
		
		super.onEnable();
	}

	@SuppressWarnings("static-access")
	@Override
	public void onDisable() {
		
		this.saveConfig();
		
		ArenaData ad = new ArenaData();
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			if(ad.arenaStatus.containsKey(p.getName())) {
				String arena = ad.playerStatus.get(p.getName());
				new FinishCTF().Finished(arena, this, true);
			}
		}
		
		super.onDisable();
	}

}

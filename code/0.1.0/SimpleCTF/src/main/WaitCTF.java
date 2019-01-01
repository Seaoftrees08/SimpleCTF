package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitCTF extends BukkitRunnable{
	
	private String arena;
	private Plugin plugin;
	private World world;
	
	//あ最初に一度のみ実行される witten in UTF-8
	@SuppressWarnings("static-access")
	public WaitCTF(String arena, Plugin plugin, World world) {
		this.plugin = plugin;
		this.arena = arena;
		this.world = world;
		int c = new ArenaData().arenaStatus.getOrDefault(arena, 0);
		if(c!=1 && !(c<10 && c!=0)) {
			throw new  IllegalArgumentException(ChatColor.AQUA + "[S-CTF] "
					+ ChatColor.DARK_RED + "fatal error when ArenaWaitCounter start. location: WaitCTF.java");
		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		int c = new ArenaData().arenaStatus.getOrDefault(arena, 0)-1;
		if(c==-2) {
			new ArenaData().arenaStatus.remove(arena);
			this.cancel();
		}else if(c==0){
			new ArenaData().arenaStatus.put(arena, 130);
			new PlayCTF(arena, plugin, world).runTaskTimer(plugin, 100, 50);//debug
			this.cancel();
		}else if(c>100) {
			this.cancel();
		}else {
			new ArenaData().arenaStatus.put(arena, c);
			Broadcast(arena + " will start in " + ChatColor.GOLD + (c*10) + ChatColor.GREEN + " Seconds", ChatColor.GREEN, arena);
		}
	}
	
	private void Broadcast(String message, ChatColor cc, String arena) {
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF]" +
				ChatColor.WHITE + "(" + arena + ") " + cc + message);
	}
	
}
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FinishCTF {
	
	@SuppressWarnings("static-access")
	public void Finish(String arena, Plugin plugin, boolean abnormal) {
		ArenaData ad = new ArenaData();
		HashMap<Integer, ArrayList<String>> playerList = ad.playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
		
		ad.playerList.remove(arena);
		
		//player
		for(String s : playerList.get(1)) {
			Player p = Bukkit.getServer().getPlayer(s);
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.getInventory().clear();
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
			p.getInventory().setChestplate(new ItemStack(Material.AIR));
			p.getInventory().setLeggings(new ItemStack(Material.AIR));
			p.getInventory().setBoots(new ItemStack(Material.AIR));
			ad.playerStatus.remove(p.getName());
			p.teleport(p.getWorld().getSpawnLocation());
		}
		for(String s : playerList.get(2)) {
			Player p = Bukkit.getServer().getPlayer(s);
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.getInventory().clear();
			p.getInventory().setHelmet(new ItemStack(Material.AIR));
			p.getInventory().setChestplate(new ItemStack(Material.AIR));
			p.getInventory().setLeggings(new ItemStack(Material.AIR));
			p.getInventory().setBoots(new ItemStack(Material.AIR));
			ad.playerStatus.remove(p.getName());
			p.teleport(p.getWorld().getSpawnLocation());
		}
		
		//arena
		ad.spawnpoint.remove(arena);
		ad.arenaStatus.remove(arena);
		
		String[] fs = ad.flag1Status.get(arena).split(", ");
		if(fs[0].equals("camp")) {
			FlyingItem fi = ad.flagArmor1.get(arena);
			fi.remove();
			ad.flagArmor1.remove(arena);
		}else if(fs[0].equalsIgnoreCase("onGround")) {
			ad.flag1Drop.get(arena).remove();
			ad.flag1Drop.remove(arena);
		}
		fs = ad.flag2Status.get(arena).split(", ");
		if(fs[0].equals("camp")) {
			FlyingItem fi = ad.flagArmor2.get(arena);
			fi.remove();
			ad.flagArmor2.remove(arena);
		}else if(fs[0].equalsIgnoreCase("onGround")) {
			ad.flag2Drop.get(arena).remove();
			ad.flag2Drop.remove(arena);
		}
		ad.flag2Status.remove(arena);
		
		List<Integer> points = ad.arenaPoints.get(arena);
		if(abnormal) {
			Broadcast("Game over! This game ended "+ChatColor.DARK_PURPLE+"Abnormally.", ChatColor.AQUA, arena);
		}else {
			if(points.get(0)>points.get(1)) {
				Broadcast("Game over! Winner team is "+ChatColor.RED+"RED Team!", ChatColor.GOLD, arena);
			}else if(points.get(0)<points.get(1)) {
				Broadcast("Game over! Winner team is "+ChatColor.BLUE+"BLUE Team!", ChatColor.GOLD, arena);
			}else {
				Broadcast("Game over! This game was "+ChatColor.LIGHT_PURPLE+"Draw!", ChatColor.GOLD, arena);
			}
		}
		ad.arenaPoints.remove(arena);
		
		List<Integer> co = plugin.getConfig().getIntegerList(arena+".flag1");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender()
				, "kill @e[type=ArmorStand,x="+co.get(0)+",y="+co.get(1)+",z="+co.get(2)+",r=3]");
		co = plugin.getConfig().getIntegerList(arena+".flag2");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender()
				, "kill @e[type=ArmorStand,x="+co.get(0)+",y="+co.get(1)+",z="+co.get(2)+",r=3]");
	}
	
	private void Broadcast(String message, ChatColor cc, String arena) {
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF]" +
				ChatColor.WHITE + "(" + arena + ") " + cc + message);
	}

}

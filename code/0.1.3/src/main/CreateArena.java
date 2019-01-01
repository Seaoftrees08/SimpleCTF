package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public class CreateArena {
	
	private static HashMap<String, Integer> creating = new HashMap<String,Integer>();
	private static HashMap<String, String> createArena = new HashMap<String,String>();
	
	public static int getCreateControle(String playerName) {
		if(creating.containsKey(playerName)) {
			return (int)creating.get(playerName);
		}else {
			return 0;
		}
	}
	
	public void MainFlow(Player p, String arenaName, Main plugin) {
		
		String name = p.getName();
		int cn = creating.getOrDefault(name, 0);
		
		//Add Item
		if(cn==0){
			p.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
			createArena.put(name, arenaName);
			creating.put(name, 1);
			SendMessage(p, "Left Punch arena First point", ChatColor.GREEN);
		}
		
		//Set FirstPoint
		// from Listener to SetFirstPoint
		
		//Set SecondPoint
		// from Listener to SetSecondPoint
		
		//Set Flag1
		// from Listener to SetFlag1
		
		//Set Flag2
		// from Listener to SetFlag2
		
		//Set Spawn1
		// from Listener to SetSpawn1
		
		//Set Spawn2
		// from Listener to SetSpawn2
		
		//Set EndPoint
		// from Listener to SetEnd
		
		//SetInv1	8
		if(cn==8) {
			SetInv(p.getInventory(), "Inv1", p.getName(), plugin);
			creating.put(p.getName(), 9);
			SendMessage(p, "type cmd for team2 /simplectf admin setInv", ChatColor.GREEN);
		}
		
		//setInv2	9
		if(cn==9) {
			SetInv(p.getInventory(), "Inv2", p.getName(), plugin);
			plugin.getConfig().set(createArena.get(p.getName())+".enable", false);
			List<String> lst = plugin.getConfig().getStringList("List");
			lst.add(createArena.get(p.getName()));
			plugin.getConfig().set("List", lst);
			creating.put(p.getName(), 10);
			
			//finish
			SendMessage(p, "Success! to Create Arena", ChatColor.GREEN);
			plugin.saveConfig();
			plugin.reloadConfig();
			creating.remove(p.getName());
			createArena.remove(p.getName());
		}
	}
	
	//1
	public void SetFirstPoint(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".firstpoint", lst);
		creating.put(playerName, 2);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "Left Punch arena Second point", ChatColor.GREEN);
	}
	
	//2
	public void SetSecondPoint(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".secondpoint", lst);
		creating.put(playerName, 3);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "Left Punch Flag1 point", ChatColor.GREEN);
	}
	
	//3
	public void SetFlag1(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".flag1", lst);
		creating.put(playerName, 4);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "Left Punch Flag2 point", ChatColor.GREEN);
	}
	
	//4
	public void SetFlag2(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".flag2", lst);
		creating.put(playerName, 5);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "Left Punch Spawn1 point", ChatColor.GREEN);
	}
	
	//5
	public void SetSpawn1(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".spawn1", lst);
		creating.put(playerName, 6);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "Left Punch Spawn2 point", ChatColor.GREEN);
	}
	
	//6
	public void SetSpawn2(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".spawn2", lst);
		creating.put(playerName, 7);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "Left Punch Return point", ChatColor.GREEN);
	}
	
	//7
	public void SetEnd(Location l, Main plugin, String playerName) {
		List<Integer> lst = new ArrayList<Integer>(Arrays.asList(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		plugin.getConfig().set(createArena.get(playerName)+".endpoint", lst);
		creating.put(playerName, 8);
		SendMessage(plugin.getServer().getPlayer(playerName)
				, "type cmd for team1 /simplectf admin setInv", ChatColor.GREEN);
	}
	
	//SetInventory
	private void SetInv(PlayerInventory pinv, String str, String playerName, Main plugin) {
		List<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> armors = new ArrayList<ArrayList<String>>();

		for(ItemStack i : pinv.getContents()) items.add(InvToList(i));
		for(ItemStack i : pinv.getArmorContents()) armors.add(InvToList(i));
		
		plugin.getConfig().set(createArena.get(playerName)+"."+str+".item", items);
		plugin.getConfig().set(createArena.get(playerName)+"."+str+".armor", armors);

	}
	
	private ArrayList<String> InvToList(ItemStack i){
		ArrayList<String> itm = new ArrayList<String>();
		if(i!=null) {
			itm.add(i.getType().name());
			itm.add(String.valueOf(i.getAmount()));
			if(i.hasItemMeta()) {
				String s = "";
				for(Enchantment en : i.getItemMeta().getEnchants().keySet()) {
					s +=en.getName() + ", " + i.getItemMeta().getEnchants().getOrDefault(en, 0) + ", ";
				}
				itm.add(s);
			}else {
				itm.add("");
			}
			//Potion
			if(i.getType().equals(Material.POTION)) {
				String s = "";
				for(PotionEffect pe : Potion.fromItemStack(i).getEffects()) {
					s += pe.getType().getName() + ", "
							+ pe.getDuration() + ", "
							+ pe.getAmplifier() + ", "
							+ Potion.fromItemStack(i).isSplash() + ", ";
				}
				itm.add(s);
			}else {
				itm.add("");
			}
			
		}else {
			itm.add(Material.AIR.name());
			itm.add("0");
			itm.add("");
			itm.add("");
		}
		return itm;
	}
	
	private void SendMessage(Player p, String message, ChatColor cc) {
		p.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
	}

}

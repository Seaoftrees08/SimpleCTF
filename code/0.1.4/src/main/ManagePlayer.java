package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ManagePlayer<EntityPlayer> {

	@SuppressWarnings("static-access")
	public void Join(Player p, String arena, Main plugin) {
		ArenaData ad = new ArenaData();
		int cn = ad.arenaStatus.getOrDefault(arena, 0);
		
		if(!plugin.getConfig().getStringList("List").contains(arena)) {
			SendMessage(p, "This arena does not exist.", ChatColor.RED);
		}else if(ad.playerStatus.containsKey(p.getName())){
			SendMessage(p, "You have already join another arena.", ChatColor.RED);
		}else if(!plugin.getConfig().getBoolean(arena+".enable")) {
			SendMessage(p, "This arena is disabled.", ChatColor.RED);
		}else {
			
			//player
			SendMessage(p, "You join CTF in " + arena, ChatColor.GREEN);
			ad.playerStatus.put(p.getName(), arena);
			HashMap<Integer, ArrayList<String>> hm = ad.playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
			ArrayList<String> team1List = hm.getOrDefault(1, new ArrayList<String>());
			ArrayList<String> team2List = hm.getOrDefault(2, new ArrayList<String>());
			int team1Length = team1List.size(), team2Length = team2List.size();
			int team;
			if(team2Length >= team1Length) {
				//team1
				team1List.add(p.getName());
				hm.put(1, team1List);
				ad.playerList.put(arena, hm); //necessary
				SendMessage(p, "You are" + ChatColor.RED + " Red Team", ChatColor.GREEN);
				p.setPlayerListName(ChatColor.RED+p.getName());
				team = 1;
			}else {
				//team2
				team2List.add(p.getName());
				hm.put(2, team2List);
				ad.playerList.put(arena, hm); //necessary
				SendMessage(p, "You are" + ChatColor.BLUE + " BLUE Team", ChatColor.GREEN);
				p.setPlayerListName(ChatColor.BLUE+p.getName());
				team = 2;
			}
			if(cn>99) {
				//already playing
				new ScoreboardCtrl().PlayScoreBoardChange(arena);
				if(team==1) {
					TeleportPlayer(plugin.getConfig().getIntegerList(arena+".spawn1"), Arrays.asList(p.getName()));
				}else {
					TeleportPlayer(plugin.getConfig().getIntegerList(arena+".spawn2"), Arrays.asList(p.getName()));
				}
			}else if(cn>-2) {
				new ScoreboardCtrl().WaitScoreboardChange(arena);
				
			}else {
				Broadcast("fatal error when aplly scoreboard. in ManagePlugin.java", ChatColor.DARK_RED);
				Broadcast("Scoreboard Controle Number is "+ cn, ChatColor.DARK_RED);
			}
			
			//arena
			hm = ad.playerList.getOrDefault(arena, new HashMap<Integer,  ArrayList<String>>());
			team1List = hm.getOrDefault(1, null);
			team2List = hm.getOrDefault(2, null);
			team1Length = 0;
			team2Length = 0;
			if(team1List!=null) team1Length = team1List.size();
			if(team2List!=null) team2Length = team2List.size();
			if(team1Length>0 && team2Length>0) {
				if(cn==-1) {
					Broadcast(arena + "'s countdown is restarted because player is more than required", ChatColor.GREEN);
					ad.arenaStatus.put(arena, 6);
					new WaitCTF(arena, plugin, p.getWorld()).runTaskTimer(plugin, 100, 200);
				}else if(cn==0) {
					//new
					ad.arenaStatus.put(arena, 6);
					Broadcast("("+arena+")"+ChatColor.GREEN+" Start countdown until begin game!", ChatColor.WHITE);
					new WaitCTF(arena, plugin, p.getWorld()).runTaskTimer(plugin, 100, 200);//debug
				}else if(cn>99) {
					//already playing
					
				}else {
					Broadcast("fatal error when arena setting. in ManagePlugin.java", ChatColor.DARK_RED);
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public void leave(Player p, Main plugin) {
		
		if(new ArenaData().playerStatus.containsKey(p.getName())) {
			ArenaData ad = new ArenaData();
			String arena = ad.playerStatus.get(p.getName());
			int cn = ad.arenaStatus.getOrDefault(arena, 0);
			
			//player
			SendMessage(p, "You leave from " + arena, ChatColor.GREEN);
			HashMap<Integer, ArrayList<String>> hm = ad.playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
			int team = ad.BelongTeam(p.getName());
			ArrayList<String> teamList = hm.getOrDefault(team, new ArrayList<String>());
			teamList.remove(p.getName());
			hm.put(team, teamList);
			ad.playerList.put(arena, hm);
			if(cn>99) {
				new ScoreboardCtrl().PlayScoreBoardChange(arena);
				p.teleport(p.getWorld().getSpawnLocation());
			}else if(cn>-2) {
				new ScoreboardCtrl().WaitScoreboardChange(arena);
			}else {
				Broadcast("fatal error when aplly scoreboard. in ManagePlugin.java", ChatColor.DARK_RED);
				Broadcast("Scoreboard Controle Number is "+ cn, ChatColor.DARK_RED);
			}
			ad.playerStatus.remove(p.getName());
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.setPlayerListName(ChatColor.WHITE+p.getName());
			
			//arena
			hm = ad.playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
			ArrayList<String> team1List = hm.getOrDefault(1, new ArrayList<String>());
			ArrayList<String> team2List = hm.getOrDefault(2, new ArrayList<String>());
			if(new ArenaData().arenaStatus.containsKey(arena)
					&&( team1List.size()==0 || team2List.size()==0)) {
				if(cn>99) {
					//already playing
					Broadcast(arena + "'s game is canceled because player is less", ChatColor.RED);
					new FinishCTF().Finish(arena, plugin, true);
				}else if(cn>-2) {
					Broadcast(arena + "'s countdown is canceled because player is less", ChatColor.RED);
					new ArenaData().arenaStatus.put(arena, -1);
				}else {
					Broadcast("fatal error when arena setting. in ManagePlugin.java", ChatColor.DARK_RED);
				}
			}
			
		}else {
			SendMessage(p, "You join nothing.", ChatColor.RED);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void PaidItems(PlayerInventory pinv, String dir, Plugin plugin, String arena, boolean armor) {	
		int i = 0;
		for(List<String> lst : (List<ArrayList<String>>) plugin.getConfig().getList(arena+"."+dir)) {
			if(armor) {
				switch(i) {
				case 0:
					pinv.setBoots(ListToItem(lst));
				case 1:
					pinv.setLeggings(ListToItem(lst));
				case 2:
					pinv.setChestplate(ListToItem(lst));
				case 3:
					pinv.setHelmet(ListToItem(lst));
					//i++ is not writeen but its ok for escape error
				}
			}else {
				pinv.addItem(ListToItem(lst));
			}
			i++;
		}
	}
	
	private ItemStack ListToItem(List<String> lst) {
		//setType
		ItemStack i = new ItemStack(Material.valueOf(lst.get(0)));
		//setAmount
		i.setAmount(Integer.valueOf(lst.get(1)));
		//setEnchant
		ItemMeta im = i.getItemMeta();
		String[] ss = lst.get(2).split(", ");
		for(int j=0; j<ss.length; j+=2) {
			if(ss[j].equals("")) break;
			im.addEnchant(Enchantment.getByName(ss[j]), Integer.valueOf(ss[j+1]), true);
		}
		//set Potion
		if(i.getType().equals(Material.POTION)) {
			ss = lst.get(3).split(", ");
			for(int j=0; j<ss.length; j+=4) {
				PotionType pet = PotionType.getByEffect(PotionEffectType.getByName(ss[0]));
				pet.getEffectType().createEffect(Integer.valueOf(ss[2]), Integer.valueOf(ss[1]));
				
				Potion po = new Potion(PotionType.getByEffect(PotionEffectType.getByName(ss[0])));
				po.setType(pet);
				po.setSplash(Boolean.valueOf(ss[3]));
				po.apply(i);
			}
		}
		i.setItemMeta(im);
		return i;
	}
	
	private void TeleportPlayer(List<Integer> coordinate, List<String> PlayerList) {
		for(String s : PlayerList) {
			Location l = Bukkit.getServer().getPlayer(s).getLocation().clone();
			l.setX(coordinate.get(0)+0.5);
			l.setY(coordinate.get(1)+1.0);
			l.setZ(coordinate.get(2)+0.5);
			Bukkit.getServer().getPlayer(s).teleport(l);
			Bukkit.getServer().getPlayer(s).setGameMode(GameMode.SURVIVAL);
		}
	}
	
	private void SendMessage(Player p, String message, ChatColor cc) {
		p.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
	}
	
	private void Broadcast(String message, ChatColor cc) {
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
	}
	
}

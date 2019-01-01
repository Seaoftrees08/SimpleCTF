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
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayCTF extends BukkitRunnable{
	
	private String arena;
	private Plugin plugin;
	private World world;
	
	@SuppressWarnings("static-access")
	public PlayCTF(String arena, Plugin plugin, World world) {
		this.plugin = plugin;
		this.arena = arena;
		this.world = world;
		ArenaData ad = new ArenaData();
		HashMap<Integer, ArrayList<String>> hm = new ArenaData().playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
		ArrayList<String> team1List = hm.getOrDefault(1, new ArrayList<String>());
		ArrayList<String> team2List = hm.getOrDefault(2, new ArrayList<String>());
		int team1Length = team1List.size(), team2Length = team2List.size();
		
		if(team1Length==0 || team2Length==0) {
			Broadcast(arena + "'s countdown is canceled because player is less", ChatColor.RED, arena);
			ad.arenaStatus.put(arena, 0);
		}else {
			ad.arenaStatus.put(arena, 130);
			FileConfiguration fc = plugin.getConfig();
			
			TeleportPlayer(fc.getIntegerList(arena+".spawn1"), team1List);
			TeleportPlayer(fc.getIntegerList(arena+".spawn2"), team2List);
			
			List<Integer> spawnpoint = new ArrayList<Integer>();
			for(int i : fc.getIntegerList(arena+".spawn1")) spawnpoint.add(i);
			for(int i : fc.getIntegerList(arena+".spawn2")) spawnpoint.add(i);
			ad.spawnpoint.put(arena, spawnpoint);
			
			Broadcast(ChatColor.BOLD+"Game Start!", ChatColor.GREEN, arena);
			Broadcast("300 seconds remaining time", ChatColor.GREEN, arena);
			
			PaidItems(team1List, "Inv1.item", false);
			PaidItems(team1List, "Inv1.armor", true);
			PaidItems(team2List, "Inv2.item", false);
			PaidItems(team2List, "Inv2.armor", true);
			
			new FlagParticle(arena, plugin, world).runTaskTimer(plugin, 50, 50);
		}
		
		//arena
		List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag1");
		FlyingItem fi = new FlyingItem();
		fi.SetLocation(new Location(this.world, coordinate.get(0), coordinate.get(1), coordinate.get(2)));
		fi.setItemStack(getFlag(1));
		fi.setText("RED flag");
		fi.spawn(plugin);
		ad.flagArmor1.put(arena, fi);
		ad.flag1Status.put(arena, "camp");
		
		coordinate = plugin.getConfig().getIntegerList(arena+".flag2");
		fi = new FlyingItem();
		fi.SetLocation(new Location(this.world, coordinate.get(0), coordinate.get(1), coordinate.get(2)));
		fi.setItemStack(getFlag(2));
		fi.setText("BLUE flag");
		fi.spawn(plugin);
		ad.flagArmor2.put(arena, fi);
		ad.flag2Status.put(arena, "camp");
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		ArenaData ad = new ArenaData();
		int cn = ad.arenaStatus.getOrDefault(arena, 0)-1;
		if(cn==100) {
			//end
			new FinishCTF().Finish(arena, plugin, false);
			this.cancel();
			
		}else if(cn<100) {
			this.cancel();
		}else {
			if((cn-100)%3==0) Broadcast(((cn-100)*10)+" seconds remaining time ", ChatColor.GREEN, arena);
			ad.arenaStatus.put(arena, cn);
			ad.arenaPoints.put(arena, ad.arenaPoints.getOrDefault(arena, Arrays.asList(0, 0)));
			new ScoreboardCtrl().PlayScoreBoardChange(arena);
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private void PaidItems(List<String> teamList, String dir, boolean armor) {	
		int i = 0;
		for(List<String> lst : (List<ArrayList<String>>) plugin.getConfig().getList(arena+"."+dir)) {
			for(String s: teamList) {
				PlayerInventory pinv = Bukkit.getServer().getPlayer(s).getInventory();
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
	
	private ItemStack getFlag(int team) {
		ItemStack is;
		if(team==1) {
			is = new ItemStack(Material.WOOL, 1, (byte)14);
			is.getItemMeta().setDisplayName(ChatColor.RED+"RED Flag");
		}else {
			is = new ItemStack(Material.WOOL, 1, (byte)11);
			is.getItemMeta().setDisplayName(ChatColor.BLUE+"BLUE Flag");
		}
		List<String> lore = new ArrayList<String>();
		lore.add("bring this flag back to your base flag");
		is.getItemMeta().setLore(lore);

		return is;
	}

	private void Broadcast(String message, ChatColor cc, String arena) {
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF]" +
				ChatColor.WHITE + "(" + arena + ") " + cc + message);
	}
}

package main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ManageFlag {

	@SuppressWarnings("static-access")
	public void WhenTakeFlag(PlayerPickupItemEvent e, Plugin plugin) {
		ArenaData ad = new ArenaData();
		String name = e.getPlayer().getName();
		int team = ad.BelongTeam(name);
		String arena = ad.playerStatus.get(name);
		
		if(team==1) {
			if(e.getItem().getItemStack().getType().equals(Material.WOOL)
					&& e.getItem().getItemStack().getDurability()==14) {
				if(ad.flag1Drop.containsKey(arena)) {
					e.getItem().remove();
					ad.flag1Drop.remove(arena);
					ad.flag1Status.put(arena, "camp");
					
					List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag1");
					FlyingItem fi = new FlyingItem();
					fi.SetLocation(new Location(e.getPlayer().getWorld(), coordinate.get(0), coordinate.get(1), coordinate.get(2)));
					fi.setItemStack(getFlag(1));
					fi.setText("RED flag");
					fi.spawn(plugin);
					ad.flagArmor1.put(arena, fi);
					
					Broadcast("RED Flag"+ChatColor.GREEN+" is returned camp", ChatColor.RED, arena);
				}
				e.setCancelled(true);
			}else if(e.getItem().getItemStack().getType().equals(Material.WOOL)
					&& e.getItem().getItemStack().getDurability()==11){
				ad.flag2Status.put(arena, name);
				if(ad.flag2Drop.containsKey(arena)) {
					ad.flag2Drop.remove(arena);
					Broadcast("BLUE Flag"+ChatColor.LIGHT_PURPLE+" is captured by "+ChatColor.RED+name, ChatColor.BLUE, arena);
				}else if(ad.flagArmor2.containsKey(arena)){
					FlyingItem fi = ad.flagArmor2.get(arena);
					fi.remove();
					ad.flagArmor2.remove(arena);
					Broadcast("BLUE Flag"+ChatColor.LIGHT_PURPLE+" is captured by "+ChatColor.RED+name, ChatColor.BLUE, arena);
				}
			}
		}else {
			if(e.getItem().getItemStack().getDurability()==11) {
				if(ad.flag2Drop.containsKey(arena)) {
					e.getItem().remove();
					ad.flag2Drop.remove(arena);
					ad.flag2Status.put(arena, "camp");
					
					List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag2");
					FlyingItem fi = new FlyingItem();
					fi.SetLocation(new Location(e.getPlayer().getWorld(), coordinate.get(0), coordinate.get(1), coordinate.get(2)));
					fi.setItemStack(getFlag(2));
					fi.setText("BLUE flag");
					fi.spawn(plugin);
					ad.flagArmor1.put(arena, fi);
					
					Broadcast("BLUE Flag"+ChatColor.GREEN+" is returned camp", ChatColor.BLUE, arena);
				}
				e.setCancelled(true);
			}else {
				ad.flag1Status.put(arena, name);
				if(ad.flag1Drop.containsKey(arena)) {
					ad.flag1Drop.remove(arena);
					Broadcast("RED Flag"+ChatColor.LIGHT_PURPLE+" is captured by "+ChatColor.BLUE+name, ChatColor.RED, arena);
				}else if(ad.flagArmor1.containsKey(arena)){
					FlyingItem fi = ad.flagArmor1.get(arena);
					fi.remove();
					ad.flagArmor1.remove(arena);
					Broadcast("RED Flag"+ChatColor.LIGHT_PURPLE+" is captured by "+ChatColor.BLUE+name, ChatColor.RED, arena);
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public void ThrowAwayFlag(PlayerDropItemEvent e) {
		ArenaData ad = new ArenaData();
		String name = e.getPlayer().getName();
		int team = ad.BelongTeam(name);
		String arena = ad.playerStatus.get(name);
		
		if(team==1) {
			if(e.getItemDrop().getItemStack().getDurability()==11
					&& !ad.flag2Drop.containsKey(arena)) {
				ad.flag2Status.put(arena, "onGround, 6");
				ad.flag2Drop.put(arena, e.getItemDrop());
			}
		}else {
			if(e.getItemDrop().getItemStack().getDurability()==14
					&& !ad.flag1Drop.containsKey(arena)) {
				ad.flag1Status.put(arena, "onGround, 6");
				ad.flag1Drop.put(arena, e.getItemDrop());
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public void PlayerDeath(PlayerDeathEvent e) {
		ArenaData ad = new ArenaData();
		String name = e.getEntity().getName();
		int team = ad.BelongTeam(name);
		String arena = ad.playerStatus.get(name);
		Location l = e.getEntity().getLocation();
		l.setY(l.getY()+1.0);
		if(team==1 && ad.flag2Status.getOrDefault(arena, "camp").equalsIgnoreCase(name)) {
			ItemStack is = getFlag(2);
			Item i = e.getEntity().getWorld().dropItemNaturally(l, is);
			ad.flag2Status.put(arena, "onGround, 6");
			ad.flag2Drop.put(arena, i);
			Broadcast("BLUE Flag"+ChatColor.LIGHT_PURPLE+" is Droped! ", ChatColor.BLUE, arena);
		}else if(team==2 && ad.flag1Status.getOrDefault(arena, "camp").equalsIgnoreCase(name)){
			ItemStack is = getFlag(1);
			Item i = e.getEntity().getWorld().dropItemNaturally(l, is);
			ad.flag1Status.put(arena, "onGround, 6");
			ad.flag1Drop.put(arena, i);
			Broadcast("RED Flag"+ChatColor.LIGHT_PURPLE+" is Droped! ", ChatColor.RED, arena);
		}
		e.setKeepInventory(true);
	}
	
	@SuppressWarnings("static-access")
	public void DevoteFlag(Player p, Plugin plugin) {
		ArenaData ad = new ArenaData();
		int team = ad.BelongTeam(p.getName());
		String arena = ad.playerStatus.get(p.getName());
		
		p.getInventory().remove(Material.WOOL);
		if(team==1) {
			ad.flag2Status.put(arena, "camp");
			List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag2");
			FlyingItem fi = new FlyingItem();
			fi.SetLocation(new Location(p.getWorld(), coordinate.get(0), coordinate.get(1), coordinate.get(2)));
			fi.setItemStack(getFlag(2));
			fi.setText("BLUE flag");
			fi.spawn(plugin);
			List<Integer> lst = ad.arenaPoints.get(arena);
			lst.set(0, lst.get(0)+1);
			ad.arenaPoints.put(arena, lst);
			ad.flagArmor2.put(arena, fi);
			Broadcast("RED Team"+ChatColor.LIGHT_PURPLE+" won 1 point!", ChatColor.RED, arena);
		}else {
			ad.flag1Status.put(arena, "camp");
			List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag1");
			FlyingItem fi = new FlyingItem();
			fi.SetLocation(new Location(p.getWorld(), coordinate.get(0), coordinate.get(1), coordinate.get(2)));
			fi.setItemStack(getFlag(1));
			fi.setText("RED flag");
			fi.spawn(plugin);
			List<Integer> lst = ad.arenaPoints.get(arena);
			lst.set(1, lst.get(1)+1);
			ad.arenaPoints.put(arena, lst);
			ad.flagArmor1.put(arena, fi);
			Broadcast("BLUE Team"+ChatColor.LIGHT_PURPLE+" won 1 point!", ChatColor.BLUE, arena);
		}
		new ScoreboardCtrl().PlayScoreBoardChange(arena);
		
		//finish
		List<Integer> lst = ad.arenaPoints.get(arena);
		if(lst.get(0)>=3 || lst.get(1)>=3) {
			new FinishCTF().Finished(arena, plugin, false);
		}
	}
	
	public boolean nearFlag(Location l, int team, String arena, Plugin plugin) {
		
		List<Integer> co = plugin.getConfig().getIntegerList(arena+".flag"+team);
		double disX = co.get(0)-l.getX();
		double disY = co.get(1)-l.getY();
		double disZ = co.get(2)-l.getZ();
		double distance = Math.sqrt(disX*disX + disY*disY + disZ*disZ);
		
		return (distance<1.2);
	}
	
	
	private void Broadcast(String message, ChatColor cc, String arena) {
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF]" +
				ChatColor.WHITE + "(" + arena + ") " + cc + message);
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
}

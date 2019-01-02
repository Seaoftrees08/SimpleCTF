package main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;


public class PlayerListeners implements Listener{

	private final Main plugin;
	
	//wittern in UTF-8
	public PlayerListeners(Main plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	//Playerが死んだときに発生
	@SuppressWarnings("static-access")
	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent e) {
		ArenaData ad = new ArenaData();
		String name = e.getEntity().getName();
		if(ad.playerStatus.containsKey(name)
				&& ad.arenaStatus.getOrDefault(ad.playerStatus.get(name), 0)>100) {
			new ManageFlag().PlayerDeath(e);
		}
	}
	
	//Playerがアイテム拾ったときに発生
	@SuppressWarnings("static-access")
	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent e) {
		ArenaData ad = new ArenaData();
		String name = e.getPlayer().getName();
		if(ad.playerStatus.containsKey(name)
				&& ad.arenaStatus.getOrDefault(ad.playerStatus.get(name), 0)>100) {
			new ManageFlag().WhenTakeFlag(e, plugin);
		}
	}
	
	//Playerがアイテム捨てたときに発生
	@SuppressWarnings("static-access")
	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent e) {
		ArenaData ad = new ArenaData();
		String name = e.getPlayer().getName();
		if(ad.playerStatus.containsKey(name)
				&& ad.arenaStatus.getOrDefault(ad.playerStatus.get(name), 0)>100) {
			if(e.getItemDrop().getItemStack().getType().equals(Material.WOOL)) {
				new ManageFlag().ThrowAwayFlag(e);
			}else {
				e.setCancelled(true);
			}
			
		}
	}
	
	//Playerの移動時に発生
	@SuppressWarnings("static-access")
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		ArenaData ad = new ArenaData();
		String name = e.getPlayer().getName();
		
		if(ad.playerStatus.containsKey(name)) {
			String arena = ad.playerStatus.get(name);
			if(ad.flag1Status.getOrDefault(arena, "camp").equalsIgnoreCase(name)
					|| ad.flag2Status.getOrDefault(arena, "camp").equalsIgnoreCase(name)) {
				int team = ad.BelongTeam(name);
				if(new ManageFlag().nearFlag(e.getPlayer().getLocation(), team, arena, plugin)) {
					new ManageFlag().DevoteFlag(e.getPlayer(), plugin);
				}
			}
		}
		
		//spectator
		if(e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)
				&& ad.spectator.containsKey(name)) {
			new MaintainOrder().OrderMove(e, plugin);
		}
	}
	
	//Playerがログアウトするときに発生
	@SuppressWarnings({ "static-access", "rawtypes" })
	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent e) {
		String name = e.getPlayer().getName();
		ArenaData ad = new ArenaData();
		if(ad.playerStatus.containsKey(name)) {
			String arena = ad.playerStatus.get(name);
			int an = ad.arenaStatus.getOrDefault(arena, 0);
			if(an>-2) {
				new ManagePlayer().leave(e.getPlayer(), plugin);
			}
		}
	}
	
	//Respawn時に発生
	@SuppressWarnings({ "static-access", "rawtypes" })
	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent e) {
		ArenaData ad = new ArenaData();
		int team = ad.BelongTeam(e.getPlayer().getName());
		if(team!=0 && ad.arenaStatus.getOrDefault(
				ad.playerStatus.get(e.getPlayer().getName()), 0)>100) {
			String arena = ad.playerStatus.get(e.getPlayer().getName());
			team = (team-1)*3;
			List<Integer> spawnpoint = ad.spawnpoint.get(arena);
			Location l = e.getPlayer().getLocation().clone();
			l.setX(spawnpoint.get(team)+0.5);
			l.setY(spawnpoint.get(team+1)+1.0);
			l.setZ(spawnpoint.get(team+2)+0.5);
			e.setRespawnLocation(l);
			
			e.getPlayer().getInventory().clear();
			List<String> lst = new ArrayList<String>();
			lst.add(e.getPlayer().getName());
			team = ad.BelongTeam(e.getPlayer().getName());
			if(team==1) {
				new ManagePlayer().PaidItems(e.getPlayer().getInventory(), "Inv1.item", plugin, arena, false);
				new ManagePlayer().PaidItems(e.getPlayer().getInventory(), "Inv1.armor", plugin, arena, true);
			}else {
				new ManagePlayer().PaidItems(e.getPlayer().getInventory(), "Inv2.item", plugin, arena, false);
				new ManagePlayer().PaidItems(e.getPlayer().getInventory(), "Inv2.armor", plugin, arena, true);
			}//*/
		}
	}
	
	//Block殴り初めに一回だけ発生
/*	@EventHandler
	public void BlockDamageEvent(BlockDamageEvent e) {

	}//*/
	
	//Playerがコマンド打ったときに発生
	@EventHandler
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		new MaintainOrder().OrderCommand(e, plugin);
	}
	
	//Block設置時に発生
	@SuppressWarnings("static-access")
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		String name = e.getPlayer().getName();
		ArenaData ad = new ArenaData();
		if(ad.playerStatus.containsKey(name)) {
			String arena = ad.playerStatus.get(name);
			int an = ad.arenaStatus.getOrDefault(arena, 0);
			if(an>99) {
				e.setCancelled(true);
			}
		}
	}
	
	//Block破壊時に発生
	@SuppressWarnings("static-access")
	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent e) {
		String name = e.getPlayer().getName();
		CreateArena ca = new CreateArena();
		int cn = ca.getCreateControle(name);
		if(cn!=0 && e.getPlayer().getInventory()
				.getItemInHand().clone().getType().equals(Material.BLAZE_ROD)) {
			if(cn==1) {
				ca.SetFirstPoint(
						e.getBlock().getLocation().clone(), plugin, name);
			}else if(cn==2) {
				ca.SetSecondPoint(
						e.getBlock().getLocation().clone(), plugin, name);
			}else if(cn==3) {
				ca.SetFlag1(
						e.getBlock().getLocation().clone(), plugin, name);
			}else if(cn==4) {
				ca.SetFlag2(
						e.getBlock().getLocation().clone(), plugin, name);
			}else if(cn==5) {
				ca.SetSpawn1(
						e.getBlock().getLocation().clone(), plugin, name);
			}else if(cn==6) {
				ca.SetSpawn2(
						e.getBlock().getLocation().clone(), plugin, name);
			}else if(cn==7) {
				ca.SetEnd(
						e.getBlock().getLocation().clone(), plugin, name);
			}
			e.setCancelled(true);
		}
		
		ArenaData ad = new ArenaData();
		if(ad.playerStatus.containsKey(name)) {
			String arena = ad.playerStatus.get(name);
			int an = ad.arenaStatus.getOrDefault(arena, 0);
			if(an>99) {
				e.setCancelled(true);
			}
		}
		
	}

}

package main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagParticle extends BukkitRunnable{
	
	private String arena;
	private World world;
	private Plugin plugin;
	private int update;
	
	public FlagParticle(String arena, Plugin plugin, World world) {
		this.arena = arena;
		this.world = world;
		this.plugin = plugin;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		ArenaData ad = new ArenaData();
		if(ad.arenaStatus.getOrDefault(arena, 0) < 100) this.cancel();
		
		for(int i=1; i<3; i++) {
			String status;
			if(i==1) {
				status = ad.flag1Status.getOrDefault(arena, "camp");
			}else {
				status = ad.flag2Status.getOrDefault(arena, "camp");
			}
			Location l;
			if(status.equals("camp")) {
				List<Integer> loc = plugin.getConfig().getIntegerList(arena+".flag"+i);
				l = new Location(world, loc.get(0), loc.get(1), loc.get(2));
				if(update==12) {
					update = 0;
					FlyingItem fi;
					if(i==1) { fi = ad.flagArmor1.get(arena); }else { fi = ad.flagArmor2.get(arena); }
					fi.remove();
					createFlag(i);
				}else {
					update++;
				}
			}else {
				String[] args = status.split(", ");
				if(args.length==2) {
					if(i==1) {
						l = ad.flag1Drop.get(arena).getLocation();
					}else {
						l = ad.flag2Drop.get(arena).getLocation();
					}
					
					int count = Integer.valueOf(args[1])-1;
					if(count==0) {
						//back
						FlagBackToCamp(i);
					}else {
						args[1] = String.valueOf(count);
						if(i==1) {
							ad.flag1Status.put(arena, args[0]+", "+args[1]);
						}else {
							ad.flag2Status.put(arena, args[0]+", "+args[1]);
						}
					}
				}else {
					l = Bukkit.getServer().getPlayer(status).getLocation().clone();
				}
			}
			world.playEffect(l, Effect.MOBSPAWNER_FLAMES, 1, 100);//Location Effect, playRadius?, visibleRadius
		}
	}
	
	@SuppressWarnings("static-access")
	private void FlagBackToCamp(int team) {
		ArenaData ad = new ArenaData();
		createFlag(team);
		if(team==1) {
			ad.flag1Drop.get(arena).remove();
			ad.flag1Drop.remove(arena);
			ad.flag1Status.put(arena, "camp");
			Broadcast("RED Flag"+ChatColor.GREEN+" is returned base", ChatColor.RED, arena);
		}else {
			ad.flag2Drop.get(arena).remove();
			ad.flag2Drop.remove(arena);
			ad.flag2Status.put(arena, "camp");
			Broadcast("BLUE Flag"+ChatColor.GREEN+" is returned base", ChatColor.BLUE, arena);
		}
	}
	
	@SuppressWarnings("static-access")
	private void createFlag(int team) {
		ArenaData ad = new ArenaData();
		FlyingItem fi = new FlyingItem();
		List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag"+team);
		fi.SetLocation(new Location(this.world, coordinate.get(0), coordinate.get(1), coordinate.get(2)));
		if(team==1) {
			fi.setItemStack(getFlag(1));
			fi.setText("RED flag");
			ad.flagArmor1.put(arena, fi);
		}else {
			fi.setItemStack(getFlag(2));
			fi.setText("BLUE flag");
			ad.flagArmor2.put(arena, fi);
		}
		fi.spawn(plugin);
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

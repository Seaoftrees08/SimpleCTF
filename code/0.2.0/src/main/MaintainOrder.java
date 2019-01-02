package main;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class MaintainOrder {

	// Cannot break block (in PlayerListener.java)
	
	// Cannot Build block (in PlayerListener.java)
	
	// Cannot type command
	@SuppressWarnings("static-access")
	public void OrderCommand(PlayerCommandPreprocessEvent e, Plugin plugin) {
		String name = e.getPlayer().getName();
		ArenaData ad = new ArenaData();
		if(ad.playerStatus.containsKey(name)) {
			String arena = ad.playerStatus.get(name);
			int an = ad.arenaStatus.getOrDefault(arena, 0);
			if(an>99) {
				String cmd = e.getMessage();
				boolean deny = !(cmd.contains("/sctf") || cmd.contains("/simplectf"));
				for(String s : plugin.getConfig().getStringList("allowCmds")) {
					if(cmd.contains(s)) {
						deny = false;
						break;
					}
				}
				if(deny) {
					e.setCancelled(true);
					SendMessage(e.getPlayer(), ChatColor.ITALIC+"Cannot use this command when playing CTF.", ChatColor.RED);
				}
			}
		}
	}
	
	//Spactator
	@SuppressWarnings("static-access")
	public void OrderMove(PlayerMoveEvent e, Plugin plugin) {
		ArenaData ad = new ArenaData();
		String name = e.getPlayer().getName();
		String arena = ad.spectator.get(name);
		List<Integer> fp = plugin.getConfig().getIntegerList(arena+".firstpoint");
		List<Integer> sp = plugin.getConfig().getIntegerList(arena+".secondpoint");
		for(int i=0; i<3; i++) {
			if(fp.get(i)>sp.get(i)) {
				int tmp = fp.get(i);
				fp.set(i, sp.get(i));
				sp.set(i, tmp);
			}
		}
		double x = e.getPlayer().getLocation().clone().getX();
		double y = e.getPlayer().getLocation().clone().getY();
		double z = e.getPlayer().getLocation().clone().getZ();
		Location l = e.getPlayer().getLocation().clone();
		if(x<fp.get(0)) {
			l.setX(l.getX()+3);
			e.getPlayer().teleport(l);
			SendMessage(e.getPlayer(), "You cannot go outside this arena.", ChatColor.RED);
			
		}else if(sp.get(0)<x) {
			l.setX(l.getX()-3);
			e.getPlayer().teleport(l);
			SendMessage(e.getPlayer(), "You cannot go outside this arena.", ChatColor.RED);
			
		}else if(y<fp.get(1)) {
			l.setY(l.getY()+3);
			e.getPlayer().teleport(l);
			SendMessage(e.getPlayer(), "You cannot go outside this arena.", ChatColor.RED);
			
		}else if(sp.get(1)<y) {
			l.setY(l.getY()-3);
			e.getPlayer().teleport(l);
			SendMessage(e.getPlayer(), "You cannot go outside this arena.", ChatColor.RED);
			
		}else if(z<fp.get(2)) {
			l.setZ(l.getZ()+3);
			e.getPlayer().teleport(l);
			SendMessage(e.getPlayer(), "You cannot go outside this arena.", ChatColor.RED);
			
		}else if(sp.get(2)<z) {
			l.setZ(l.getZ()-3);
			e.getPlayer().teleport(l);
			SendMessage(e.getPlayer(), "You cannot go outside this arena.", ChatColor.RED);
			
		}
	}
	
	
	private void SendMessage(Player p, String message, ChatColor cc) {
		p.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
	}
	
}

package main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MaintainOrder {

	// Cannot break block (in PlayerListener.java)
	
	// Cannot Build block (in PlayerListener.java)
	
	// Cannot type command
	@SuppressWarnings("static-access")
	public void OrderCommand(PlayerCommandPreprocessEvent e) {
		String name = e.getPlayer().getName();
		ArenaData ad = new ArenaData();
		if(ad.playerStatus.containsKey(name)) {
			String arena = ad.playerStatus.get(name);
			int an = ad.arenaStatus.getOrDefault(arena, 0);
			if(an>99) {
				String cmd = e.getMessage();
				if(!cmd.equalsIgnoreCase("/kill")
						&& !cmd.equalsIgnoreCase("/simplectf leave")
						&& !cmd.equalsIgnoreCase("/sctf leave")) {
					e.setCancelled(true);
					SendMessage(e.getPlayer(), ChatColor.ITALIC+"Cannot use this command when playing CTF.", ChatColor.RED);
				}
			}
		}
	}
	
	private void SendMessage(Player p, String message, ChatColor cc) {
		p.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
	}
	
}

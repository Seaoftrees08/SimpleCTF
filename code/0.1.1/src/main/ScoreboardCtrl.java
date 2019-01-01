package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

public class ScoreboardCtrl {
	
	// <arenaName, {team1, team2}>
	private static HashMap<String, Scoreboard> teamBoard = new HashMap<String, Scoreboard>();
	
	public void UpdateScoreboardData(String arena, Scoreboard sb) {
		teamBoard.put(arena, sb);
	}
	
	@SuppressWarnings("static-access")
	public void WaitScoreboardChange(String arena) {
		HashMap<Integer, ArrayList<String>> hm = new ArenaData().playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
		Scoreboard sb = Initialize(arena, hm);
		Objective obj = sb.getObjective(arena);
	
		obj.getScore(ChatColor.GREEN + "  == CTF in " + arena + " == ").setScore(200);
		obj.getScore(" ").setScore(199);
		obj.getScore(ChatColor.RED + " -- RED Team Member -- ").setScore(198);
		obj.getScore(" ").setScore(99);
		obj.getScore(ChatColor.BLUE + " -- BLUE Team Member -- ").setScore(98);//*/
		
		// RED_101~197,  BLUE_1~97
		int i = 101;
		if(hm.get(1)!=null) {
			for(String s : hm.get(1)) {
				obj.getScore(" "+ChatColor.RED+s).setScore(i);
				i++;
			}
		}
		i = 0;
		if(hm.get(2)!=null) {
			for(String s : hm.get(2)) {
				obj.getScore(" "+ChatColor.BLUE+s).setScore(i);
				i++;
			}
		}

		ApplyScoreboard(arena, sb, hm);
	}
	
	@SuppressWarnings("static-access")
	public void PlayScoreBoardChange(String arena) {
		HashMap<Integer, ArrayList<String>> hm = new ArenaData().playerList.getOrDefault(arena, new HashMap<Integer, ArrayList<String>>());
		Scoreboard sb = Initialize(arena, hm);
		Objective obj = sb.getObjective(arena);
		
		List<Integer> points = new ArenaData().arenaPoints.getOrDefault(arena, Arrays.asList(0, 0));
		int[] i = {0, 0};
		if(hm.get(1)!=null) { i[0] = hm.get(1).size(); }
		if(hm.get(2)!=null) { i[1] = hm.get(2).size(); }
		
		// RED_101~197,  BLUE_1~97
		obj.getScore(ChatColor.GREEN + "  == CTF in " + arena + " == ").setScore(300);
		obj.getScore(" ").setScore(299);
		obj.getScore(ChatColor.AQUA + "remaining time: ").setScore(300);
		obj.getScore(" ").setScore(199);
		obj.getScore(ChatColor.RED + " -- RED Team Status -- ").setScore(198);
		obj.getScore(ChatColor.RED + " Score: " + ChatColor.WHITE + points.get(0)).setScore(197);;
		obj.getScore(ChatColor.RED + " Join Players: " + ChatColor.WHITE + i[0]).setScore(196);
		obj.getScore(" ").setScore(99);
		obj.getScore(ChatColor.BLUE + " -- BLUE Team Status -- ").setScore(98);
		obj.getScore(ChatColor.BLUE + " Score: " + ChatColor.WHITE + points.get(1)).setScore(97);;
		obj.getScore(ChatColor.BLUE + " Join Players: " + ChatColor.WHITE + i[1]).setScore(96);

		ApplyScoreboard(arena, sb, hm);
	}
	
	private Scoreboard Initialize(String arena, HashMap<Integer, ArrayList<String>> hm) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		sb.registerNewTeam("team1").setAllowFriendlyFire(false);
		sb.registerNewTeam("team2").setAllowFriendlyFire(false);
		sb.getTeam("team1").setPrefix(ChatColor.RED+"");
		sb.getTeam("team2").setPrefix(ChatColor.BLUE+"");
		Objective obj = sb.registerNewObjective(arena, "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		if(hm.get(1)!=null) {
			for(String s : hm.get(1)) {
				Player player = Bukkit.getServer().getPlayer(s);
				player.getScoreboard().resetScores(ChatColor.RED+player.getName());
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				sb.getTeam("team1").addEntry(player.getName());
			}
		}
		if(hm.get(2)!=null) {
			for(String s : hm.get(2)) {
				Player player = Bukkit.getServer().getPlayer(s);
				player.getScoreboard().resetScores(ChatColor.BLUE+player.getName());
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				sb.getTeam("team2").addEntry(player.getName());
			}
		}
		
		return sb;
	}
	
	private void ApplyScoreboard(String arena, Scoreboard sb, HashMap<Integer, ArrayList<String>> hm) {
		if(hm.get(1)!=null) {
			for(String s : hm.get(1)) {
				Player player = Bukkit.getServer().getPlayer(s);
				player.setScoreboard(sb);
			}
		}
		if(hm.get(2)!=null) {
			for(String s : hm.get(2)) {
				Player player = Bukkit.getServer().getPlayer(s);
				player.setScoreboard(sb);
			}
		}
		teamBoard.put(arena, sb);
	}

}

package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Item;

public class ArenaData {
	
	// <playerName, arenaName>
	public static HashMap<String, String> playerStatus = new HashMap<String, String>();

	// <arenaName, <Team, PlayerList>>
	public static HashMap<String, HashMap<Integer, ArrayList<String>>> playerList
		= new HashMap<String, HashMap<Integer, ArrayList<String>>>();
	
	//Scoreboard setting in ScoreboardCtrl.java
	
	//SpawnPoint <arenaName, {x1 y1 z1, x2, y2, z2}>
	public static HashMap<String, List<Integer>> spawnpoint = new HashMap<String, List<Integer>>();
	
	// <arena, arenaStatusNumber>
	public static HashMap<String, Integer> arenaStatus = new HashMap<String, Integer>();
	/* 
	 * ArenaStatus => 	0orNULL=none
	 * 					-1 = waiting cancelling...
	 * 					6,5,4,3,2,1=waiting player (*10 is timer sec)
	 * 					130, 129, 128....101 = playing (130=>5m0s(30*10s), 129=>2m 50s(29*10s))
	 * 					131 is ready time
	 */
	
	// <arena, flagStatus>
	public static HashMap<String, String> flag1Status = new HashMap<String, String>();
	public static HashMap<String, String> flag2Status = new HashMap<String, String>();
	/*
	 * flagStatus =>	"camp" => on BaseCamp
	 * 					"onGround, count" => onGround (count=>6, 5, 4, 3, 2, 1, 0.  0isReturn. count*5 seconds)
	 * 					playerName => havePlayer 
	 */
	
	//<arena, DropItem> when flag is onGround
	public static HashMap<String, Item> flag1Drop = new HashMap<String, Item>();
	public static HashMap<String, Item> flag2Drop = new HashMap<String, Item>();
	
	// <arena, armor>
	public static HashMap<String, FlyingItem> flagArmor1 = new HashMap<String, FlyingItem>();
	public static HashMap<String, FlyingItem> flagArmor2 = new HashMap<String, FlyingItem>();
	
	// <arena {team1Point, team2Poing}
	public static HashMap<String, List<Integer>> arenaPoints = new HashMap<String, List<Integer>>();
	
	// <playerName arena>
	public static HashMap<String, String> spectator = new HashMap<String, String>();
	
	public int BelongTeam(String name) {
		if(playerStatus.containsKey(name)) {
			ArrayList<String> list = playerList.get(playerStatus.get(name)).get(1);
			for(String s : list) {
				if(s.equalsIgnoreCase(name)) return 1;
			}
			return 2;
		}else {
			return 0;
		}
	}
	
}

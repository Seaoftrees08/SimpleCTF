package main;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor{
	
	private final Main plugin;
	
	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings({ "static-access", "rawtypes" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[]args){
		
		if(cmd.getName().equalsIgnoreCase("simplectf")) {
			
			//	/simplctf
			if(args.length==0) {
				//help sctf
				SendMessage(sender, "   ===== Simple CTF CommandList ===== ", ChatColor.GRAY);
				SendMessage(sender, "/simplectf join <arena>     #Join to <arena>", ChatColor.GRAY);
				SendMessage(sender, "/simplectf leave            #Leave from <arena>", ChatColor.GRAY);
				SendMessage(sender, "/simplectf rate [player]     #Look myrate or [player]rate", ChatColor.GRAY);
				SendMessage(sender, "/simplectf list             #Show arena list", ChatColor.GRAY);
				SendMessage(sender, "/simplectf watch <arena>    #Watch a <arena> game", ChatColor.GRAY);
				SendMessage(sender, "/simplectf back             #back to spawn from Watching game", ChatColor.GRAY);
				SendMessage(sender, "/simplectf version          #view this Plugin Infomation", ChatColor.GRAY);
				SendMessage(sender, "/simplectf admin            #Admin Cmds", ChatColor.GRAY);
				SendMessage(sender, "", ChatColor.GRAY);
				SendMessage(sender, "You can use /sctf instead of /simplectf", ChatColor.GRAY);
				
			}else if(args[0].equalsIgnoreCase("admin") && sender.hasPermission("simplectf.admin")) {
				
				//	/simplectf admin create <arenaName>
				if(args.length>=3 && args[1].equalsIgnoreCase("create") && sender instanceof Player) {
					new CreateArena().MainFlow((Player)sender, args[2], plugin);
					
				//	/simplectf admin setInv
				}else if(args.length==2 && args[1].equalsIgnoreCase("setInv") && sender instanceof Player) {
					final int cn = new CreateArena().getCreateControle(sender.getName());
					if(cn != 0) {
						new CreateArena().MainFlow((Player)sender, args[1], plugin);
					}else {
						SendMessage(sender, "You can use this when creating arena.", ChatColor.RED);
					}
					
				//	/simplectf admin remove <arenaName>
				}else if(args.length==3 && args[1].equalsIgnoreCase("remove")) {
					plugin.getConfig().set(args[2], null);
					List<String> lst = plugin.getConfig().getStringList("List");
					lst.remove(args[2]);
					plugin.getConfig().set("List", lst);
					plugin.saveConfig();
					plugin.reloadConfig();
					SendMessage(sender, "Remove arena", ChatColor.GREEN);
					
				//	/simplectf admin reloadconfig
				}else if(args.length==2 && args[1].equalsIgnoreCase("reloadconfig")) {
					plugin.reloadConfig();
					SendMessage(sender, "Config reloades!", ChatColor.GREEN);
					
				//	/simplectf admin enable <arena>
				}else if(args.length==3 && args[1].equalsIgnoreCase("enable")) {
					if(plugin.getConfig().contains(args[2])){
						plugin.getConfig().set(args[2]+".enable", true);
						plugin.saveConfig();
						plugin.reloadConfig();
						SendMessage(sender, args[2]+" is enabled!", ChatColor.GREEN);
					}else {
						SendMessage(sender, "Cannot lookup arena", ChatColor.RED);
					}
					
				// /simplectf admin disable <arena>
				}else if(args.length==3 && args[1].equalsIgnoreCase("disable")) {
					if(plugin.getConfig().contains(args[2])){
						plugin.getConfig().set(args[2]+".enable", false);
						plugin.saveConfig();
						plugin.reloadConfig();
						SendMessage(sender, args[2]+" is disabled!", ChatColor.GREEN);
					}else {
						SendMessage(sender, "Cannot lookup arena", ChatColor.RED);
					}
					
				}else{
					//help admin cmd
					SendMessage(sender, "   ===== Simple CTF Admin CommandList ===== ", ChatColor.GRAY);
					SendMessage(sender, "/sctf admin create <arena>     #create <arena>", ChatColor.GRAY);
					SendMessage(sender, "/sctf admin remove <arena>     #remove <arena>", ChatColor.GRAY);
					SendMessage(sender, "/sctf admin setInv             #use this when creating arena", ChatColor.GRAY);
					SendMessage(sender, "/sctf admin remove <arena>     #remove <arena>", ChatColor.GRAY);
					SendMessage(sender, "/sctf admin enable <arena>     #enable <arena>", ChatColor.GRAY);
					SendMessage(sender, "/sctf admin disable <arena>    #disable <arena>", ChatColor.GRAY);
				}
				
			//	/simplectf rate <player>
			}else if(args[0].equalsIgnoreCase("rate") && args.length==2 && sender.hasPermission("simplectf.rate")) {
				SendMessage(sender, "This function will come in future version!", ChatColor.GOLD);
				
			//	/simplectf join <arena>
			}else if(args[0].equalsIgnoreCase("join") && args.length==2
					&& sender.hasPermission("simplectf.play") && sender instanceof Player) {
				new ManagePlayer().Join((Player)sender, args[1], plugin);
				
			//	/simplectf leave
			}else if(args[0].equalsIgnoreCase("leave") && sender instanceof Player) {
				new ManagePlayer().leave((Player)sender, plugin);
				
			//	/simplectf list
			}else if(args[0].equalsIgnoreCase("list")){
				List<String> lst = plugin.getConfig().getStringList("List");
				if(lst.size()!=0) {
					SendMessage(sender, "   ===== Arena List =====", ChatColor.GRAY);
					for(String s : lst) SendMessage(sender, s, ChatColor.GRAY);
				}else {
					SendMessage(sender, "Arena don't exist.", ChatColor.GRAY);
				}
				
			//	/simplectf watch <arena>
			}else if(args[0].equalsIgnoreCase("watch") && args.length==2 && sender instanceof Player) {
				SendMessage(sender, "This function will come in future version!", ChatColor.GOLD);
				
			//	/simplectf back
			}else if(args[0].equalsIgnoreCase("back") && args.length==2 && sender instanceof Player) {
				SendMessage(sender, "This function will come in future version!", ChatColor.GOLD);
				
			}else if(args[0].equalsIgnoreCase("version")) {
				SendMessage(sender, "   ===== Simple CTF Infomation ===== ", ChatColor.GRAY);
				SendMessage(sender, "This plugin is to play CTF!", ChatColor.GRAY);
				SendMessage(sender, "version: 0.1.0", ChatColor.GRAY);
				SendMessage(sender, "Author: Seaoftrees (Minecraft ID)", ChatColor.GRAY);
				
			}else {
				SendMessage(sender, "Invalid command."+ChatColor.GRAY+" please type cmd for open list", ChatColor.RED);
				return false;
			}
			return true;
		}else{
			return false;
		}
	}
	
	private void SendMessage(CommandSender cs, String message, ChatColor cc) {
		cs.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
	}

}

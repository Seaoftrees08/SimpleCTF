package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.Arena;
import com.github.seaoftrees08.simplectf.arena.ArenaCreationCause;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.reflection.RefPotionData;
import com.github.seaoftrees08.simplectf.utils.PlayerInventoryItems;
import com.github.seaoftrees08.simplectf.utils.SctfPerms;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor {
    public Commands() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!cmd.getName().equalsIgnoreCase("simplectf")) return false;

        // /simplectf
        if(args.length==0) {
            sendHelp(sender);
            return true;
        }

        // adminコマンド
        if(args[0].equalsIgnoreCase("admin")){
            //権限をお持ちでない場合
            if(!sender.hasPermission(SctfPerms.ADMIN)){
                sendMessage(sender, "You have not enough permission.", ChatColor.RED);
                return true;
            }

            // /simplectf admin create <arena>
            if(args.length>=3 && args[1].equalsIgnoreCase("create") && sender instanceof Player){
                if(getArenaList().contains(args[2])){
                    sendMessage(sender, "This arena already created!", ChatColor.RED);
                }
                ArenaManager.startCreation((Player) sender, args[2]);
                return true;
            }

            // /simplectf admin setInv
            if(args.length==2 && args[1].equalsIgnoreCase("setInv") && sender instanceof Player){
                Player p = (Player) sender;
                ArenaManager.doCreationFlow(sender.getName(), ArenaCreationCause.COMMAND, null, new PlayerInventoryItems(p.getInventory()));
                return true;
            }

            // /simplectf admin setLoc
            if(args.length==2 && args[1].equalsIgnoreCase("setLoc") && sender instanceof Player){
                Player p = (Player) sender;
                ArenaManager.doCreationFlow(sender.getName(), ArenaCreationCause.COMMAND, p.getLocation(), null);
                return true;
            }

            // /simplectf admin remove <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("remove")){
                List<String> arenaList = ArenaManager.loadArenaNameList();
                if(arenaList.contains(args[2])){
                    arenaList.remove(args[2]);
                    SimpleCTF.getSimpleCTF().getConfig().set(ArenaManager.ARENA_LIST_PATH, arenaList);
                    SimpleCTF.getSimpleCTF().saveConfig();
                    new Arena(args[2]).remove();
                }else{
                    sendMessage(sender, args[2] + " is not exist.", ChatColor.GRAY);
                }
                return true;
            }

            // /simplectf admin reloadconfig
            if(args.length==2 && args[1].equalsIgnoreCase("reloadconfig")){
                SimpleCTF.getSimpleCTF().reloadConfig();
                sendMessage(sender, "Config reloaded!", ChatColor.GREEN);
                return true;
            }

            // /simplectf admin enable <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("enable")){
                if(ArenaManager.loadArenaNameList().contains(args[2])){
                    Arena arena = new Arena(args[2]);
                    arena.setEnable(true);
                    arena.save();
                    sendMessage(sender, args[2] + " is enabled!", ChatColor.GREEN);
                }else{
                    sendMessage(sender, args[2] + " is not exist.", ChatColor.GRAY);
                }
                return true;
            }

            // /simplectf admin disable <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("disable")){
                if(ArenaManager.loadArenaNameList().contains(args[2])){
                    Arena arena = new Arena(args[2]);
                    arena.setEnable(false);
                    arena.save();
                    sendMessage(sender, args[2] + " is disabled!", ChatColor.GREEN);
                }else{
                    sendMessage(sender, args[2] + " is not exist.", ChatColor.GRAY);
                }
                return true;
            }

            //	/simplectf admin addCmd <allowCmd>
            if(args.length==3 && args[1].equalsIgnoreCase("addCmd")) {
                //TODO: Add Allow Command
                sendMessage(sender, "This command implementation in Future Update.", ChatColor.GRAY);
                return true;
            }

            //	/simplectf admin rmCmd <allowedCmd>
            if(args.length==3 && args[1].equalsIgnoreCase("rmCmd")){
                //TODO: Remove Allowed Command
                sendMessage(sender, "This command implementation in Future Update.", ChatColor.GRAY);
                return true;
            }

            //  /simplectf admin cmdlist
            if(args.length==2 && args[1].equalsIgnoreCase("cmdList")){
                //TODO: Allow Command list
                sendMessage(sender, "This command implementation in Future Update.", ChatColor.GRAY);
                return true;
            }

            //どの/simplectf admin にも該当しないならヘルプを表示
            sendAdminHelp(sender);
            return true;
        }


        // 一般コマンド
        // /simplectf join <arena>
        if(args.length>=2 && args[0].equalsIgnoreCase("join") && sender.hasPermission(SctfPerms.PLAY)
                && sender instanceof Player){
            //TODO: Arena Join
            return true;
        }

        // /simplectf leave
        if(args[0].equalsIgnoreCase("leave") && sender instanceof Player){
            //TODO: Arena Leave
            return true;
        }

        // /simplectf list
        if(args[0].equalsIgnoreCase("list")){
            //TODO: Arena List
            return true;
        }

        // /simplectf watch
        if(args.length>=2 && args[0].equalsIgnoreCase("watch") && sender instanceof Player){
            //TODO: Watch Arena
            return true;
        }

        // /simplectf back
        if(args[0].equalsIgnoreCase("back") && args.length==1 && sender instanceof Player){
            //TODO: Back
            return true;
        }

        // /simplectf start
        if(args[0].equalsIgnoreCase("start") && args.length==2){
            //TODO: 強制スタート
            return true;
        }

        // /simplectf version
        if(args[0].equalsIgnoreCase("version")){

            Player p = (Player) sender;

            p.sendMessage("debug: ");

//            sendMessage(sender, "   ===== Simple CTF Infomation ===== ", ChatColor.GRAY);
//            sendMessage(sender, "This plugin is to play CTF!", ChatColor.GRAY);
//            sendMessage(sender, "version: "+SimpleCTF.getSimpleCTF().getDescription().getVersion(), ChatColor.GRAY);
//            sendMessage(sender, "Author: Seaoftrees08 (Minecraft ID)", ChatColor.GRAY);
            return true;
        }

        //最後までどれにも該当しない場合ヘルプを表示
        sendHelp(sender);
        return true;
    }

    private void sendMessage(CommandSender cs, String message, ChatColor cc) {
        cs.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
    }

    private void broadcast(String message, ChatColor cc) {
        SimpleCTF.getSimpleCTF().getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
    }

    private void sendHelp(CommandSender sender){
        sendMessage(sender, "   ===== Simple CTF CommandList ===== ", ChatColor.GRAY);
        sendMessage(sender, "/simplectf join <arena>     #Join to <arena>", ChatColor.GRAY);
        sendMessage(sender, "/simplectf leave            #Leave from <arena>", ChatColor.GRAY);
        sendMessage(sender, "/simplectf rate [player]     #Look myrate or [player]rate", ChatColor.GRAY);
        sendMessage(sender, "/simplectf list             #Show arena list", ChatColor.GRAY);
        sendMessage(sender, "/simplectf watch <arena>    #Watch a <arena> game", ChatColor.GRAY);
        sendMessage(sender, "/simplectf back             #back to spawn from Watching game", ChatColor.GRAY);
        sendMessage(sender, "/simplectf version          #view this Plugin Infomation", ChatColor.GRAY);
        sendMessage(sender, "/simplectf start <arena>    #Force start game in <arena>", ChatColor.GRAY);
        sendMessage(sender, "/simplectf admin            #Admin Cmds", ChatColor.GRAY);
        sendMessage(sender, "", ChatColor.GRAY);
        sendMessage(sender, "You can use /sctf instead of /simplectf", ChatColor.GRAY);
    }

    private void sendAdminHelp(CommandSender sender){
        sendMessage(sender, "   ===== Simple CTF Admin CommandList ===== ", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin create <arena>     #create <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin remove <arena>     #remove <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin setInv             #use this when creating arena", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin setLoc             #use this when creating arena", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin enable <arena>     #enable <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin disable <arena>    #disable <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin addCmd <cmd>       #add usable command contain /", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin rmCmd <cmd>        #remove usable command", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin cmdlist            #view usable command list", ChatColor.GRAY);
    }

    private List<String> getArenaList(){
        //TODO: getEnableArena
        return new ArrayList<>();
    }
    private List<String> getEnableArenaList(){
        //TODO: getEnableArena
        return new ArrayList<>();
    }

    private List<String> getDisableArenaList(){
        //TODO: getEnableArena
        return new ArrayList<>();
    }
    private List<String> getCmdList(){
        //TODO: getCmds
        return new ArrayList<>();
    }
}

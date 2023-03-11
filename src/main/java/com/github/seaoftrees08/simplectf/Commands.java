package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.utils.SctfPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

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
                //TODO
                return true;
            }

            // /simplectf admin setInv
            if(args.length==2 && args[1].equalsIgnoreCase("setTeam") && sender instanceof Player){
                //TODO
                return true;
            }

            // /simplectf admin remove <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("remove")){
                //TODO
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
                //TODO
                return true;
            }

            // /simplectf admin disable <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("disable")){
                //TODO
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
            //TODO
            return true;
        }

        // /simplectf leave
        if(args[0].equalsIgnoreCase("leave") && sender instanceof Player){
            Player p = (Player) sender;
            //TODO
            return true;
        }

        // /simplectf list
        if(args[0].equalsIgnoreCase("list")){
            sendMessage(sender, "   ===== Arena List =====", ChatColor.GRAY);
            //TODO
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

        // /simplectf start <arena>
        if(args[0].equalsIgnoreCase("start") && args.length==2){
            //TODO
            return true;
        }

        // /simplectf version
        if(args[0].equalsIgnoreCase("version")){
            sendMessage(sender, "   ===== Simple CTF Infomation ===== ", ChatColor.GRAY);
            sendMessage(sender, "This plugin is to play CTF!", ChatColor.GRAY);
            sendMessage(sender, "version: "+SimpleCTF.getSimpleCTF().getDescription().getVersion(), ChatColor.GRAY);
            sendMessage(sender, "Author: Seaoftrees08 (Minecraft ID)", ChatColor.GRAY);
            return true;
        }

        // /simplectf reset
        if(args[0].equalsIgnoreCase("reset") && sender instanceof Player){
            sendMessage(sender, "reset scoreboard.", ChatColor.GRAY);
            Player p = (Player) sender;
            p.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            return true;
        }

        //最後までどれにも該当しない場合ヘルプを表示
        sendHelp(sender);
        return true;
    }

    private void sendMessage(CommandSender cs, String message, ChatColor cc) {
        cs.sendMessage(ChatColor.AQUA + "[S-CTF] " + cc + message);
    }

    private void sendHelp(CommandSender sender){
        sendMessage(sender, "   ===== Simple CTF CommandList ===== ", ChatColor.GRAY);
        sendMessage(sender, "/simplectf join <arena>     #Join to <arena>", ChatColor.GRAY);
        sendMessage(sender, "/simplectf leave            #Leave from <arena>", ChatColor.GRAY);
        sendMessage(sender, "/simplectf list             #Show arena list", ChatColor.GRAY);
        sendMessage(sender, "/simplectf watch <arena>    #Watch a <arena> game", ChatColor.GRAY);
        sendMessage(sender, "/simplectf back             #back to spawn from Watching game", ChatColor.GRAY);
        sendMessage(sender, "/simplectf version          #view this Plugin Infomation", ChatColor.GRAY);
        sendMessage(sender, "/simplectf start <arena>    #Force start game in <arena>", ChatColor.GRAY);
        sendMessage(sender, "/simplectf reset            #Reset your scoreboard", ChatColor.GRAY);
        sendMessage(sender, "/simplectf admin            #Admin Cmds", ChatColor.GRAY);
        sendMessage(sender, "", ChatColor.GRAY);
        sendMessage(sender, "You can use /sctf instead of /simplectf", ChatColor.GRAY);
    }

    private void sendAdminHelp(CommandSender sender){
        sendMessage(sender, "   ===== Simple CTF Admin CommandList ===== ", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin create <arena>     #create <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin remove <arena>     #remove <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin setTeam            #use this when creating arena", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin enable <arena>     #enable <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin disable <arena>    #disable <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin addCmd <cmd>       #add usable command contain /", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin rmCmd <cmd>        #remove usable command", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin cmdlist            #view usable command list", ChatColor.GRAY);
    }
}

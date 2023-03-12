package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.Arena;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.ArenaPhase;
import com.github.seaoftrees08.simplectf.arena.CreateArena;
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
                if(!ArenaManager.doCreation(args[2], (Player) sender)){
                    sendMessage(sender, "Arena creation failed! because this arena already exist.", ChatColor.RED);
                }
                return true;
            }

            // /simplectf admin setTeam
            if(args.length==2 && args[1].equalsIgnoreCase("setTeam") && sender instanceof Player){
                String arenaName = ArenaManager.getBelongingCreateArenaName(sender.getName());
                ArenaPhase ap = ArenaManager.getCreateArenaPhase(arenaName);
                if(ap.equals(ArenaPhase.RED_TEAM_DATA_SETTING) || ap.equals(ArenaPhase.BLUE_TEAM_DATA_SETTING)){
                    ArenaManager.doCreateFlow(arenaName, null, (Player) sender);
                }else{
                    sendMessage(sender, "You are not creating arena.", ChatColor.GRAY);
                }
                return true;
            }

            // /simplectf admin remove <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("remove")){
                if(ArenaManager.existPlayArena(args[2])){
                    CreateArena ca = new CreateArena(args[2], null);//sendMessageを使わないのでnullを入れている
                    if(ca.isEnable()){
                        sendMessage(sender, "This arena does enabled.", ChatColor.RED);
                        sendMessage(sender, "If you want to remove this arena, please type /sctf admin disable " + args[2], ChatColor.GRAY);
                        return true;
                    }
                    ca.deleteArena();
                    sendMessage(sender, "Arena Removed!", ChatColor.GREEN);
                }else{
                    sendMessage(sender, "This arena does not exist.", ChatColor.GRAY);
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
                if(ArenaManager.existPlayArena(args[2])){
                    CreateArena ca = new CreateArena(args[2], null);//sendMessageを使わないのでnullを入れている
                    ca.setEnable(true);
                    ca.save();
                    sendMessage(sender, ChatColor.GOLD + args[2] + " is enabled!", ChatColor.GREEN);
                }else{
                    sendMessage(sender, "This arena does not exist.", ChatColor.GRAY);
                }
                return true;
            }

            // /simplectf admin disable <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("disable")){
                if(ArenaManager.existPlayArena(args[2])){
                    CreateArena ca = new CreateArena(args[2], null);//sendMessageを使わないのでnullを入れている
                    ca.setEnable(false);
                    ca.save();
                    sendMessage(sender, ChatColor.GOLD + args[2] + " is disabled!", ChatColor.DARK_GREEN);
                }else{
                    sendMessage(sender, "This arena does not exist.", ChatColor.GRAY);
                }
                return true;
            }

            //	/simplectf admin addCmd <arena> <allowCmd>
            if(args.length==4 && args[1].equalsIgnoreCase("addCmd")) {
                if(ArenaManager.existPlayArena(args[2])){
                    CreateArena ca = new CreateArena(args[2], null);//sendMessageを使わないのでnullを入れている
                    ca.addAllowCommand(args[3]);
                    ca.save();
                    sendMessage(sender,"'" + ChatColor.GOLD + args[3] + ChatColor.GREEN
                            + "' are allowed in " + args[2], ChatColor.GREEN);
                }else{
                    sendMessage(sender, "This arena does not exist.", ChatColor.GRAY);
                }
                return true;
            }

            //	/simplectf admin rmCmd <arena> <Cmd>
            if(args.length==4 && args[1].equalsIgnoreCase("rmCmd")){
                if(args[2].contains("/sctf") || args[2].contains("/simplectf")){
                    sendMessage(sender, "This command cannot be deny.", ChatColor.RED);
                    return true;
                }
                if(ArenaManager.existPlayArena(args[2])){
                    CreateArena ca = new CreateArena(args[2], null);//sendMessageを使わないのでnullを入れている
                    ca.removeAllowCommand(args[3]);
                    ca.save();
                    sendMessage(sender,"'" + ChatColor.GOLD + args[3] + ChatColor.DARK_GREEN
                            + "' are denied in " + args[2], ChatColor.DARK_GREEN);
                }else{
                    sendMessage(sender, "This arena does not exist.", ChatColor.GRAY);
                }
                return true;
            }

            //  /sctf admin cmdlist <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("cmdList")){
                if(ArenaManager.existPlayArena(args[2])){
                    CreateArena ca = new CreateArena(args[2], null);//sendMessageを使わないのでnullを入れている
                    sendMessage(sender, "   ===== Allow CommandList in " + args[2] + " =====", ChatColor.GRAY);
                    sendMessage(sender, "/sctf", ChatColor.GRAY);
                    sendMessage(sender, "/simplectf", ChatColor.GRAY);
                    ca.getAllowCommands().forEach(c -> sendMessage(sender, c, ChatColor.GRAY));
                }else{
                    sendMessage(sender, "This arena does not exist.", ChatColor.GRAY);
                }
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
            ArenaManager.join(args[1], (Player) sender);
            return true;
        }

        // /simplectf leave
        if(args[0].equalsIgnoreCase("leave") && sender instanceof Player){
            ArenaManager.leave((Player) sender);
            return true;
        }

        // /simplectf list
        if(args[0].equalsIgnoreCase("list")){
            sendMessage(sender, "   ===== Arena List =====", ChatColor.GRAY);
            ArenaManager.loadArenaNameList().forEach(name -> sendMessage(sender, name, ChatColor.GRAY));
            return true;
        }

        // /simplectf watch <arena>
        if(args.length==2 && args[0].equalsIgnoreCase("watch") && sender instanceof Player){
            ArenaManager.joinSpectator(args[1], (Player) sender);
            return true;
        }

        // /simplectf back
        if(args[0].equalsIgnoreCase("back") && args.length==1 && sender instanceof Player){
            ArenaManager.leaveSpectator(((Player) sender));
            return true;
        }

        // /simplectf start <arena>
        if(args[0].equalsIgnoreCase("start") && args.length==2){
            if(ArenaManager.forceStart(args[1])){
                sendMessage(sender, "Set the countdown to 10 seconds.", ChatColor.GOLD);
            }
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
        sendMessage(sender, "/sctf admin addCmd <arena> <allowCmd> #add usable command contain /", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin rmCmd <arena> <denyCmd>   #remove usable command", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin cmdList <arena>           #view usable command list", ChatColor.GRAY);
    }
}

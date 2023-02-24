package com.github.seaoftrees08.simplectf;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        // admin�R�}���h
        if(args[0].equalsIgnoreCase("admin")){
            //�������������łȂ��ꍇ
            if(!sender.hasPermission(SctfPerms.ADMIN)){
                sendMessage(sender, "You have not enough permission.", ChatColor.RED);
                return true;
            }

            // /simplectf admin create <arena>
            if(args.length>=3 && args[1].equalsIgnoreCase("create") && sender instanceof Player){
                //TODO: Arena Creation
                return true;
            }

            // /simplectf admin setInv
            if(args.length==2 && args[1].equalsIgnoreCase("setInv") && sender instanceof Player){
                //TODO: Arena Creation @setInv
                return true;
            }

            // /simplectf admin remove <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("remove")){
                //TODO: Arena Remove
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
                //TODO: Arena Enable
                return true;
            }

            // /simplectf admin disable <arena>
            if(args.length==3 && args[1].equalsIgnoreCase("disable")){
                //TODO: Arena Disable
                return true;
            }

            //	/simplectf admin addCmd <allowCmd>
            if(args.length==3 && args[1].equalsIgnoreCase("addCmd")) {
                //TODO: Add Allow Command
                return true;
            }

            //	/simplectf admin rmCmd <allowedCmd>
            if(args.length==3 && args[1].equalsIgnoreCase("rmCmd")){
                //TODO: Remove Allowed Command
                return true;
            }

            //  /simplectf admin cmdlist
            if(args.length==2 && args[1].equalsIgnoreCase("cmdList")){
                //TODO: Allow Command list
                return true;
            }

            //�ǂ�/simplectf admin �ɂ��Y�����Ȃ��Ȃ�w���v��\��
            sendAdminHelp(sender);
        }


        // ��ʃR�}���h
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
            //TODO: �����X�^�[�g
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

        //�Ō�܂łǂ�ɂ��Y�����Ȃ��ꍇ�w���v��\��
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
        sendMessage(sender, "/sctf admin enable <arena>     #enable <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin disable <arena>    #disable <arena>", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin addCmd <cmd>       #add usable command contain /", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin rmCmd <cmd>        #remove usable command", ChatColor.GRAY);
        sendMessage(sender, "/sctf admin cmdlist            #view usable command list", ChatColor.GRAY);
    }

}

package com.github.seaoftrees08.simplectf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CmdTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        // /simplectf <‚±‚±‚Ì•âŠ®>
        if(cmd.getName().equalsIgnoreCase("simplectf") && args.length==0){
            List<String> arguments = new ArrayList<>();
            if(sender.hasPermission(SctfPerms.PLAY)) arguments.add("join");
            arguments.add("leave");
            arguments.add("list");
            arguments.add("watch");
            arguments.add("back");
            arguments.add("version");
            if(sender.hasPermission(SctfPerms.FORCE)) arguments.add("start");
            if(sender.hasPermission(SctfPerms.ADMIN)) arguments.add("admin");
            return arguments;
        }

        // /simplectf join <‚±‚±‚Ì•âŠ®>
        if(args.length==1 && args[0].equalsIgnoreCase("join")){
            return getEnableArenaList();
        }

        // /simplectf watch <‚±‚±‚Ì•âŠ®>
        if(args.length==1 && args[0].equalsIgnoreCase("watch")){
            return getEnableArenaList();
        }

        // /simplectf admin <‚±‚±‚Ì•âŠ®>
        if(args.length==1 && args[0].equalsIgnoreCase("admin") && sender.hasPermission(SctfPerms.ADMIN)){
            List<String> arguments = new ArrayList<>();
            arguments.add("create");
            arguments.add("remove");
            arguments.add("setInv");
            arguments.add("enable");
            arguments.add("disable");
            arguments.add("addCmd");
            arguments.add("rmCmd");
            arguments.add("cmdlist");
            return arguments;
        }

        // /simplectf admin remove <‚±‚±‚Ì•âŠ®>
        if(args.length==2 && args[0].equalsIgnoreCase("admin") &&
                args[1].equalsIgnoreCase("remove") && sender.hasPermission(SctfPerms.ADMIN)){
            return getDisableArenaList();
        }

        // /simplectf admin enable <‚±‚±‚Ì•âŠ®>
        if(args.length==2 && args[0].equalsIgnoreCase("admin") &&
                args[1].equalsIgnoreCase("enable") && sender.hasPermission(SctfPerms.ADMIN)){
            return getDisableArenaList();
        }

        // /simplectf admin disable <‚±‚±‚Ì•âŠ®>
        if(args.length==2 && args[0].equalsIgnoreCase("admin") &&
                args[1].equalsIgnoreCase("disable") && sender.hasPermission(SctfPerms.ADMIN)){
            return getEnableArenaList();
        }

        // /simplectf admin rmCmd <‚±‚±‚Ì•âŠ®>
        if(args.length==2 && args[0].equalsIgnoreCase("admin") &&
                args[1].equalsIgnoreCase("rmCmd") && sender.hasPermission(SctfPerms.ADMIN)){
            return getCmdList();
        }

        return null;
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

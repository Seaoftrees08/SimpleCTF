package com.github.seaoftrees08.simplectf.flag;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.ArenaStatus;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagParticle extends BukkitRunnable {

    private String arena;
    private World world;
    private int update;

    public FlagParticle(String arena, World world) {
        this.arena = arena;
        this.world = world;
    }

    @Override
    public void run() {
        PlayArena playArena = ArenaManager.getPlayArena(arena);
        if(playArena.getArenaStatus() != ArenaStatus.PLAYING) this.cancel();

        spawnFlagParticle(playArena.getRedFlagStatus());
        spawnFlagParticle(playArena.getBlueFlagStatus());
    }

    private void spawnFlagParticle(FlagStatus status){
        if(status.equals(FlagStatus.CAMP)){

        }
    }

    @SuppressWarnings("static-access")
    private void FlagBackToCamp(int team) {
        ArenaData ad = new ArenaData();
        createFlag(team);
        if(team==1) {
            ad.flag1Drop.get(arena).remove();
            ad.flag1Drop.remove(arena);
            ad.flag1Status.put(arena, "camp");
            Broadcast("RED Flag"+ChatColor.GREEN+" is returned base", ChatColor.RED, arena);
        }else {
            ad.flag2Drop.get(arena).remove();
            ad.flag2Drop.remove(arena);
            ad.flag2Status.put(arena, "camp");
            Broadcast("BLUE Flag"+ChatColor.GREEN+" is returned base", ChatColor.BLUE, arena);
        }
    }

    @SuppressWarnings("static-access")
    private void createFlag(int team) {
        ArenaData ad = new ArenaData();
        FlyingItem fi = new FlyingItem();
        List<Integer> coordinate = plugin.getConfig().getIntegerList(arena+".flag"+team);
        fi.SetLocation(new Location(this.world, coordinate.get(0), coordinate.get(1), coordinate.get(2)));
        if(team==1) {
            fi.setItemStack(getFlag(1));
            fi.setText("RED flag");
            ad.flagArmor1.put(arena, fi);
        }else {
            fi.setItemStack(getFlag(2));
            fi.setText("BLUE flag");
            ad.flagArmor2.put(arena, fi);
        }
        fi.spawn(plugin);
    }

    private ItemStack getFlag(int team) {
        ItemStack is;
        if(team==1) {
            is = new ItemStack(Material.WOOL, 1, (byte)14);
            is.getItemMeta().setDisplayName(ChatColor.RED+"RED Flag");
        }else {
            is = new ItemStack(Material.WOOL, 1, (byte)11);
            is.getItemMeta().setDisplayName(ChatColor.BLUE+"BLUE Flag");
        }
        List<String> lore = new ArrayList<String>();
        lore.add("bring this flag back to your base flag");
        is.getItemMeta().setLore(lore);

        return is;
    }

    private void Broadcast(String message, ChatColor cc, String arena) {
        Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[S-CTF]" +
                ChatColor.WHITE + "(" + arena + ") " + cc + message);
    }
}

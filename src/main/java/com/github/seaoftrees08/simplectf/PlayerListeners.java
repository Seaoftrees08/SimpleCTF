package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.ArenaPhase;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.github.seaoftrees08.simplectf.arena.TeamColor;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.flag.FlagStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerListeners implements Listener {

    public static HashMap<String, Integer> moveEventCooldown = new HashMap<>();

    public PlayerListeners(SimpleCTF simpleCTF) {
        simpleCTF.getServer().getPluginManager().registerEvents(this, simpleCTF);
    }

    //Playerがブロックを破壊したときに発生
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        String playerName = e.getPlayer().getName();

        //Arena作成時
        if(ArenaManager.isCreating(playerName) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)){
            String arenaName = ArenaManager.getBelongingCreateArenaName(playerName);
            ArenaPhase ap = ArenaManager.getCreateArenaPhase(arenaName);
            if(ap.equals(ArenaPhase.FIRST_POINT_SETTING) || ap.equals(ArenaPhase.SECOND_POINT_SETTING)
                    || ap.equals(ArenaPhase.RED_FLAG_SETTING) || ap.equals(ArenaPhase.BLUE_FLAG_SETTING)){
                ArenaManager.doCreateFlow(arenaName, e.getBlock().getLocation(), null);
                e.setCancelled(true);
            }
        }

        //Play中の破壊禁止
        if(isArenaJoined(e.getPlayer())){
            e.setCancelled(true);
        }

    }

    //Playerがブロックを設置したときに発生
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e){
        //プレイ中の設置禁止
        if(isArenaJoined(e.getPlayer())){
            e.setCancelled(true);
        }
    }

    //Playerがコマンドを送信したときに発生
    @EventHandler
    public void PLayerCommandProcess(PlayerCommandPreprocessEvent e){

        //プレイ中のコマンド禁止
        if(isArenaJoined(e.getPlayer())){
            //絶対除外
            if(e.getMessage().contains("/sctf leave") || e.getMessage().contains("/simplectf leave")) return;
            if(e.getMessage().contains("/sctf start") || e.getMessage().contains("/simplectf start")) return;

            //除外コマンド
            PlayArena pa = ArenaManager.getPlayArena(ArenaManager.whereJoined(e.getPlayer().getName()));
            if(pa != null){
                List<String> cmds = pa.getAllowCommands();
                if(cmds.stream().map(cmd -> e.getMessage().contains(cmd)).toList().contains(true)) return;
            }

            e.setCancelled(true);
        }
    }

    //Playerが死んだときに発生
    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent e){
        String arenaName = ArenaManager.whereJoined(e.getEntity().getName());
        if(!arenaName.equals(ArenaManager.INVALID_ARENA_NAME)){
            String playerName = e.getEntity().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            //RedFlag
            if(tc.equals(TeamColor.RED) && pa.getBlueFlag().hasFlag(playerName)){
                e.getEntity().getInventory().remove(Material.RED_WOOL);
                e.getEntity().getInventory().remove(Material.BLUE_WOOL);
                Location loc = e.getEntity().getLocation();
                Item i = Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, Flag.getBlueFlagItemStack());
                pa.dropBlueFlag(e.getEntity(), i);
            }
            //BlueFlag
            if(tc.equals(TeamColor.BLUE) && pa.getRedFlag().hasFlag(playerName)){
                e.getEntity().getInventory().remove(Material.RED_WOOL);
                e.getEntity().getInventory().remove(Material.BLUE_WOOL);
                Location loc = e.getEntity().getLocation();
                Item i = Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, Flag.getRedFlagItemStack());
                pa.dropRedFlag(e.getEntity(), i);
            }
        }
    }

    //Playerがアイテムを拾ったときに発生
    @EventHandler
    public void PlayerPickupEvent(EntityPickupItemEvent e){
        String arenaName = ArenaManager.whereJoined(e.getEntity().getName());
        if(!arenaName.equals(ArenaManager.INVALID_ARENA_NAME)){
            String playerName = e.getEntity().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            System.out.println("debug: " + Objects.requireNonNull(e.getItem().getItemStack().getItemMeta()).getDisplayName());

            //RedFlagを拾ったとき
            if(e.getItem().getItemStack().getType().equals(Material.RED_WOOL)
                    && Objects.requireNonNull(e.getItem().getItemStack().getItemMeta()).getDisplayName().contains(Flag.RED_FLAG_NAME)){

                if(tc.equals(TeamColor.BLUE)) {
                    pa.pickupRedFlag(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId()));
                }else if(tc.equals(TeamColor.RED)
                        && (pa.getRedFlag().status.equals(FlagStatus.GROUND) || pa.getRedFlag().status.equals(FlagStatus.BRING))){
                    removeCtfItems(Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId())), pa);
                    pa.spawnRedFlagAtBase(false);
                }else{
                    e.setCancelled(true);
                }
            //Blue Flagを拾ったとき
            }else if(e.getItem().getItemStack().getType().equals(Material.BLUE_WOOL)
                    && Objects.requireNonNull(e.getItem().getItemStack().getItemMeta()).getDisplayName().contains(Flag.BLUE_FLAG_NAME)){
                System.out.println("debug: pickup BLUE_FLAG");
                if(tc.equals(TeamColor.RED)) {
                    pa.pickupBlueFlag(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId()));
                }else if(tc.equals(TeamColor.BLUE)
                        && (pa.getBlueFlag().status.equals(FlagStatus.GROUND) || pa.getBlueFlag().status.equals(FlagStatus.BRING))){
                    removeCtfItems(Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId())), pa);
                    pa.spawnBlueFlagAtBase(false);
                }else{
                    e.setCancelled(true);
                }
            }
        }
    }

    //プレイヤーがアイテムを捨てた時に発生
    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent e){
        String arenaName = ArenaManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(ArenaManager.INVALID_ARENA_NAME)){
            String playerName = e.getPlayer().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            //赤旗を捨てた時
            if(e.getItemDrop().getItemStack().getType().equals(Material.RED_WOOL)
                    && Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName().contains("Red Flag")
                    && pa.getRedFlag().hasFlag(playerName)){
                pa.dropRedFlag(e.getPlayer(), e.getItemDrop());
            }
            //青旗を捨てた時
            if(e.getItemDrop().getItemStack().getType().equals(Material.BLUE_WOOL)
                    && Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName().contains("Blue Flag")
                    && pa.getBlueFlag().hasFlag(playerName)){
                pa.dropBlueFlag(e.getPlayer(), e.getItemDrop());
            }
        }
    }

    //プレイヤーが移動時に発生
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){
        String arenaName = ArenaManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(ArenaManager.INVALID_ARENA_NAME)){
            String playerName = e.getPlayer().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            //赤チーム
            if(tc.equals(TeamColor.RED)){
                //頭を強制で赤コンクリに
                if(pa.getPhase().equals(ArenaPhase.PLAYING)) e.getPlayer().getInventory().setHelmet(new ItemStack(Material.RED_CONCRETE));
                //赤旗がキャンプにあって、青旗を持っているときで、CAMP付近 -> 納品
                if(pa.getBlueFlag().hasFlag(playerName)
                        && pa.nearRedFlagFence(e.getPlayer().getLocation())){
                    if(pa.getRedFlag().status.equals(FlagStatus.CAMP)){
                        if(moveEventCooldown.getOrDefault(playerName, 0) <=0 ) pa.takePointRed();
                        e.getPlayer().getInventory().remove(Material.BLUE_WOOL);
                        moveEventCooldown.put(playerName, 10);
                    }else{
                        pa.broadcastRedTeam(ChatColor.LIGHT_PURPLE + "Cannot score because the flag is not in camp!");
                    }
                }
            }
            //青チーム
            if(tc.equals(TeamColor.BLUE)){
                //頭を強制で青コンクリに
                if(pa.getPhase().equals(ArenaPhase.PLAYING)) e.getPlayer().getInventory().setHelmet(new ItemStack(Material.BLUE_CONCRETE));
                //青旗がキャンプにあって、赤旗を持っているときで、CAMP付近 -> 納品
                if(pa.getRedFlag().hasFlag(playerName)
                        && pa.nearBlueFlagFence(e.getPlayer().getLocation())){
                    if(pa.getBlueFlag().status.equals(FlagStatus.CAMP)){
                        if(moveEventCooldown.getOrDefault(e.getPlayer().getName(), 0) <=0 ) pa.takePointBlue();
                        e.getPlayer().getInventory().remove(Material.RED_WOOL);
                        moveEventCooldown.put(e.getPlayer().getName(), 10);
                    }else{
                        pa.broadcastBlueTeam(ChatColor.LIGHT_PURPLE + "Cannot score because the flag is not in camp!");
                    }
                }
            }
            removeCtfItems(e.getPlayer(), pa);
        }

        //moveEventCooldown
        int value = moveEventCooldown.getOrDefault(e.getPlayer().getName(), 0);
        if(value>0){
            moveEventCooldown.put(e.getPlayer().getName(), value-1);
        }else{
            moveEventCooldown.remove(e.getPlayer().getName());
        }

        //TODO:Spectator
    }

    //プレイヤーがリスポするときに発生
    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent e){
        String arenaName = ArenaManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(ArenaManager.INVALID_ARENA_NAME)) {
            String playerName = e.getPlayer().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            switch (tc) {
                case RED -> {
                    e.setRespawnLocation(pa.getRedRespawnLocation());
                    pa.whenSpawn(e.getPlayer());
                }
                case BLUE ->{
                    e.setRespawnLocation(pa.getBlueRespawnLocation());
                    pa.whenSpawn(e.getPlayer());
                }
                default -> {}
            }
        }
    }

    //プレイヤーがログアウトするときに発生
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e){
        String arenaName = ArenaManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(ArenaManager.INVALID_ARENA_NAME)) {
            ArenaManager.leave(e.getPlayer());
        }
    }

    private static boolean isArenaJoined(Player player){
        return !ArenaManager.whereJoined(player.getName()).equals(ArenaManager.INVALID_ARENA_NAME);
    }

    private void removeCtfItems(Player p, PlayArena pa){
        p.getInventory().remove(Material.RED_CONCRETE);
        p.getInventory().remove(Material.BLUE_CONCRETE);

        //赤旗
        if(pa.getRedFlag().hasFlag(p.getName())){
            //赤旗を2つ以上持っている場合
            Optional<ItemStack> ois = Arrays.stream(p.getInventory().getContents()).filter(is -> is.getType().equals(Material.RED_WOOL)).findFirst();
            if(ois.isPresent() && ois.get().getAmount() > 1){
                p.getInventory().remove(Material.RED_WOOL);
                p.getInventory().addItem(Flag.getRedFlagItemStack());
            }
        }else{
            p.getInventory().remove(Material.RED_WOOL);
        }

        //青旗
        if(pa.getBlueFlag().hasFlag(p.getName())){
            //赤旗を2つ以上持っている場合
            Optional<ItemStack> ois = Arrays.stream(p.getInventory().getContents()).filter(is -> is.getType().equals(Material.BLUE_WOOL)).findFirst();
            if(ois.isPresent() && ois.get().getAmount() > 1){
                p.getInventory().remove(Material.BLUE_WOOL);
                p.getInventory().addItem(Flag.getBlueFlagItemStack());
            }
        }else{
            p.getInventory().remove(Material.BLUE_WOOL);
        }

    }
}

package com.github.seaoftrees08.simplectf;

import com.github.seaoftrees08.simplectf.arena.ArenaCreationCause;
import com.github.seaoftrees08.simplectf.arena.ArenaManager;
import com.github.seaoftrees08.simplectf.arena.ArenaStatus;
import com.github.seaoftrees08.simplectf.arena.PlayArena;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.flag.FlagStatus;
import com.github.seaoftrees08.simplectf.team.PlayerManager;
import com.github.seaoftrees08.simplectf.team.TeamColor;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class PlayerListeners implements Listener {

    public static HashMap<String, Integer> moveEventCooldown = new HashMap<>();

    public PlayerListeners(SimpleCTF simpleCTF) {
        simpleCTF.getServer().getPluginManager().registerEvents(this, simpleCTF);
    }

    //Playerがブロックを壊したときに発生
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        String name = e.getPlayer().getName();

        //Arena作成時
        if(ArenaManager.isCreating(name) && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)){
            ArenaManager.doCreationFlow(name, ArenaCreationCause.EVENT, e.getBlock().getLocation(), null);
            e.setCancelled(true);
        }

        //プレイ中の破壊禁止
        if(PlayerManager.isJoined(name)){
            e.setCancelled(true);
        }
    }

    //Playerがブロックを設置したときに発生
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e){
        //プレイ中の設置禁止
        if(PlayerManager.isJoined(e.getPlayer().getName())){
            e.setCancelled(true);
        }
    }

    //Playerがコマンドを送信したときに発生
    @EventHandler
    public void PLayerCommandProcess(PlayerCommandPreprocessEvent e){
        //プレイ中のコマンド禁止
        if(PlayerManager.isJoined(e.getPlayer().getName())){
            //除外
            if(e.getMessage().contains("/sctf leave") || e.getMessage().contains("/simplectf leave")) return;
            if(e.getMessage().contains("/sctf start") || e.getMessage().contains("/simplectf start")) return;

            e.setCancelled(true);
        }
    }

    //Playerが死んだときに発生
    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent e){
        String arenaName = PlayerManager.whereJoined(e.getEntity().getName());
        if(!arenaName.equals(PlayerManager.NONE)){
            String playerName = e.getEntity().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            //RedFlag
            if(tc.equals(TeamColor.RED) && pa.hasBlueFlag(playerName)){
                e.getEntity().getInventory().remove(Material.RED_WOOL);
                e.getEntity().getInventory().remove(Material.BLUE_WOOL);
                Location loc = e.getEntity().getLocation();
                Item i = Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, Flag.getBlueFlagItemStack());
                pa.dropBlueFlag(e.getEntity(), i);
            }
            if(tc.equals(TeamColor.BLUE) && pa.hasRedFlag(playerName)){
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
        String arenaName = PlayerManager.whereJoined(e.getEntity().getName());
        if(!arenaName.equals(PlayerManager.NONE)){
            String playerName = e.getEntity().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            //RedFlagを拾ったとき
            if(e.getItem().getItemStack().getType().equals(Material.RED_WOOL)
                && Objects.requireNonNull(e.getItem().getItemStack().getItemMeta()).getDisplayName().contains("Red Flag")){
                if(tc.equals(TeamColor.BLUE)) {
                    pa.pickupRedFlag(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId()));
                }else if(tc.equals(TeamColor.RED)
                        && (pa.getRedFlagStatus().equals(FlagStatus.GROUND) || pa.getRedFlagStatus().equals(FlagStatus.BRING))){
                    removeCtfItems(Objects.requireNonNull(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId())), pa);
                    pa.spawnRedFlagAtBase(false);
                }else{
                    e.setCancelled(true);
                }
            //Blue Flagを拾ったとき
            }else if(e.getItem().getItemStack().getType().equals(Material.BLUE_WOOL)
                    && Objects.requireNonNull(e.getItem().getItemStack().getItemMeta()).getDisplayName().contains("Blue Flag")){
                if(tc.equals(TeamColor.RED)) {
                    pa.pickupBlueFlag(SimpleCTF.getSimpleCTF().getServer().getPlayer(e.getEntity().getUniqueId()));
                }else if(tc.equals(TeamColor.BLUE)
                        && (pa.getBlueFlagStatus().equals(FlagStatus.GROUND) || pa.getBlueFlagStatus().equals(FlagStatus.BRING))){
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
        String arenaName = PlayerManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(PlayerManager.NONE)){
            String playerName = e.getPlayer().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            //赤旗を捨てた時
            if(e.getItemDrop().getItemStack().getType().equals(Material.RED_WOOL)
                    && Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName().contains("Red Flag")
                    && pa.hasRedFlag(playerName)){
                pa.dropRedFlag(e.getPlayer(), e.getItemDrop());
            }
            //青旗を捨てた時
            if(e.getItemDrop().getItemStack().getType().equals(Material.BLUE_WOOL)
                    && Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName().contains("Blue Flag")
                    && pa.hasBlueFlag(playerName)){
                pa.dropBlueFlag(e.getPlayer(), e.getItemDrop());
            }
        }
    }

    //プレイヤーが移動時に発生
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){
        String arenaName = PlayerManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(PlayerManager.NONE)){
            String playerName = e.getPlayer().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            //赤チーム
            if(tc.equals(TeamColor.RED)){
                //頭を強制で赤コンクリに
                if(pa.getArenaStatus().equals(ArenaStatus.PLAYING)) e.getPlayer().getInventory().setHelmet(new ItemStack(Material.RED_CONCRETE));
                //赤旗がキャンプにあって、青旗を持っているときで、CAMP付近 -> 納品
                if(pa.hasBlueFlag(e.getPlayer().getName())
                        && pa.nearRedFlagFence(e.getPlayer().getLocation())){
                    if(pa.getRedFlagStatus().equals(FlagStatus.CAMP)){
                        if(moveEventCooldown.getOrDefault(e.getPlayer().getName(), 0) <=0 ) pa.takePointRed();
                        e.getPlayer().getInventory().remove(Material.BLUE_WOOL);
                        moveEventCooldown.put(e.getPlayer().getName(), 10);
                    }else{
                        pa.broadcastRedTeam(ChatColor.LIGHT_PURPLE + "Cannot score because the flag is not in camp!");
                    }
                }
            }
            //青チーム
            if(tc.equals(TeamColor.BLUE)){
                //頭を強制で青コンクリに
                if(pa.getArenaStatus().equals(ArenaStatus.PLAYING)) e.getPlayer().getInventory().setHelmet(new ItemStack(Material.BLUE_CONCRETE));
                //青旗がキャンプにあって、赤旗を持っているときで、CAMP付近 -> 納品
                if(pa.hasRedFlag(e.getPlayer().getName())
                        && pa.nearBlueFlagFence(e.getPlayer().getLocation())){
                    if(pa.getBlueFlagStatus().equals(FlagStatus.CAMP)){
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
        String arenaName = PlayerManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(PlayerManager.NONE)) {
            String playerName = e.getPlayer().getName();
            PlayArena pa = ArenaManager.getPlayArena(arenaName);
            TeamColor tc = pa.getPlayerTeamColor(playerName);
            switch (tc) {
                case RED -> {
                    pa.getRedInv().setInventory(e.getPlayer());
                    e.setRespawnLocation(pa.getRedRespawnLocation());
                }
                case BLUE ->{
                    pa.getBlueInv().setInventory(e.getPlayer());
                    e.setRespawnLocation(pa.getBlueRespawnLocation());
                }
                default -> {}
            }
        }
    }

    //プレイヤーがログアウトするときに発生
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e){
        String arenaName = PlayerManager.whereJoined(e.getPlayer().getName());
        if(!arenaName.equals(PlayerManager.NONE)) {
            String playerName = e.getPlayer().getName();
            PlayerManager.leave(playerName);
        }
    }

    private void removeCtfItems(Player p, PlayArena pa){
        p.getInventory().remove(Material.RED_CONCRETE);
        p.getInventory().remove(Material.BLUE_CONCRETE);

        //赤旗
        if(pa.hasRedFlag(p.getName())){
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
        if(pa.hasBlueFlag(p.getName())){
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

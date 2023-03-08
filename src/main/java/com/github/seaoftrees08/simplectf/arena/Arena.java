package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.utils.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    public final static String RED_TEAM = "Red Team";
    public final static String BLUE_TEAM = "Blue Team";
    public final String arenaName;
    private final FileConfiguration yml;
    private final File file;
    protected Cuboid arenaField;
    protected ArenaTeam redTeam;
    protected ArenaTeam blueTeam;
    protected Flag redFlag;
    protected Flag blueFlag;
    protected Scoreboard scoreboard;
    protected int remTime = 0;
    protected ArenaPhase phase = ArenaPhase.NONE;
    protected boolean enable = false;
    protected ArenaTeam spectators = new ArenaTeam(TeamColor.SPECTATOR, new StoredPlayerData());

    /**
     * CreateArenaにて使われるコンストラクタ
     * 区別のためにbooleanの値をとっている、まあ使ってないけど
     * @param uniqueName
     * @param isCreation
     */
    protected Arena(String uniqueName, boolean isCreation){
        this.arenaName = uniqueName;
        file = new File(SimpleCTF.getSimpleCTF().getDataFolder(), arenaName+ ".yml");
        yml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * アリーナを作成、読み込む
     * これはArenaManagerから呼ばれるもので、config上で存在するアリーナ名(=実在するもの)のみが使われることを前提とする
     * @param arenaName
     */
    public Arena(String arenaName){
        this(arenaName, false);

        //load arena
        //arena field
        arenaField = new Cuboid(new Vec3i(yml.getIntegerList(ArenaYamlPath.FIRST_POINT)), new Vec3i(yml.getIntegerList(ArenaYamlPath.SECOND_POINT)));

        //StoredPlayerData for redTeam
        ArrayList<ArenaItemStack> redMainItems = new ArrayList<>();
        if(yml.getList(ArenaYamlPath.RED_INV_ITEMS) != null){
            for(List<String> lst : (List<ArrayList<String>>) yml.getList(ArenaYamlPath.RED_INV_ITEMS)){
                redMainItems.add(new ArenaItemStack(lst));
            }
        }
        StoredPlayerData red_spd = new StoredPlayerData(
                redMainItems,
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.RED_INV_HELMET)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.RED_INV_CHEST_PLATE)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.RED_INV_LEGGINGS)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.RED_INV_BOOTS)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.RED_INV_OFFHAND)),
                new LocationStringList(yml.getStringList(ArenaYamlPath.RED_SPAWN))
        );
        redTeam = new ArenaTeam(TeamColor.RED, red_spd);

        //StoredPlayerData for blueTeam
        ArrayList<ArenaItemStack> blueMainItems = new ArrayList<>();
        if(yml.getList(ArenaYamlPath.BLUE_INV_ITEMS) != null){
            for(List<String> lst : (List<ArrayList<String>>) yml.getList(ArenaYamlPath.BLUE_INV_ITEMS)){
                blueMainItems.add(new ArenaItemStack(lst));
            }
        }
        StoredPlayerData blue_spd = new StoredPlayerData(
                blueMainItems,
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.BLUE_INV_HELMET)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.BLUE_INV_CHEST_PLATE)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.BLUE_INV_LEGGINGS)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.BLUE_INV_BOOTS)),
                new ArenaItemStack(yml.getStringList(ArenaYamlPath.BLUE_INV_OFFHAND)),
                new LocationStringList(yml.getStringList(ArenaYamlPath.BLUE_SPAWN))
        );
        blueTeam = new ArenaTeam(TeamColor.BLUE, blue_spd);

        //redFlag
        redFlag = new Flag(TeamColor.RED, new LocationStringList(yml.getStringList(ArenaYamlPath.RED_FLAG)).getLocation());

        //blueFlag
        blueFlag = new Flag(TeamColor.BLUE, new LocationStringList(yml.getStringList(ArenaYamlPath.BLUE_FLAG)).getLocation());

        //scoreboard
        //phaseに分けて各タイミングで実行するため、コンストラクタでは未定義

    }

    /**
     * プレイヤーが本アリーナに参加していない場合は、参加させる
     * このプレイヤーは他のアリーナには所属していないものとする.
     * ただし、本アリーナに所属しているかどうかはわからない.
     *
     * @param player 所属させるプレイヤー
     * @return すでに所属している -> false、どこにも所属せず正式に参加できた -> true
     */
    public boolean join(Player player){
        if(redTeam.isBelonging(player.getName()) || blueTeam.isBelonging(player.getName())) return false;

        if(redTeam.getArenaPlayerList().size() < blueTeam.getArenaPlayerList().size()){
            redTeam.addMember(new ArenaPlayer(player));
        }else{
            blueTeam.addMember(new ArenaPlayer(player));
        }
        player.getInventory().clear();

        if(redTeam.getArenaPlayerList().size()>=1 && blueTeam.getArenaPlayerList().size()>=1
                && (phase.equals(ArenaPhase.NONE) || phase.equals(ArenaPhase.FINISHED))){
            //TODO: clock work
        }
        return true;
    }

    /**
     * このアリーナからプレイヤーを退場させる
     * このプレイヤーが本アリーナに所属していない場合`null`が返される.
     * インベントリの修復等もここで行われる
     * @param player 退場させるプレイヤー
     * @return 退場させたArenaPlayer (本アリーナに所属していない場合`null`)
     */
    public ArenaPlayer leave(Player player){
        if(redTeam.isBelonging(player.getName()) || blueTeam.isBelonging(player.getName())) return null;

        ArenaPlayer arenaPlayer = redTeam.removeMember(player.getName());
        if(arenaPlayer == null) arenaPlayer = blueTeam.removeMember(player.getName());
        if(arenaPlayer == null) return null;

        arenaPlayer.setInventory(player);
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.teleport(arenaPlayer.getLocationStringList().getLocation());

        //TODO:arenaの事後処理

        return arenaPlayer;
    }


}

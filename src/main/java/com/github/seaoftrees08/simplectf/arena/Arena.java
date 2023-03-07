package com.github.seaoftrees08.simplectf.arena;

import com.github.seaoftrees08.simplectf.SimpleCTF;
import com.github.seaoftrees08.simplectf.flag.Flag;
import com.github.seaoftrees08.simplectf.flag.FlagItem;
import com.github.seaoftrees08.simplectf.utils.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
     * これはArenaManagerから呼ばれるもので、config上で存在するアリーナ名のみが使われることを前提とする
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
        //TODO
        return false;
    }

    /**
     * このアリーナからプレイヤーを退場させる
     * このプレイヤーが本アリーナに所属していない場合`null`が返される.
     * @param playerName 退場させるプレイヤー名
     * @return 退場させたArenaPlayer (本アリーナに所属していない場合`null`)
     */
    public ArenaPlayer leave(String playerName){
        ArenaPlayer player = redTeam.removeMember(playerName);
        if(player == null) player = blueTeam.removeMember(playerName);
        if(player == null) return null;
        //TODO
        return player;
    }


}

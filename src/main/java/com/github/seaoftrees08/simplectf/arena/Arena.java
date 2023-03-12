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
    protected final FileConfiguration yml;
    protected final File file;
    protected Cuboid arenaField;
    protected ArenaTeam redTeam;
    protected ArenaTeam blueTeam;
    protected Flag redFlag;
    protected Flag blueFlag;
    protected ArenaPhase phase = ArenaPhase.NONE;
    protected boolean enable = false;
    protected List<String> allowCommands = new ArrayList<>();

    /**
     * CreateArenaにて使われるコンストラクタ
     * 区別のためにbooleanの値をとっている、まあ使ってないけど
     * @param uniqueName アリーナ名. 使用されていないことを要請する
     * @param isCreation 区別のためにbooleanの値をとっている、まあ使ってないけど(2回目)
     */
    protected Arena(String uniqueName, boolean isCreation){
        this.arenaName = uniqueName;
        file = new File(SimpleCTF.getSimpleCTF().getDataFolder(), arenaName+ ".yml");
        yml = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * アリーナを作成、読み込む
     * これはArenaManagerから呼ばれるもので、config上で存在するアリーナ名(=実在するもの)のみが使われることを前提とする
     * @param arenaName 読み込むアリーナ名
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

        //enable
        enable = yml.getBoolean(ArenaYamlPath.ENABLE);

        //allow commands
        allowCommands = yml.getStringList(ArenaYamlPath.ALLOW_COMMANDS);
    }

    public List<String> getAllowCommands(){
        return allowCommands;
    }

    public boolean isEnable(){
        return enable;
    }

}

package com.github.seaoftrees08.simplectf.utils;

import com.github.seaoftrees08.simplectf.reflection.RefPotionData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaItemStack extends ItemStack {
    public ArenaItemStack(){
        super(new ItemStack(Material.AIR));
    }
    public ArenaItemStack(ItemStack is){
        super(nullRemoval(is));
    }

    public ArenaItemStack(List<String> lst){
        super(listToItem(lst));
    }

    public List<String> getStringList(){
        return itemToList(this);
    }

    public ItemStack getItemStack(){
        return this;
    }

    public int isAir(){
        return getType().equals(Material.AIR) ? 1 : 0;
    }

    /**
     * nullかどうかわからないItemSTackをnull safeにしてくれる子
     * nullはAIRとなる
     * @param i null除去するItemStack
     * @return null除去されたItemStack
     */
    @Nonnull
    public static ItemStack nullRemoval(ItemStack i){
        return i == null ? new ItemStack(Material.AIR) : i;
    }

    /**
     * ItemStackをYAMLとして保存する用のListにする
     *
     * @param i 対象となるItemStack
     * @return List化したItemStack
     */
    private static List<String> itemToList(ItemStack i){
        ArrayList<String> lst = new ArrayList<>();

        if(i==null){
            lst.add(Material.AIR.name());
            lst.add("0");
            lst.add("");
            lst.add("");
        }

        //Name and Amount
        lst.add(i.getType().name());
        lst.add(String.valueOf(i.getAmount()));

        //ItemMeta
        if(i.hasItemMeta()){
            String s = "";
            for(Enchantment en : i.getItemMeta().getEnchants().keySet()){
                s += en.getKey() + ", " + i.getItemMeta().getEnchants().getOrDefault(en, 0) + ", ";
            }
            lst.add(s);
        }else{
            lst.add("");
        }

        //Potion
        if((i.getType().equals(Material.POTION) || i.getType().equals(Material.SPLASH_POTION) || i.getType().equals(Material.LINGERING_POTION))
                && i.hasItemMeta()){
            PotionMeta pm = (PotionMeta)i.getItemMeta();
            PotionData pd = pm.getBasePotionData();
            lst.add(pd.getType() + ", " + pd.isExtended() + ", " + pd.isUpgraded());//Extend->時間, Upgrade->II
        }else{
            lst.add("");
        }

        return lst;
    }

    /**
     * ListからItemStackを作る
     * Listは下記の順が保証されているものとする(さもなくばAIRを返す)
     * ただし、Index=1, 2は空の場合がある
     * 0: Material名
     * 1: 個数(数字)
     * 2: MetaData(EnchantName1, Level, EnchantName2, Level, ...)
     * 3: PoritonData(PotionType, Extended, Upgraded)
     *
     * @param lst 変換元のリスト
     * @return 変換したItemsStack
     */
    private static ItemStack listToItem(List<String> lst){
        if(lst==null || lst.size()<=3){
            return new ItemStack(Material.AIR);
        }

        //setType
        ItemStack i = new ItemStack(Material.valueOf(lst.get(0)));

        //setAmount
        i.setAmount(Integer.parseInt(lst.get(1)));

        //setEnchant
        ItemMeta im = i.getItemMeta();
        String[] ss = lst.get(2).split(", ");
        boolean hasEnchant = false;
        for(int j=0; j<ss.length; j+=2){
            if(ss[j].equals("")) break;
            assert im != null;
            im.addEnchant(
                    Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.fromString(ss[j]))),
                    Integer.parseInt(ss[j+1]),
                    true
            );
            hasEnchant = true;
        }
        if(hasEnchant) i.setItemMeta(im);

        //setPortion
        if((i.getType().equals(Material.POTION) || i.getType().equals(Material.SPLASH_POTION) || i.getType().equals(Material.LINGERING_POTION))){
            ss = lst.get(3).split(", ");
            PotionData pd = new RefPotionData(PotionType.valueOf(ss[0]), Boolean.parseBoolean(ss[1]), Boolean.parseBoolean(ss[2])).getPotionData();
            PotionMeta pm = (PotionMeta) im;
            assert pm != null;
            pm.setBasePotionData(pd);
            i.setItemMeta(pm);
        }

        return i;
    }
}

package com.github.seaoftrees08.simplectf.reflection;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * org.bukkit.potion.PotionData が PotionType.isExtendable等がtrueじゃないと使えないのに
 * それしか使わせてもらえない欠陥品だったのでこれを実装。
 * 例：PotionData pd = new PotionData(PotionType.FIRE_RESISTANCE, false, false); //Error
 */
public class RefPotionData {

    private final PotionType type;
    private final boolean extended;
    private final boolean upgraded;

    public RefPotionData(PotionType type, boolean extended, boolean upgraded){
        this.type = type;
        this.extended = type.isExtendable() && extended;
        this.upgraded = type.isUpgradeable() && upgraded;
    }

    public PotionData getPotionData(){

        try {
            Class<?> pdClass = Class.forName("org.bukkit.potion.PotionData");
            Constructor<?> constructor = pdClass.getConstructor(PotionType.class, boolean.class, boolean.class);
            Object pdObj = constructor.newInstance(type, extended, upgraded);

            return (PotionData) pdObj;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}

package com.github.seaoftrees08.simplectf.flag;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class FlagItem {
    private ArmorStand armorstand;
    private Location location;
    private String text = null;
    private ItemStack itemstack;

    private boolean h = false;

    public FlagItem(Location location, String text, ItemStack itemstack){
        setLocation(location);
        setText(text);
        setItemStack(itemstack);
    }

    public void setLocation(Location location) {
        Location l = location.clone();
        l.setX(l.getX()+0.5);
        l.setZ(l.getZ()+0.5);
        this.location = l;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setItemStack(ItemStack itemstack) {
        this.itemstack = itemstack;
    }

    public void kill() {
        this.location = null;
        if(this.armorstand!=null) this.armorstand.remove();
        assert this.armorstand != null;
        if(this.armorstand.getPassengers().size() != 0) this.armorstand.getPassengers().forEach(it -> this.armorstand.removePassenger(it));
        this.armorstand = null;
        this.text = null;
        this.itemstack = null;
    }

    public void spawn() {
        if(!h) {
            this.location.setY(this.location.getY());
            h = true;
        }
        armorstand = Objects.requireNonNull(this.location.getWorld()).spawn(this.location, ArmorStand.class);
        armorstand.setGravity(false);
        armorstand.setVisible(false);
        Item i = this.location.getWorld().dropItem(this.getLocation(), this.getItemStack());
        i.setPickupDelay(0);
        if(this.text != null) {
            i.setCustomName(this.text);
            i.setCustomNameVisible(true);
        }
        armorstand.addPassenger(i);
    }

    public Location getLocation() {
        return this.location;
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }

}

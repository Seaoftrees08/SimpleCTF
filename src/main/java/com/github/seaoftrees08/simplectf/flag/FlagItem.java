package com.github.seaoftrees08.simplectf.flag;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class FlagItem {
    private ArmorStand armorstand;
    private Location fenceLocation;
    private String text = null;
    private ItemStack itemstack;

    private boolean h = false;

    public FlagItem(Location fenceLocation, String text, ItemStack itemstack){
        setFenceLocation(fenceLocation);
        setText(text);
        setItemStack(itemstack);
    }

    public void setFenceLocation(Location fenceLocation) {
        Location l = fenceLocation.clone();
        l.setX(l.getX()+0.5);
        l.setZ(l.getZ()+0.5);
        this.fenceLocation = l;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setItemStack(ItemStack itemstack) {
        this.itemstack = itemstack;
    }

    public void kill() {
        this.fenceLocation = null;
        if(this.armorstand!=null) this.armorstand.remove();
        assert this.armorstand != null;
        if(this.armorstand.getPassengers().size() != 0) this.armorstand.getPassengers().forEach(it -> this.armorstand.removePassenger(it));
        this.armorstand = null;
        this.text = null;
        this.itemstack = null;
    }

    public void spawn() {
        if(!h) {
            this.fenceLocation.setY(this.fenceLocation.getY());
            h = true;
        }
        armorstand = Objects.requireNonNull(this.fenceLocation.getWorld()).spawn(this.fenceLocation, ArmorStand.class);
        armorstand.setGravity(false);
        armorstand.setVisible(false);
        Item i = this.fenceLocation.getWorld().dropItem(this.getFenceLocation(), this.getItemStack());
        i.setPickupDelay(0);
        if(this.text != null) {
            i.setCustomName(this.text);
            i.setCustomNameVisible(true);
        }
        armorstand.addPassenger(i);
    }

    public Location getFenceLocation() {
        return this.fenceLocation.clone();
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }
}

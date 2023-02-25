package com.github.seaoftrees08.simplectf.flag;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class Flag {
    private ArmorStand armorstand;
    private Location location;
    private String text = null;
    private boolean h = false;
    private ItemStack itemstack;

    public void SetLocation(Location location) {
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

    public void remove() {
        this.location = null;
        if(this.armorstand!=null) this.armorstand.remove();
        assert this.armorstand != null;
        if(this.armorstand.getPassengers().size() != 0) this.armorstand.getPassengers().forEach(it -> this.armorstand.removePassenger(it));
        this.armorstand = null;
        this.h = false;
        this.text = null;
        this.itemstack = null;
    }

    public void teleport(Location location) {
        if(this.location != null) {
            armorstand.teleport(location);
            this.location = location;
        }
    }

    public void spawn() {
        if(!h) {
            this.location.setY(this.location.getY());
            h = true;
        }
        armorstand = (ArmorStand)this.location.getWorld().spawn(this.location, ArmorStand.class);
        armorstand.setGravity(false);
        armorstand.setVisible(false);
        Item i = this.location.getWorld().dropItem(this.getLocation(), this.getItemStack());
        i.setPickupDelay(0);
        if(this.text != null) {
            i.setCustomName(this.text);
            i.setCustomNameVisible(true);
        }
        armorstand.setPassenger(i);
    }

    public Location getLocation() {
        return this.location;
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }

    public String getText() {
        return this.text;
    }
}

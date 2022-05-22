package com.github.idimabr.objects;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BackupEntity{

    private int id;
    private String name;
    private EntityType type;
    private Location location;
    private boolean hostile;
    private int timer;
    private List<Pair<String, Double>> attributes = Lists.newArrayList();
    private List<Pair<Integer, ItemStack>> drops = Lists.newArrayList();

    public BackupEntity(int id, boolean hostile, String name, EntityType type, Location location, int timer, List<Pair<String, Double>> attributes, List<Pair<Integer, ItemStack>> drops) {
        this.id = id;
        this.hostile = hostile;
        this.name = name;
        this.type = type;
        this.location = location;
        this.timer = timer;
        this.attributes = attributes;
        this.drops = drops;
    }

    public BackupEntity(int id, boolean hostile, EntityType type, Location location, int timer, List<Pair<String, Double>> attributes, List<Pair<Integer, ItemStack>> drops) {
        this.id = id;
        this.hostile = hostile;
        this.type = type;
        this.location = location;
        this.timer = timer;
        this.attributes = attributes;
        this.drops = drops;
    }

    public boolean isHostile() {
        return hostile;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getTimer() {
        return timer;
    }

    public List<Pair<String, Double>> getAttributes() {
        return attributes;
    }

    public List<Pair<Integer, ItemStack>> getDrops() {
        return drops;
    }
}

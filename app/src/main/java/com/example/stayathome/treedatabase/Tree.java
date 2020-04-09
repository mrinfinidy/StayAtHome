package com.example.stayathome.treedatabase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tree_table")
public class Tree {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String wifi;
    private int treeType;
    private String name;
    private int growthState;
    private boolean toPlant;


    public Tree(String wifi, int treeType, String name, int growthState) {
        this.wifi = wifi;
        this.treeType = treeType;
        this.name = name;
        this.growthState = growthState;
        this.toPlant = false;
    }

    public void setId(int id) { this.id = id; }

    public void setWifi(String wifi) { this.wifi = wifi; }

    public void setTreeType(int treeType) { this.treeType = treeType; }

    public void setName(String name) { this.name = name; }

    public void setGrowthState(int growthState) { this.growthState = growthState; }

    public int getId() { return id; }

    public String getWifi() {
        return wifi;
    }

    public int getTreeType() { return treeType; }

    public String getName() {
        return name;
    }

    public int getGrowthState() {
        return growthState;
    }

    public boolean isToPlant() {
        return toPlant;
    }

    public void setToPlant(boolean toPlant) {
        this.toPlant = toPlant;
    }
}

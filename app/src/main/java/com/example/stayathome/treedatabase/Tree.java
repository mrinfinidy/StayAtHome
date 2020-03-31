package com.example.stayathome.treedatabase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tree_table")
public class Tree {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String project;

    private String wifi;

    private String treeType;

    private String name;

    private int growthState;

    public Tree(String project, String wifi, String treeType, String name, int growthState) {
        this.project = project;
        this.wifi = wifi;
        this.treeType = treeType;
        this.name = name;
        this.growthState = growthState;
    }

    public void setId(int id) { this.id = id; }

    public void setProject(String project) { this.project = project; }

    public void setWifi(String wifi) { this.wifi = wifi; }

    public void setTreeType(String treeType) { this.treeType = treeType; }

    public void setName(String name) { this.name = name; }

    public void setGrowthState(int growthState) { this.growthState = growthState; }

    public int getId() { return id; }

    public String getProject() {
        return project;
    }

    public String getWifi() {
        return wifi;
    }

    public String getTreeType() { return treeType; }

    public String getName() {
        return name;
    }

    public int getGrowthState() {
        return growthState;
    }
}

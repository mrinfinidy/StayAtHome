package com.example.stayathome.treedatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tree_table")
public class Tree {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String project;

    private String wifi;

    private String name;

    private String growthState;

    public Tree(String project, String wifi, String name, String growthState) {
        this.project = project;
        this.wifi = wifi;
        this.name = name;
        this.growthState = growthState;
    }

    public int setId() {
        return id;
    }

    public String getProject() {
        return project;
    }

    public String getWifi() {
        return wifi;
    }

    public String getName() {
        return name;
    }

    public String getGrowthState() {
        return growthState;
    }
}

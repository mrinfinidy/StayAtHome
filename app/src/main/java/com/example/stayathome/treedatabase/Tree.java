package com.example.stayathome.treedatabase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tree_table")
public class Tree implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String project;

    private String wifi;

    private String name;

    private int growthState;

    public Tree(String project, String wifi, String name, int growthState) {
        this.project = project;
        this.wifi = wifi;
        this.name = name;
        this.growthState = growthState;
    }

    protected Tree(Parcel in) {
        id = in.readInt();
        project = in.readString();
        wifi = in.readString();
        name = in.readString();
        growthState = in.readInt();
    }

    public static final Creator<Tree> CREATOR = new Creator<Tree>() {
        @Override
        public Tree createFromParcel(Parcel in) {
            return new Tree(in);
        }

        @Override
        public Tree[] newArray(int size) {
            return new Tree[size];
        }
    };

    public void setId(int id) { this.id = id; }

    public void setProject(String project) { this.project = project; }

    public void setWifi(String wifi) { this.wifi = wifi; }

    public void setName(String name) { this.name = name; }

    public void setGrowthState(int growthState) { this.growthState = growthState; }

    public int getId() { return id; }

    public String getProject() {
        return project;
    }

    public String getWifi() {
        return wifi;
    }

    public String getName() {
        return name;
    }

    public int getGrowthState() {
        return growthState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(project);
        dest.writeString(wifi);
        dest.writeString(name);
        dest.writeInt(growthState);
    }
}

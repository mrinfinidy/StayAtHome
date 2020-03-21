package com.example.stayathome.treedatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

@Database(entities = {Tree.class}, version = 1)
public abstract class TreeDatabase {

    public abstract TreeDao treeDao();

}

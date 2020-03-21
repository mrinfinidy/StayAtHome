package com.example.stayathome.treedatabase;

import androidx.room.Database;

@Database(entities = {Tree.class}, version = 1)
public abstract class TreeDatabase {
    public abstract TreeDao treeDao();
}

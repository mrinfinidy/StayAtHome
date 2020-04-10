package com.example.stayathome.treedatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Tree.class}, version = 1)
public abstract class TreeDatabase extends RoomDatabase {

    public abstract TreeDao treeDao();

    private static volatile TreeDatabase treeInstance;

    static TreeDatabase getTreeDatabase(final Context context) {
        if (treeInstance == null) {
            synchronized (TreeDatabase.class) {
                if (treeInstance == null) {
                    treeInstance = Room.databaseBuilder(context.getApplicationContext(),
                            TreeDatabase.class, "tree_database")
                            .build();
                }
            }
        }
        return treeInstance;
    }
}

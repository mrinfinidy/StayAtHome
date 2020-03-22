package com.example.stayathome.treedatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Tree.class}, version = 1)
public abstract class TreeDatabase extends RoomDatabase {

    public abstract TreeDao treeDao();

    private static TreeDatabase treeDatabase;

    public static synchronized TreeDatabase getInstance(Context context) {
        if (treeDatabase == null) {
            treeDatabase = Room.databaseBuilder(context.getApplicationContext(), TreeDatabase.class, "tree_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return treeDatabase;
    }
}

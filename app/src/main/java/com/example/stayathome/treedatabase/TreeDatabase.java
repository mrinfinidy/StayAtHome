package com.example.stayathome.treedatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Tree.class}, version = 1)
public abstract class TreeDatabase extends RoomDatabase {

    public abstract TreeDao treeDao();

}

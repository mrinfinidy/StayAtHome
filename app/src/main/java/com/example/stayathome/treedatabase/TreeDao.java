package com.example.stayathome.treedatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TreeDao {

    @Insert
    void insert(Tree tree);

    @Update
    void update(Tree tree);

    @Delete
    void delete(Tree tree);

}

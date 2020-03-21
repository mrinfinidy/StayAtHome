package com.example.stayathome.treedatabase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TreeRepository {

    private TreeDao treeDao;
    private LiveData<List<Tree>> allTrees;

    public TreeRepository(Application application) {

    }
}

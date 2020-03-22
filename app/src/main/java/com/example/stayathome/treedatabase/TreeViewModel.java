package com.example.stayathome.treedatabase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TreeViewModel extends AndroidViewModel {
    private TreeRepository treeRepository;
    private LiveData<List<Tree>> allTrees;

    public TreeViewModel(@NonNull Application application) {
        super(application);
        treeRepository = new TreeRepository(application);
        allTrees = treeRepository.getTrees();
    }

    public void insert(Tree tree) {
        treeRepository.insert(tree);
    }

    public void update(Tree tree) {
        treeRepository.update(tree);
    }

    public void delete(Tree tree) {
        treeRepository.delete(tree);
    }

    public LiveData<List<Tree>> getTrees() {
        return allTrees;
    }
}

package com.example.stayathome.interfacelogic;

import android.util.Log;

import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeDBActions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TreeManager {

    private TreeDBActions treeDBActions;
    private List<Tree> allTrees;

    public TreeManager(TreeDBActions treeDBActions) {
        this.treeDBActions = treeDBActions;
    }

    public void insertTree(Tree tree) {
        TreeDBActions.insert(tree);
    }

    public void editGrowthState(Tree tree, int growthState) {
        tree.setGrowthState(growthState);
        TreeDBActions.update(tree);
    }

    public void editPlantability(Tree tree, boolean value) {
        tree.setToPlant(value);
        TreeDBActions.update(tree);
    }

    public void deleteById(int id) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            if (allTrees.get(i).getId() == id) {
                TreeDBActions.delete(allTrees.get(i));
            }
        }
    }

    public void deleteByName(String name) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            if (allTrees.get(i).getName().equals(name)) {
                TreeDBActions.delete(allTrees.get(i));
            }
        }
    }

    public void deleteAllTrees() throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            TreeDBActions.delete(allTrees.get(i));
        }
    }
}

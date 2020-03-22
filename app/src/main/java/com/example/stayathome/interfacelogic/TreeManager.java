package com.example.stayathome.interfacelogic;

import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeDBActions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TreeManager {

    TreeDBActions treeDBActions;
    List<Tree> allTrees;

    public TreeManager(TreeDBActions treeDBActions) {
        this.treeDBActions = treeDBActions;
    }

    public void insertTree(Tree tree) {
        treeDBActions.insert(tree);
    }

    public void editGrowthState(Tree tree, int growthState) {
        tree.setGrowthState(growthState);
        treeDBActions.update(tree);
    }

    public void deleteByName(String name) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            if (allTrees.get(i).getName().equals(name)) {
                treeDBActions.delete(allTrees.get(i));
            }
        }
    }

    public void deleteAllTrees() throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            treeDBActions.delete(allTrees.get(i));
        }
    }
}

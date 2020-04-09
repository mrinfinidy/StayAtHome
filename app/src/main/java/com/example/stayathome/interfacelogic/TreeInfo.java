package com.example.stayathome.interfacelogic;

import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeDBActions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TreeInfo {

    TreeDBActions treeDBActions;
    List<Tree> allTrees;

    public TreeInfo(TreeDBActions treeDBActions) {
        this.treeDBActions = treeDBActions;
    }

    public int totalTrees() throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        return allTrees.size();
    }

    public List<Tree> treesInProject(String project) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        List<Tree> inProject = null;
        for (int i = 0; i < allTrees.size(); i++) {
            if (allTrees.get(i).getName().equals(project)) {
                inProject.add(allTrees.get(i));
            }
        }
        return inProject;
    }

    public List<Tree> treesInWifi(String ssid) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        List<Tree> inWifi = new ArrayList<>();
        for (int i = 0; i < allTrees.size(); i++) {
            if(allTrees.get(i).getWifi().equals(ssid)) {
                inWifi.add(allTrees.get(i));
            }
        }
        return inWifi;
    }

    public Tree getByName(String name) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            if (allTrees.get(i).getName().equals(name)) {
                return allTrees.get(i);
            }
        }
        return null;
    }

    public int growthState(Tree tree) throws ExecutionException, InterruptedException {
        allTrees = treeDBActions.getTrees();
        for (int i = 0; i < allTrees.size(); i++) {
            if (allTrees.get(i) == tree){
                return allTrees.get(i).getGrowthState();
            }
        }
        return -1;
    }
}

package com.example.stayathome.treedatabase;

import java.util.ArrayList;
import java.util.List;

public class TreeInfo {

    public static  ArrayList<Tree> allTrees;
    public static ArrayList<Tree> treesInWifi;

    public TreeInfo() {
        allTrees = new ArrayList<>();
        treesInWifi = new ArrayList();
    }

    public void updateAllTrees(List<Tree> trees) {
        for (Tree tree : trees) {
            if (!allTrees.contains(tree)) {
                allTrees.add(tree);
            }
        }
    }

    public void updateTreesInWifi(List<Tree> trees, String ssid) {
        if (ssid != null) {
            for (Tree tree : trees) {
                if (tree.getWifi().equals(ssid) && !treesInWifi.contains(tree)) {
                    treesInWifi.add(tree);
                }
            }
        }
    }
}

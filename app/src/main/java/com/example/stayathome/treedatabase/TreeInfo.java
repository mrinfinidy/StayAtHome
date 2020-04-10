package com.example.stayathome.treedatabase;

import java.util.ArrayList;
import java.util.List;

public class TreeInfo {

    private static ArrayList<Tree> allTrees;
    private static ArrayList<Tree> treesInWifi;
    private static ArrayList<Tree> plantableTrees;

    public TreeInfo() {
        allTrees = new ArrayList<>();
        treesInWifi = new ArrayList<>();
        plantableTrees = new ArrayList<>();
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

    public void updatePlantableTrees(List<Tree> trees) {
        for (Tree tree : trees) {
            if (tree.isPlantable() && !plantableTrees.contains(tree)) {
                plantableTrees.add(tree);
            }
        }
    }

    public static ArrayList<Tree> getAllTrees() {
        return allTrees;
    }

    public static void setAllTrees(ArrayList<Tree> allTrees) {
        TreeInfo.allTrees = allTrees;
    }

    public static ArrayList<Tree> getTreesInWifi() {
        return treesInWifi;
    }

    public static void setTreesInWifi(ArrayList<Tree> treesInWifi) {
        TreeInfo.treesInWifi = treesInWifi;
    }

    public static ArrayList<Tree> getPlantableTrees() {
        return plantableTrees;
    }

    public static void setPlantableTrees(ArrayList<Tree> plantableTrees) {
        TreeInfo.plantableTrees = plantableTrees;
    }
}

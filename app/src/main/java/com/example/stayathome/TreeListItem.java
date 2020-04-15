package com.example.stayathome;

public class TreeListItem {

    private int treeListImg;
    private String treeListName;
    private String plantable;

    public TreeListItem(int treeListImg, String treeListName, String plantable) {
        this.treeListImg = treeListImg;
        this.treeListName = treeListName;
        this.plantable = plantable;
    }

    public int getTreeListImg() {
        return treeListImg;
    }

    public String getTreeListName() {
        return treeListName;
    }

    public String getPlantable() {
        return plantable;
    }
}

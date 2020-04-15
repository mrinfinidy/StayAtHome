package com.example.stayathome;

public class TreeListItem {

    private int treeListImg;
    private String treeListName;

    public TreeListItem(int treeListImg, String treeListName) {
        this.treeListImg = treeListImg;
        this.treeListName = treeListName;
    }

    public int getTreeListImg() {
        return treeListImg;
    }

    public String getTreeListName() {
        return treeListName;
    }
}

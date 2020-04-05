package com.example.stayathome.ui;

//remember values during selection process
public class HoldSelection {
    private static boolean creationPending;
    private static String wifiName;
    private static int treeType;
    private static String treeName;

    public static boolean isCreationPending() {
        return creationPending;
    }

    public static void setCreationPending(boolean creationPending) {
        HoldSelection.creationPending = creationPending;
    }

    public static String getWifiName() {
        return wifiName;
    }

    public static void setWifiName(String wifiName) {
        HoldSelection.wifiName = wifiName;
    }

    public static int getTreeType() {
        return treeType;
    }

    public static void setTreeType(int treeType) {
        HoldSelection.treeType = treeType;
    }

    public static String getTreeName() {
        return treeName;
    }

    public static void setTreeName(String treeName) {
        HoldSelection.treeName = treeName;
    }
}

package com.example.stayathome.treedatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TreeDBActions {

    private String dbName = "trees";
    private  TreeDatabase treeDatabase;

    public TreeDBActions(Context context) {
        treeDatabase = Room.databaseBuilder(context, TreeDatabase.class, dbName).build();
    }

    public List<Tree> getTrees() throws ExecutionException, InterruptedException {
        return new getAllAsyncTree(treeDatabase).execute().get();
    }

    private static class getAllAsyncTree extends android.os.AsyncTask<Void, Void, List<Tree>> {

        private TreeDatabase treeDatabase;
        List<Tree> allTrees;

        getAllAsyncTree(TreeDatabase dao) {
            treeDatabase = dao;
        }

        @Override
        protected List<Tree> doInBackground(Void... voids) {
            return treeDatabase.treeDao().getTrees();
        }
    }

    //@SuppressLint("StaticFieldLeak")
    public void insert(final Tree tree) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                treeDatabase.treeDao().insert(tree);
                return null;
            }
        }.execute();
    }

    public void delete(final Tree tree) {
        if (tree != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    treeDatabase.treeDao().delete(tree);
                    return null;
                }
            }.execute();
        }
    }

    public void update(final Tree tree) {
        if (tree != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    treeDatabase.treeDao().update(tree);
                    return null;
                }
            }.execute();
        }
    }

    public LiveData<Tree> getTree(int treeId) {
        return treeDatabase.treeDao().getTree(treeId);
    }


}

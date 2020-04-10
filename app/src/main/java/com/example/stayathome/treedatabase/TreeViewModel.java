package com.example.stayathome.treedatabase;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TreeViewModel extends AndroidViewModel {

    private String TAG = this.getClass().getSimpleName();
    private TreeDao treeDao;
    private TreeDatabase treeDatabase;
    private LiveData<List<Tree>> allTrees;

    public TreeViewModel(Application application) {
        super(application);

        treeDatabase = TreeDatabase.getTreeDatabase(application);
        treeDao = treeDatabase.treeDao();
        allTrees = treeDao.getTrees();
    }

    //get all trees in database
    public LiveData<List<Tree>> getTrees() {
        return allTrees;
    }

    //insert tree in database
    public void insert(Tree tree) {
        new InsertAsyncTask(treeDao).execute(tree);
    }

    private class InsertAsyncTask extends AsyncTask<Tree, Void, Void> {

        TreeDao treeDao;
        public InsertAsyncTask(TreeDao treeDao) {
            this.treeDao = treeDao;
        }

        @Override
        protected Void doInBackground(Tree... trees) {
            treeDao.insert(trees[0]);
            return null;
        }
    }

    //delete tree from database
    public void delete(Tree tree) {
        new DeleteAsyncTask(treeDao).execute(tree);
    }

    private class DeleteAsyncTask extends AsyncTask<Tree, Void, Void> {
        TreeDao treeDao;
        public DeleteAsyncTask(TreeDao treeDao) {
            this.treeDao = treeDao;
        }

        @Override
        protected Void doInBackground(Tree... trees) {
            treeDao.delete(trees[0]);
            return null;
        }
    }

    //update tree in database
    public void update(Tree tree) {
        new UpdateAsyncTask(treeDao).execute(tree);
    }

    private class UpdateAsyncTask extends AsyncTask<Tree, Void, Void> {
        TreeDao treeDao;
        public UpdateAsyncTask(TreeDao treeDao) {
            this.treeDao = treeDao;
        }

        @Override
        protected Void doInBackground(Tree... trees) {
            treeDao.update(trees[0]);
            return null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "ViewModel Destroyed");
    }

    public LiveData<Tree> getTree(int treeId) {
        return treeDatabase.treeDao().getTree(treeId);
    }
}

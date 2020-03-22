package com.example.stayathome.treedatabase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class TreeRepository {

    private TreeDao treeDao;
    private LiveData<List<Tree>> allTrees;

    public TreeRepository(Application application) {
        TreeDatabase treeDatabase = TreeDatabase.getInstance(application);
        treeDao = treeDatabase.treeDao();
        allTrees = treeDao.getTrees();
    }

    public void insert (Tree tree) {
        new InsertTreeAsyncTask(treeDao).execute(tree);
    }

    public void update(Tree tree) {
        new UpdateTreeAsyncTask(treeDao).execute(tree);
    }

    public void delete(Tree tree) {
        new DeleteTreeAsyncTask(treeDao).execute(tree);
    }

    public LiveData<List<Tree>> getTrees() {
        return allTrees;
    }

    private static class InsertTreeAsyncTask extends AsyncTask<Tree, Void, Void> {
        private TreeDao treeDao;

        private InsertTreeAsyncTask(TreeDao treeDao) {
            this.treeDao = treeDao;
        }

        @Override
        protected Void doInBackground(Tree... trees) {
            treeDao.insert(trees[0]);
            return null;
        }
    }

    private static class UpdateTreeAsyncTask extends AsyncTask<Tree, Void, Void> {
        private TreeDao treeDao;

        private UpdateTreeAsyncTask(TreeDao treeDao) {
            this.treeDao = treeDao;
        }

        @Override
        protected Void doInBackground(Tree... trees) {
            treeDao.update(trees[0]);
            return null;
        }
    }

    private static class DeleteTreeAsyncTask extends AsyncTask<Tree, Void, Void> {
        private TreeDao treeDao;

        private DeleteTreeAsyncTask(TreeDao treeDao) {
            this.treeDao = treeDao;
        }

        @Override
        protected Void doInBackground(Tree... trees) {
            treeDao.delete(trees[0]);
            return null;
        }
    }

}

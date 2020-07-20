package com.finder.application;

import android.os.AsyncTask;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;

import java.util.List;

public class GetMyLocations extends AsyncTask<Void, Void, List<Item>> {

    private UpdateLocationListener listener;
    private String user_id;

    public GetMyLocations(UpdateLocationListener listener, String user_id) {
        this.listener = listener;
        this.user_id = user_id;
    }

    protected List<Item> doInBackground(Void... voids) {

        AppDatabase database = App.database;
        final ItemDao itemDao = database.itemDao();
        return itemDao.getAllByUserId(user_id);
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);
        listener.OnUpdateLocations(items);
    }
}

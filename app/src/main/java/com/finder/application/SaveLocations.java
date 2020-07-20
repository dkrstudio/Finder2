package com.finder.application;

import android.os.AsyncTask;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;
import com.finder.application.model.ResponceLocation;

import java.util.ArrayList;
import java.util.List;

public class SaveLocations extends AsyncTask<Void, Void, List<Item>> {
    private final ArrayList<ResponceLocation> locations;
    private final String user_id;
    private UpdateLocationListener listener;

    public SaveLocations(ArrayList<ResponceLocation> locations, String user_id, UpdateLocationListener listener) {

        this.locations = locations;
        this.user_id = user_id;
        this.listener = listener;
    }

    @Override
    protected List<Item> doInBackground(Void... voids) {
        AppDatabase database = App.database;
        ItemDao itemDao = database.itemDao();
        Item item = null;
        for(ResponceLocation responceLocation : locations) {
            if(!responceLocation.user_id.equals(user_id)) {
                item = itemDao.get(Long.parseLong(responceLocation.unic));
                if(item == null) {
                    item = new Item();
                    item.unic = Long.parseLong(responceLocation.unic);
                    item.author_id = responceLocation.user_id;
                    item.author_avatar = responceLocation.author_avatar;
                    item.author_name = responceLocation.author_name;
                    item.title = responceLocation.title;
                    item.description = responceLocation.description;
                    item.location = responceLocation.location;
                    item.latitude = Double.parseDouble(responceLocation.latitude);
                    item.longitude = Double.parseDouble(responceLocation.longitude);
                    item.is_public = 1;
                    itemDao.Insert(item);
                } else {
                    item.author_id = responceLocation.user_id;
                    item.author_avatar = responceLocation.author_avatar;
                    item.author_name = responceLocation.author_name;
                    item.title = responceLocation.title;
                    item.description = responceLocation.description;
                    item.location = responceLocation.location;
                    item.latitude = Double.parseDouble(responceLocation.latitude);
                    item.longitude = Double.parseDouble(responceLocation.longitude);
                    item.is_public = 1;
                    itemDao.Update(item);
                }
            }
        }
        return itemDao.getAll();
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);
        listener.OnUpdateLocations(items);
    }
}

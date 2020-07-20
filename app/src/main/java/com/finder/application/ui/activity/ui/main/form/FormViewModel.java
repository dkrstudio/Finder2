package com.finder.application.ui.activity.ui.main.form;

import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.finder.application.App;
import com.finder.application.AppDatabase;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;

public class FormViewModel extends ViewModel {

    public MutableLiveData<Item> dataItem;

    public FormViewModel() {
        dataItem = new MutableLiveData<>();
    }

    public void putItem(long id) {
        new AsyncTask<Long, Void, Item>() {

            @Override
            protected Item doInBackground(Long... longs) {
                AppDatabase database = App.database;
                ItemDao itemDao = database.itemDao();
                return itemDao.get(longs[0]);
            }

            @Override
            protected void onPostExecute(Item item) {
                super.onPostExecute(item);
                if(item != null) {
                    dataItem.setValue(item);
                }
            }
        }.execute(id);
    }

    public void saveItem(String title, String description, int type, String location, double longitude, double latitude) {
        if(dataItem != null) {
            Item item = dataItem.getValue();
            boolean is_insert = false;
            if(item == null) {
                item = new Item();
                is_insert = true;
            }
            item.title = title;
            item.description = description;
            item.is_public = type;
            item.location = location;
            item.latitude = latitude;
            item.longitude = longitude;
            final boolean finalIs_insert = is_insert;
            new AsyncTask<Item, Void, Item>() {

                @Override
                protected Item doInBackground(Item... items) {
                    Item item = items[0];
                    AppDatabase database = App.database;
                    ItemDao itemDao = database.itemDao();
                    if(finalIs_insert) {
                        itemDao.Insert(item);
                    } else {
                        itemDao.Update(item);
                    }
                    return item;
                }

                @Override
                protected void onPostExecute(Item item) {
                    super.onPostExecute(item);
                    if(item != null) {
                        dataItem.setValue(item);
                    }
                }
            }.execute(item);
        }
    }
}
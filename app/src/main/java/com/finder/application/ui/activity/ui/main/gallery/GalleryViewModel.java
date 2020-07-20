package com.finder.application.ui.activity.ui.main.gallery;

import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.finder.application.App;
import com.finder.application.AppDatabase;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;

import java.util.List;

public class GalleryViewModel extends ViewModel {

    public MutableLiveData<List<Item>> dataItems;

    public GalleryViewModel() {
        dataItems = new MutableLiveData<>();
    }

    public void putList() {
        new AsyncTask<Void, Void, List<Item>>() {

            @Override
            protected List<Item> doInBackground(Void... voids) {
                AppDatabase database = App.database;
                ItemDao itemDao = database.itemDao();
                return itemDao.getAll();
            }

            @Override
            protected void onPostExecute(List<Item> items) {
                super.onPostExecute(items);
                if(items != null) {
                    dataItems.setValue(items);
                }
            }
        }.execute();
    }
}
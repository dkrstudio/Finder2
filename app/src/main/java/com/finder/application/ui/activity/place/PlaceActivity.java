package com.finder.application.ui.activity.place;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.*;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.add_place.AddPlaceActivity;
import com.finder.application.ui.activity.signin.SignInActivity;
import com.finder.application.ui.activity.ui.adapters.AdapterItem;

import java.util.List;

public class PlaceActivity extends AppCompatActivity {
    private RecyclerView recycler_view_item;
    private AdapterItem adapterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);
        App app = (App) getApplication();
        App.UserInfo user_info = app.hasUser();
    }
}

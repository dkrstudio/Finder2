package com.finder.application.ui.activity.my_place;

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

public class MyPlaceActivity extends AppCompatActivity implements ClickItemListener {
    private RecyclerView recycler_view_item;
    private AdapterItem adapterItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myplace_activity);
        App app = (App) getApplication();
        App.UserInfo user_info = app.hasUser();
        if(!user_info.is_auth) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }

        recycler_view_item = (RecyclerView) findViewById(R.id.recycler_view_item);
        recycler_view_item.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view_item.setLayoutManager(layoutManager);
        adapterItem = new AdapterItem(this);
        recycler_view_item.setAdapter(adapterItem);

        new GetMyLocations(new UpdateLocationListener() {
            @Override
            public void OnUpdateLocations(List<Item> items) {
                if (items != null) {
                    App.items = items;
                    adapterItem.notifyDataSetChanged();
                }
            }
        }, user_info.user_id).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myplace_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                App.MoveToAddPlace(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnClickItem(Long unic) {
        Intent intent = new Intent(getApplicationContext(), AddPlaceActivity.class);
        intent.putExtra("unic", unic);
        startActivity(intent);
    }
}

package com.finder.application.ui.activity.ui.adapters.holders;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.App;
import com.finder.application.AppDatabase;
import com.finder.application.ClickItemListener;
import com.finder.application.R;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.my_place.MyPlaceActivity;
import com.finder.application.ui.activity.ui.adapters.AdapterItem;

public class HolderItem extends RecyclerView.ViewHolder {
    private AdapterItem adapterItem;
    private View view_item;
    private View view_buttons;
    private TextView view_title;
    private TextView view_location;
    private CheckBox checkbox_public;
    private ImageView image_view_remove;

    public HolderItem(final AdapterItem adapterItem, final FragmentActivity activity, @NonNull View itemView) {
        super(itemView);
        this.adapterItem = adapterItem;

        view_item = itemView.findViewById(R.id.view_item);
        view_buttons = itemView.findViewById(R.id.view_buttons);
        view_title = itemView.findViewById(R.id.view_title);
        view_location = itemView.findViewById(R.id.view_location);
        checkbox_public = itemView.findViewById(R.id.checkbox_public);
        image_view_remove = itemView.findViewById(R.id.image_view_remove);
        view_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof ClickItemListener) {
                    ClickItemListener clickItemListener = (ClickItemListener) activity;
                    clickItemListener.OnClickItem(App.items.get(getPosition()).unic);
                }
            }
        });
        checkbox_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = App.items.get(getPosition());
                if (App.IsOnline(activity.getApplicationContext())) {
                    item.is_public = item.is_public == 1 ? 0 : 1;
                    if(checkbox_public.isChecked()) {
                        App.RunUpdate(activity, item);
                    } else {
                        App.Toast(activity.getApplicationContext(), "UnPublic " + item.is_public);
                        App.UnPublic(activity, item);
                    }
                }
            }
        });
        image_view_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = App.items.get(getPosition());
                App.Remove(item);
                App.items.remove(getPosition());
                adapterItem.notifyDataSetChanged();
            }
        });
    }

    public void bind() {
        view_title.setText(App.items.get(getPosition()).title);
        view_location.setText(App.items.get(getPosition()).location);
        checkbox_public.setChecked(App.items.get(getPosition()).is_public == 1);
    }
}

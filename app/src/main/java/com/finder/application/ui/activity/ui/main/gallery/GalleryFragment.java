package com.finder.application.ui.activity.ui.main.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.App;
import com.finder.application.R;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.ui.adapters.AdapterItem;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class GalleryFragment extends Fragment {
    private RecyclerView recycler_view_item;
    private AdapterItem adapterItem;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        recycler_view_item = (RecyclerView) root.findViewById(R.id.view_recycler);
        recycler_view_item.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_view_item.setLayoutManager(layoutManager);
        adapterItem = new AdapterItem(this);
        recycler_view_item.setAdapter(adapterItem);
        galleryViewModel.putList();
        galleryViewModel.dataItems.observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                App.items = items;
                adapterItem.notifyDataSetChanged();
            }
        });
        return root;
    }
}

package com.finder.application.ui.activity.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.App;
import com.finder.application.R;
import com.finder.application.ui.activity.ui.adapters.holders.HolderItem;

public class AdapterItem extends RecyclerView.Adapter<HolderItem> {

    private FragmentActivity activity;

    public AdapterItem(Fragment fragment) {
        this.activity = fragment.getActivity();
    }

    public AdapterItem(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public HolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderItem(this, activity, LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderItem holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        if(App.items == null) return 0;
        return App.items.size();
    }
}
